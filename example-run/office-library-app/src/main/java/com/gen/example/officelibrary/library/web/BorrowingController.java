package com.gen.example.officelibrary.library.web;

import com.gen.example.officelibrary.library.application.BorrowingService;
import com.gen.example.officelibrary.library.domain.BorrowBookRequest;
import com.gen.example.officelibrary.library.domain.BorrowingRecordDTO;
import com.gen.example.officelibrary.library.domain.BorrowingStatus;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api")
public class BorrowingController {
    
    private final BorrowingService borrowingService;

    public BorrowingController(BorrowingService borrowingService) {
        this.borrowingService = borrowingService;
    }

    @PostMapping("/books/{bookId}/borrow")
    public ResponseEntity<BorrowingRecordDTO> borrowBook(
            @PathVariable UUID bookId, 
            @Valid @RequestBody BorrowBookRequest request) {
        log.debug("Processing borrow request for book: {} by: {}", bookId, request.getBorrowerEmail());
        BorrowingRecordDTO borrowingRecord = borrowingService.borrowBook(bookId, request);
        return new ResponseEntity<>(borrowingRecord, HttpStatus.CREATED);
    }

    @PutMapping("/borrowing-records/{id}/return")
    public ResponseEntity<BorrowingRecordDTO> returnBook(@PathVariable UUID id) {
        log.debug("Processing return request for borrowing record: {}", id);
        BorrowingRecordDTO borrowingRecord = borrowingService.returnBook(id);
        return ResponseEntity.ok(borrowingRecord);
    }

    @GetMapping("/borrowing-records")
    public ResponseEntity<List<BorrowingRecordDTO>> findAll(
            @RequestParam(required = false) BorrowingStatus status,
            @RequestParam(required = false) String borrowerEmail,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        log.debug("Fetching borrowing records with filters - status: {}, email: {}, from: {}, to: {}", 
                 status, borrowerEmail, fromDate, toDate);
        
        List<BorrowingRecordDTO> records;
        if (status != null || borrowerEmail != null || fromDate != null || toDate != null) {
            records = borrowingService.findRecordsWithFilters(status, borrowerEmail, fromDate, toDate);
        } else {
            records = borrowingService.findAll();
        }
        
        return ResponseEntity.ok(records);
    }

    @GetMapping("/borrowing-records/{id}")
    public ResponseEntity<BorrowingRecordDTO> findById(@PathVariable UUID id) {
        log.debug("Fetching borrowing record with ID: {}", id);
        BorrowingRecordDTO record = borrowingService.findById(id);
        return ResponseEntity.ok(record);
    }

    @GetMapping("/borrowing-records/overdue")
    public ResponseEntity<List<BorrowingRecordDTO>> findOverdueRecords() {
        log.debug("Fetching overdue borrowing records");
        List<BorrowingRecordDTO> records = borrowingService.findOverdueRecords();
        return ResponseEntity.ok(records);
    }

    @GetMapping("/borrowing-records/by-borrower")
    public ResponseEntity<List<BorrowingRecordDTO>> findByBorrowerEmail(@RequestParam String email) {
        log.debug("Fetching borrowing records for borrower: {}", email);
        List<BorrowingRecordDTO> records = borrowingService.findByBorrowerEmail(email);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/books/{bookId}/borrowing-history")
    public ResponseEntity<List<BorrowingRecordDTO>> findBorrowingHistoryByBookId(@PathVariable UUID bookId) {
        log.debug("Fetching borrowing history for book: {}", bookId);
        List<BorrowingRecordDTO> records = borrowingService.findBorrowingHistoryByBookId(bookId);
        return ResponseEntity.ok(records);
    }
}