package com.gen.example.officelibrary.library.application;

import com.gen.example.officelibrary.author.domain.Author;
import com.gen.example.officelibrary.author.domain.AuthorNotFoundException;
import com.gen.example.officelibrary.author.persistence.AuthorRepository;
import com.gen.example.officelibrary.library.domain.*;
import com.gen.example.officelibrary.library.persistence.BookRepository;
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
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    private UUID bookId;
    private UUID authorId;
    private Book book;
    private Author author;
    private BookDTO bookDTO;
    private CreateBookRequest createRequest;
    private UpdateBookRequest updateRequest;

    @BeforeEach
    void setUp() {
        bookId = UUID.randomUUID();
        authorId = UUID.randomUUID();
        
        author = new Author("John", "Doe");
        author.setId(authorId);
        
        book = new Book("978-0123456789", "Test Book", author, BookStatus.AVAILABLE, LocalDate.now());
        book.setId(bookId);
        
        bookDTO = new BookDTO(bookId, "978-0123456789", "Test Book", authorId, 
                             "Test Publisher", 2023, "Fiction", BookStatus.AVAILABLE, 
                             LocalDate.now(), "A1", null);
        
        createRequest = new CreateBookRequest("978-0123456789", "Test Book", authorId,
                                            "Test Publisher", 2023, "Fiction", "A1");
        
        updateRequest = new UpdateBookRequest("978-0987654321", "Updated Book", authorId,
                                            "Updated Publisher", 2024, "Non-Fiction", "B2");
    }

    @Test
    void create_ShouldReturnBookDTO_WhenValidRequest() {
        // Given
        when(bookRepository.existsByIsbn(createRequest.getIsbn())).thenReturn(false);
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(bookMapper.toEntity(createRequest, author)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDTO);

        // When
        BookDTO result = bookService.create(createRequest);

        // Then
        assertNotNull(result);
        assertEquals(bookDTO, result);
        verify(bookRepository).existsByIsbn(createRequest.getIsbn());
        verify(authorRepository).findById(authorId);
        verify(bookMapper).toEntity(createRequest, author);
        verify(bookRepository).save(book);
        verify(bookMapper).toDto(book);
    }

    @Test
    void create_ShouldThrowBusinessRuleException_WhenIsbnAlreadyExists() {
        // Given
        when(bookRepository.existsByIsbn(createRequest.getIsbn())).thenReturn(true);

        // When & Then
        assertThrows(BusinessRuleException.class, () -> bookService.create(createRequest));
        verify(bookRepository).existsByIsbn(createRequest.getIsbn());
        verify(authorRepository, never()).findById(any());
        verify(bookMapper, never()).toEntity(any(), any());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void create_ShouldThrowAuthorNotFoundException_WhenAuthorDoesNotExist() {
        // Given
        when(bookRepository.existsByIsbn(createRequest.getIsbn())).thenReturn(false);
        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(AuthorNotFoundException.class, () -> bookService.create(createRequest));
        verify(bookRepository).existsByIsbn(createRequest.getIsbn());
        verify(authorRepository).findById(authorId);
        verify(bookMapper, never()).toEntity(any(), any());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void findAll_ShouldReturnListOfBookDTOs() {
        // Given
        List<Book> books = Arrays.asList(book);
        when(bookRepository.findAll()).thenReturn(books);
        when(bookMapper.toDto(book)).thenReturn(bookDTO);

        // When
        List<BookDTO> result = bookService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookDTO, result.get(0));
        verify(bookRepository).findAll();
        verify(bookMapper).toDto(book);
    }

    @Test
    void findById_ShouldReturnBookDTO_WhenBookExists() {
        // Given
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDTO);

        // When
        BookDTO result = bookService.findById(bookId);

        // Then
        assertNotNull(result);
        assertEquals(bookDTO, result);
        verify(bookRepository).findById(bookId);
        verify(bookMapper).toDto(book);
    }

    @Test
    void findById_ShouldThrowBookNotFoundException_WhenBookDoesNotExist() {
        // Given
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BookNotFoundException.class, () -> bookService.findById(bookId));
        verify(bookRepository).findById(bookId);
        verify(bookMapper, never()).toDto(any());
    }

    @Test
    void findByIsbn_ShouldReturnBookDTO_WhenBookExists() {
        // Given
        String isbn = "978-0123456789";
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDTO);

        // When
        BookDTO result = bookService.findByIsbn(isbn);

        // Then
        assertNotNull(result);
        assertEquals(bookDTO, result);
        verify(bookRepository).findByIsbn(isbn);
        verify(bookMapper).toDto(book);
    }

    @Test
    void findByIsbn_ShouldThrowBookNotFoundException_WhenBookDoesNotExist() {
        // Given
        String isbn = "978-0123456789";
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BookNotFoundException.class, () -> bookService.findByIsbn(isbn));
        verify(bookRepository).findByIsbn(isbn);
        verify(bookMapper, never()).toDto(any());
    }

    @Test
    void findByStatus_ShouldReturnListOfBookDTOs() {
        // Given
        BookStatus status = BookStatus.AVAILABLE;
        List<Book> books = Arrays.asList(book);
        when(bookRepository.findByStatus(status)).thenReturn(books);
        when(bookMapper.toDto(book)).thenReturn(bookDTO);

        // When
        List<BookDTO> result = bookService.findByStatus(status);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookDTO, result.get(0));
        verify(bookRepository).findByStatus(status);
        verify(bookMapper).toDto(book);
    }

    @Test
    void findByAuthorId_ShouldReturnListOfBookDTOs() {
        // Given
        List<Book> books = Arrays.asList(book);
        when(bookRepository.findByAuthor_Id(authorId)).thenReturn(books);
        when(bookMapper.toDto(book)).thenReturn(bookDTO);

        // When
        List<BookDTO> result = bookService.findByAuthorId(authorId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookDTO, result.get(0));
        verify(bookRepository).findByAuthor_Id(authorId);
        verify(bookMapper).toDto(book);
    }

    @Test
    void searchBooks_ShouldReturnListOfBookDTOs() {
        // Given
        String searchTerm = "Test";
        List<Book> books = Arrays.asList(book);
        when(bookRepository.searchBooks(searchTerm)).thenReturn(books);
        when(bookMapper.toDto(book)).thenReturn(bookDTO);

        // When
        List<BookDTO> result = bookService.searchBooks(searchTerm);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookDTO, result.get(0));
        verify(bookRepository).searchBooks(searchTerm);
        verify(bookMapper).toDto(book);
    }

    @Test
    void update_ShouldReturnUpdatedBookDTO_WhenBookExists() {
        // Given
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookRepository.existsByIsbn(updateRequest.getIsbn())).thenReturn(false);
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDTO);

        // When
        BookDTO result = bookService.update(bookId, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals(bookDTO, result);
        verify(bookRepository).findById(bookId);
        verify(bookRepository).existsByIsbn(updateRequest.getIsbn());
        verify(authorRepository).findById(authorId);
        verify(bookMapper).updateEntity(book, updateRequest, author);
        verify(bookRepository).save(book);
        verify(bookMapper).toDto(book);
    }

    @Test
    void update_ShouldThrowBookNotFoundException_WhenBookDoesNotExist() {
        // Given
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BookNotFoundException.class, () -> bookService.update(bookId, updateRequest));
        verify(bookRepository).findById(bookId);
        verify(bookRepository, never()).existsByIsbn(any());
        verify(authorRepository, never()).findById(any());
        verify(bookMapper, never()).updateEntity(any(), any(), any());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void update_ShouldThrowBusinessRuleException_WhenIsbnAlreadyExists() {
        // Given
        book.setIsbn("978-0000000000");
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookRepository.existsByIsbn(updateRequest.getIsbn())).thenReturn(true);

        // When & Then
        assertThrows(BusinessRuleException.class, () -> bookService.update(bookId, updateRequest));
        verify(bookRepository).findById(bookId);
        verify(bookRepository).existsByIsbn(updateRequest.getIsbn());
        verify(authorRepository, never()).findById(any());
        verify(bookMapper, never()).updateEntity(any(), any(), any());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void deleteById_ShouldDeleteBook_WhenBookExistsAndNoActiveBorrowings() {
        // Given
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookRepository.hasActiveBorrowingRecords(bookId)).thenReturn(false);

        // When
        bookService.deleteById(bookId);

        // Then
        verify(bookRepository).findById(bookId);
        verify(bookRepository).hasActiveBorrowingRecords(bookId);
        verify(bookRepository).deleteById(bookId);
    }

    @Test
    void deleteById_ShouldThrowBookNotFoundException_WhenBookDoesNotExist() {
        // Given
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BookNotFoundException.class, () -> bookService.deleteById(bookId));
        verify(bookRepository).findById(bookId);
        verify(bookRepository, never()).hasActiveBorrowingRecords(any());
        verify(bookRepository, never()).deleteById(any());
    }

    @Test
    void deleteById_ShouldThrowBusinessRuleException_WhenBookHasActiveBorrowings() {
        // Given
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookRepository.hasActiveBorrowingRecords(bookId)).thenReturn(true);

        // When & Then
        assertThrows(BusinessRuleException.class, () -> bookService.deleteById(bookId));
        verify(bookRepository).findById(bookId);
        verify(bookRepository).hasActiveBorrowingRecords(bookId);
        verify(bookRepository, never()).deleteById(any());
    }

    @Test
    void markAsBorrowed_ShouldUpdateBookStatus_WhenBookIsAvailable() {
        // Given
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);

        // When
        bookService.markAsBorrowed(bookId);

        // Then
        verify(bookRepository).findById(bookId);
        assertEquals(BookStatus.BORROWED, book.getStatus());
        verify(bookRepository).save(book);
    }

    @Test
    void markAsBorrowed_ShouldThrowBusinessRuleException_WhenBookIsNotAvailable() {
        // Given
        book.setStatus(BookStatus.BORROWED);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // When & Then
        assertThrows(BusinessRuleException.class, () -> bookService.markAsBorrowed(bookId));
        verify(bookRepository).findById(bookId);
        verify(bookRepository, never()).save(any());
    }

    @Test
    void markAsAvailable_ShouldUpdateBookStatus() {
        // Given
        book.setStatus(BookStatus.BORROWED); // Set initial state
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);

        // When
        bookService.markAsAvailable(bookId);

        // Then
        verify(bookRepository).findById(bookId);
        assertEquals(BookStatus.AVAILABLE, book.getStatus());
        verify(bookRepository).save(book);
    }
}