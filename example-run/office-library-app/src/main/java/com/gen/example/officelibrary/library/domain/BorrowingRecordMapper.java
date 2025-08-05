package com.gen.example.officelibrary.library.domain;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class BorrowingRecordMapper {

    private final BookMapper bookMapper;

    public BorrowingRecordMapper(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
    }

    public BorrowingRecordDTO toDto(BorrowingRecord record) {
        if (record == null) {
            return null;
        }
        
        BorrowingRecordDTO dto = new BorrowingRecordDTO(
            record.getId(),
            record.getBorrowerName(),
            record.getBorrowerEmail(),
            record.getBorrowDate(),
            record.getDueDate(),
            record.getReturnDate(),
            record.getStatus(),
            record.getNotes(),
            record.getBook().getId(),
            null
        );
        
        if (record.getBook() != null) {
            dto.setBook(bookMapper.toDto(record.getBook()));
        }
        
        return dto;
    }

    public BorrowingRecord toEntity(BorrowBookRequest request, Book book) {
        if (request == null || book == null) {
            return null;
        }
        
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusWeeks(2); // Default 2 weeks borrowing period
        
        return new BorrowingRecord(
            request.getBorrowerName(),
            request.getBorrowerEmail(),
            borrowDate,
            dueDate,
            BorrowingStatus.ACTIVE,
            request.getNotes(),
            book
        );
    }
}