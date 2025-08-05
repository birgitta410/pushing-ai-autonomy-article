package com.gen.example.officelibrary.library.application;

import com.gen.example.officelibrary.author.domain.Author;
import com.gen.example.officelibrary.library.domain.*;
import com.gen.example.officelibrary.library.persistence.BookRepository;
import com.gen.example.officelibrary.library.persistence.BorrowingRecordRepository;
import com.gen.example.officelibrary.shared.exception.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowingServiceTest {

    @Mock
    private BorrowingRecordRepository borrowingRecordRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BorrowingRecordMapper borrowingRecordMapper;

    @Mock
    private BookService bookService;

    @InjectMocks
    private BorrowingService borrowingService;

    private UUID bookId;
    private UUID borrowingRecordId;
    private Book book;
    private Author author;
    private BorrowingRecord borrowingRecord;
    private BorrowingRecordDTO borrowingRecordDTO;
    private BorrowBookRequest borrowBookRequest;

    @BeforeEach
    void setUp() {
        bookId = UUID.randomUUID();
        borrowingRecordId = UUID.randomUUID();
        
        author = new Author("John", "Doe");
        author.setId(UUID.randomUUID());
        
        book = new Book("978-0123456789", "Test Book", author, BookStatus.AVAILABLE, LocalDate.now());
        book.setId(bookId);
        
        borrowingRecord = new BorrowingRecord("Jane Smith", "jane@example.com",
                                            LocalDate.now(), LocalDate.now().plusWeeks(2),
                                            BorrowingStatus.ACTIVE, book);
        borrowingRecord.setId(borrowingRecordId);
        
        borrowingRecordDTO = new BorrowingRecordDTO(borrowingRecordId, "Jane Smith", "jane@example.com",
                                                  LocalDate.now(), LocalDate.now().plusWeeks(2), null,
                                                  BorrowingStatus.ACTIVE, null, bookId, null);
        
        borrowBookRequest = new BorrowBookRequest("Jane Smith", "jane@example.com", "Test notes");
    }

    @Test
    void borrowBook_ShouldReturnBorrowingRecordDTO_WhenValidRequest() {
        // Given
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(borrowingRecordRepository.existsByBookAndStatus(book, BorrowingStatus.ACTIVE)).thenReturn(false);
        when(borrowingRecordRepository.countActiveBorrowingsByEmail(borrowBookRequest.getBorrowerEmail())).thenReturn(0L);
        when(borrowingRecordMapper.toEntity(borrowBookRequest, book)).thenReturn(borrowingRecord);
        when(borrowingRecordRepository.save(borrowingRecord)).thenReturn(borrowingRecord);
        when(borrowingRecordMapper.toDto(borrowingRecord)).thenReturn(borrowingRecordDTO);

        // When
        BorrowingRecordDTO result = borrowingService.borrowBook(bookId, borrowBookRequest);

        // Then
        assertNotNull(result);
        assertEquals(borrowingRecordDTO, result);
        verify(bookRepository).findById(bookId);
        verify(borrowingRecordRepository).existsByBookAndStatus(book, BorrowingStatus.ACTIVE);
        verify(borrowingRecordRepository).countActiveBorrowingsByEmail(borrowBookRequest.getBorrowerEmail());
        verify(borrowingRecordMapper).toEntity(borrowBookRequest, book);
        verify(borrowingRecordRepository).save(borrowingRecord);
        verify(bookService).markAsBorrowed(bookId);
        verify(borrowingRecordMapper).toDto(borrowingRecord);
    }

    @Test
    void borrowBook_ShouldThrowBookNotFoundException_WhenBookDoesNotExist() {
        // Given
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BookNotFoundException.class, () -> borrowingService.borrowBook(bookId, borrowBookRequest));
        verify(bookRepository).findById(bookId);
        verify(borrowingRecordRepository, never()).existsByBookAndStatus(any(), any());
        verify(borrowingRecordRepository, never()).save(any());
        verify(bookService, never()).markAsBorrowed(any());
    }

    @Test
    void borrowBook_ShouldThrowBusinessRuleException_WhenBookIsNotAvailable() {
        // Given
        book.setStatus(BookStatus.BORROWED);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // When & Then
        assertThrows(BusinessRuleException.class, () -> borrowingService.borrowBook(bookId, borrowBookRequest));
        verify(bookRepository).findById(bookId);
        verify(borrowingRecordRepository, never()).existsByBookAndStatus(any(), any());
        verify(borrowingRecordRepository, never()).save(any());
        verify(bookService, never()).markAsBorrowed(any());
    }

    @Test
    void borrowBook_ShouldThrowBusinessRuleException_WhenBookAlreadyBorrowed() {
        // Given
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(borrowingRecordRepository.existsByBookAndStatus(book, BorrowingStatus.ACTIVE)).thenReturn(true);

        // When & Then
        assertThrows(BusinessRuleException.class, () -> borrowingService.borrowBook(bookId, borrowBookRequest));
        verify(bookRepository).findById(bookId);
        verify(borrowingRecordRepository).existsByBookAndStatus(book, BorrowingStatus.ACTIVE);
        verify(borrowingRecordRepository, never()).countActiveBorrowingsByEmail(any());
        verify(borrowingRecordRepository, never()).save(any());
        verify(bookService, never()).markAsBorrowed(any());
    }

    @Test
    void borrowBook_ShouldThrowBusinessRuleException_WhenBorrowingLimitReached() {
        // Given
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(borrowingRecordRepository.existsByBookAndStatus(book, BorrowingStatus.ACTIVE)).thenReturn(false);
        when(borrowingRecordRepository.countActiveBorrowingsByEmail(borrowBookRequest.getBorrowerEmail())).thenReturn(3L);

        // When & Then
        assertThrows(BusinessRuleException.class, () -> borrowingService.borrowBook(bookId, borrowBookRequest));
        verify(bookRepository).findById(bookId);
        verify(borrowingRecordRepository).existsByBookAndStatus(book, BorrowingStatus.ACTIVE);
        verify(borrowingRecordRepository).countActiveBorrowingsByEmail(borrowBookRequest.getBorrowerEmail());
        verify(borrowingRecordRepository, never()).save(any());
        verify(bookService, never()).markAsBorrowed(any());
    }

    @Test
    void returnBook_ShouldReturnBorrowingRecordDTO_WhenValidRequest() {
        // Given
        when(borrowingRecordRepository.findById(borrowingRecordId)).thenReturn(Optional.of(borrowingRecord));
        when(borrowingRecordRepository.save(borrowingRecord)).thenReturn(borrowingRecord);
        when(borrowingRecordMapper.toDto(borrowingRecord)).thenReturn(borrowingRecordDTO);

        // When
        BorrowingRecordDTO result = borrowingService.returnBook(borrowingRecordId);

        // Then
        assertNotNull(result);
        assertEquals(borrowingRecordDTO, result);
        verify(borrowingRecordRepository).findById(borrowingRecordId);
        assertEquals(BorrowingStatus.RETURNED, borrowingRecord.getStatus());
        assertNotNull(borrowingRecord.getReturnDate());
        verify(borrowingRecordRepository).save(borrowingRecord);
        verify(bookService).markAsAvailable(book.getId());
        verify(borrowingRecordMapper).toDto(borrowingRecord);
    }

    @Test
    void returnBook_ShouldThrowBorrowingRecordNotFoundException_WhenRecordDoesNotExist() {
        // Given
        when(borrowingRecordRepository.findById(borrowingRecordId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BorrowingRecordNotFoundException.class, () -> borrowingService.returnBook(borrowingRecordId));
        verify(borrowingRecordRepository).findById(borrowingRecordId);
        verify(borrowingRecordRepository, never()).save(any());
        verify(bookService, never()).markAsAvailable(any());
    }

    @Test
    void returnBook_ShouldThrowBusinessRuleException_WhenRecordIsNotActive() {
        // Given
        borrowingRecord.setStatus(BorrowingStatus.RETURNED);
        when(borrowingRecordRepository.findById(borrowingRecordId)).thenReturn(Optional.of(borrowingRecord));

        // When & Then
        assertThrows(BusinessRuleException.class, () -> borrowingService.returnBook(borrowingRecordId));
        verify(borrowingRecordRepository).findById(borrowingRecordId);
        verify(borrowingRecordRepository, never()).save(any());
        verify(bookService, never()).markAsAvailable(any());
    }

    @Test
    void findAll_ShouldReturnListOfBorrowingRecordDTOs() {
        // Given
        List<BorrowingRecord> records = Arrays.asList(borrowingRecord);
        when(borrowingRecordRepository.findAll()).thenReturn(records);
        when(borrowingRecordMapper.toDto(borrowingRecord)).thenReturn(borrowingRecordDTO);

        // When
        List<BorrowingRecordDTO> result = borrowingService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(borrowingRecordDTO, result.get(0));
        verify(borrowingRecordRepository).findAll();
        verify(borrowingRecordMapper).toDto(borrowingRecord);
    }

    @Test
    void findById_ShouldReturnBorrowingRecordDTO_WhenRecordExists() {
        // Given
        when(borrowingRecordRepository.findById(borrowingRecordId)).thenReturn(Optional.of(borrowingRecord));
        when(borrowingRecordMapper.toDto(borrowingRecord)).thenReturn(borrowingRecordDTO);

        // When
        BorrowingRecordDTO result = borrowingService.findById(borrowingRecordId);

        // Then
        assertNotNull(result);
        assertEquals(borrowingRecordDTO, result);
        verify(borrowingRecordRepository).findById(borrowingRecordId);
        verify(borrowingRecordMapper).toDto(borrowingRecord);
    }

    @Test
    void findById_ShouldThrowBorrowingRecordNotFoundException_WhenRecordDoesNotExist() {
        // Given
        when(borrowingRecordRepository.findById(borrowingRecordId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BorrowingRecordNotFoundException.class, () -> borrowingService.findById(borrowingRecordId));
        verify(borrowingRecordRepository).findById(borrowingRecordId);
        verify(borrowingRecordMapper, never()).toDto(any());
    }

    @Test
    void findByStatus_ShouldReturnListOfBorrowingRecordDTOs() {
        // Given
        BorrowingStatus status = BorrowingStatus.ACTIVE;
        List<BorrowingRecord> records = Arrays.asList(borrowingRecord);
        when(borrowingRecordRepository.findByStatus(status)).thenReturn(records);
        when(borrowingRecordMapper.toDto(borrowingRecord)).thenReturn(borrowingRecordDTO);

        // When
        List<BorrowingRecordDTO> result = borrowingService.findByStatus(status);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(borrowingRecordDTO, result.get(0));
        verify(borrowingRecordRepository).findByStatus(status);
        verify(borrowingRecordMapper).toDto(borrowingRecord);
    }

    @Test
    void findByBorrowerEmail_ShouldReturnListOfBorrowingRecordDTOs() {
        // Given
        String email = "jane@example.com";
        List<BorrowingRecord> records = Arrays.asList(borrowingRecord);
        when(borrowingRecordRepository.findByBorrowerEmailIgnoreCase(email)).thenReturn(records);
        when(borrowingRecordMapper.toDto(borrowingRecord)).thenReturn(borrowingRecordDTO);

        // When
        List<BorrowingRecordDTO> result = borrowingService.findByBorrowerEmail(email);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(borrowingRecordDTO, result.get(0));
        verify(borrowingRecordRepository).findByBorrowerEmailIgnoreCase(email);
        verify(borrowingRecordMapper).toDto(borrowingRecord);
    }

    @Test
    void findOverdueRecords_ShouldReturnListOfBorrowingRecordDTOs() {
        // Given
        List<BorrowingRecord> records = Arrays.asList(borrowingRecord);
        when(borrowingRecordRepository.findOverdueRecords(any(LocalDate.class))).thenReturn(records);
        when(borrowingRecordMapper.toDto(borrowingRecord)).thenReturn(borrowingRecordDTO);

        // When
        List<BorrowingRecordDTO> result = borrowingService.findOverdueRecords();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(borrowingRecordDTO, result.get(0));
        verify(borrowingRecordRepository).findOverdueRecords(any(LocalDate.class));
        verify(borrowingRecordMapper).toDto(borrowingRecord);
    }

    @Test
    void findBorrowingHistoryByBookId_ShouldReturnListOfBorrowingRecordDTOs() {
        // Given
        List<BorrowingRecord> records = Arrays.asList(borrowingRecord);
        when(borrowingRecordRepository.findBorrowingHistoryByBookId(bookId)).thenReturn(records);
        when(borrowingRecordMapper.toDto(borrowingRecord)).thenReturn(borrowingRecordDTO);

        // When
        List<BorrowingRecordDTO> result = borrowingService.findBorrowingHistoryByBookId(bookId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(borrowingRecordDTO, result.get(0));
        verify(borrowingRecordRepository).findBorrowingHistoryByBookId(bookId);
        verify(borrowingRecordMapper).toDto(borrowingRecord);
    }

    @Test
    void markOverdueRecords_ShouldMarkActiveRecordsAsOverdue() {
        // Given
        List<BorrowingRecord> overdueRecords = Arrays.asList(borrowingRecord);
        when(borrowingRecordRepository.findOverdueRecords(any(LocalDate.class))).thenReturn(overdueRecords);
        when(borrowingRecordRepository.save(borrowingRecord)).thenReturn(borrowingRecord);

        // When
        borrowingService.markOverdueRecords();

        // Then
        verify(borrowingRecordRepository).findOverdueRecords(any(LocalDate.class));
        assertEquals(BorrowingStatus.OVERDUE, borrowingRecord.getStatus());
        verify(borrowingRecordRepository).save(borrowingRecord);
    }

    @Test
    void markOverdueRecords_ShouldNotMarkAlreadyOverdueRecords() {
        // Given
        borrowingRecord.setStatus(BorrowingStatus.OVERDUE);
        List<BorrowingRecord> overdueRecords = Arrays.asList(borrowingRecord);
        when(borrowingRecordRepository.findOverdueRecords(any(LocalDate.class))).thenReturn(overdueRecords);

        // When
        borrowingService.markOverdueRecords();

        // Then
        verify(borrowingRecordRepository).findOverdueRecords(any(LocalDate.class));
        assertEquals(BorrowingStatus.OVERDUE, borrowingRecord.getStatus()); // Should remain OVERDUE
        verify(borrowingRecordRepository, never()).save(borrowingRecord);
    }
}