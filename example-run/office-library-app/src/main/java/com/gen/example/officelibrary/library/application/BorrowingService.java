package com.gen.example.officelibrary.library.application;

import com.gen.example.officelibrary.library.domain.*;
import com.gen.example.officelibrary.library.persistence.BookRepository;
import com.gen.example.officelibrary.library.persistence.BorrowingRecordRepository;
import com.gen.example.officelibrary.shared.exception.BusinessRuleException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class BorrowingService {

    private final BorrowingRecordRepository borrowingRecordRepository;
    private final BookRepository bookRepository;
    private final BorrowingRecordMapper borrowingRecordMapper;
    private final BookService bookService;

    public BorrowingService(BorrowingRecordRepository borrowingRecordRepository,
                           BookRepository bookRepository,
                           BorrowingRecordMapper borrowingRecordMapper,
                           BookService bookService) {
        this.borrowingRecordRepository = borrowingRecordRepository;
        this.bookRepository = bookRepository;
        this.borrowingRecordMapper = borrowingRecordMapper;
        this.bookService = bookService;
    }

    public BorrowingRecordDTO borrowBook(UUID bookId, BorrowBookRequest request) {
        log.info("Processing book borrowing request for book: {} by: {}", bookId, request.getBorrowerEmail());
        
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        
        // Check if book is available
        if (!book.isAvailable()) {
            throw new BusinessRuleException("Book is not available for borrowing");
        }
        
        // Check if there's already an active borrowing record for this book
        if (borrowingRecordRepository.existsByBookAndStatus(book, BorrowingStatus.ACTIVE)) {
            throw new BusinessRuleException("Book is already borrowed");
        }
        
        // Check borrowing limits (e.g., max 3 active borrowings per user)
        long activeBorrowings = borrowingRecordRepository.countActiveBorrowingsByEmail(request.getBorrowerEmail());
        if (activeBorrowings >= 3) {
            throw new BusinessRuleException("Maximum borrowing limit (3 books) reached for user: " + request.getBorrowerEmail());
        }
        
        // Create borrowing record
        BorrowingRecord borrowingRecord = borrowingRecordMapper.toEntity(request, book);
        BorrowingRecord savedRecord = borrowingRecordRepository.save(borrowingRecord);
        
        // Mark book as borrowed
        bookService.markAsBorrowed(bookId);
        
        log.info("Successfully created borrowing record with id: {}", savedRecord.getId());
        return borrowingRecordMapper.toDto(savedRecord);
    }

    public BorrowingRecordDTO returnBook(UUID borrowingRecordId) {
        log.info("Processing book return for borrowing record: {}", borrowingRecordId);
        
        BorrowingRecord borrowingRecord = borrowingRecordRepository.findById(borrowingRecordId)
                .orElseThrow(() -> new BorrowingRecordNotFoundException(borrowingRecordId));
        
        // Check if record is active
        if (!borrowingRecord.isActive()) {
            throw new BusinessRuleException("Borrowing record is not active");
        }
        
        // Mark record as returned
        borrowingRecord.markAsReturned(LocalDate.now());
        BorrowingRecord updatedRecord = borrowingRecordRepository.save(borrowingRecord);
        
        // Mark book as available
        bookService.markAsAvailable(borrowingRecord.getBook().getId());
        
        log.info("Successfully returned book for borrowing record: {}", borrowingRecordId);
        return borrowingRecordMapper.toDto(updatedRecord);
    }

    @Transactional(readOnly = true)
    public List<BorrowingRecordDTO> findAll() {
        log.debug("Retrieving all borrowing records");
        return borrowingRecordRepository.findAll()
                .stream()
                .map(borrowingRecordMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BorrowingRecordDTO findById(UUID id) {
        log.debug("Retrieving borrowing record with id: {}", id);
        BorrowingRecord record = borrowingRecordRepository.findById(id)
                .orElseThrow(() -> new BorrowingRecordNotFoundException(id));
        return borrowingRecordMapper.toDto(record);
    }

    @Transactional(readOnly = true)
    public List<BorrowingRecordDTO> findByStatus(BorrowingStatus status) {
        log.debug("Finding borrowing records by status: {}", status);
        return borrowingRecordRepository.findByStatus(status)
                .stream()
                .map(borrowingRecordMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BorrowingRecordDTO> findByBorrowerEmail(String email) {
        log.debug("Finding borrowing records by borrower email: {}", email);
        return borrowingRecordRepository.findByBorrowerEmailIgnoreCase(email)
                .stream()
                .map(borrowingRecordMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BorrowingRecordDTO> findOverdueRecords() {
        log.debug("Finding overdue borrowing records");
        return borrowingRecordRepository.findOverdueRecords(LocalDate.now())
                .stream()
                .map(borrowingRecordMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BorrowingRecordDTO> findBorrowingHistoryByBookId(UUID bookId) {
        log.debug("Finding borrowing history for book: {}", bookId);
        return borrowingRecordRepository.findBorrowingHistoryByBookId(bookId)
                .stream()
                .map(borrowingRecordMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BorrowingRecordDTO> findRecordsWithFilters(BorrowingStatus status, String borrowerEmail, 
                                                          LocalDate fromDate, LocalDate toDate) {
        log.debug("Finding borrowing records with filters - status: {}, email: {}, from: {}, to: {}", 
                 status, borrowerEmail, fromDate, toDate);
        return borrowingRecordRepository.findRecordsWithFilters(status, borrowerEmail, fromDate, toDate)
                .stream()
                .map(borrowingRecordMapper::toDto)
                .collect(Collectors.toList());
    }

    public void markOverdueRecords() {
        log.info("Marking overdue borrowing records");
        List<BorrowingRecord> overdueRecords = borrowingRecordRepository.findOverdueRecords(LocalDate.now());
        
        for (BorrowingRecord record : overdueRecords) {
            if (record.getStatus() == BorrowingStatus.ACTIVE) {
                record.markAsOverdue();
                borrowingRecordRepository.save(record);
                log.debug("Marked borrowing record {} as overdue", record.getId());
            }
        }
        
        log.info("Processed {} overdue records", overdueRecords.size());
    }
}