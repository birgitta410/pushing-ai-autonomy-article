package com.gen.example.officelibrary.library.domain;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingRecordDTO {

    private UUID id;

    @NotNull
    @Size(min = 1, max = 255)
    private String borrowerName;

    @NotNull
    @Email
    @Size(max = 255)
    private String borrowerEmail;

    @NotNull
    private LocalDate borrowDate;

    @NotNull
    private LocalDate dueDate;

    private LocalDate returnDate;

    @NotNull
    private BorrowingStatus status;

    @Size(max = 500)
    private String notes;

    private UUID bookId;

    // Optional book information for display purposes
    private BookDTO book;

    public static BorrowingRecordDTO fromEntity(BorrowingRecord borrowingRecord) {
        BorrowingRecordDTO dto = new BorrowingRecordDTO(
                borrowingRecord.getId(),
                borrowingRecord.getBorrowerName(),
                borrowingRecord.getBorrowerEmail(),
                borrowingRecord.getBorrowDate(),
                borrowingRecord.getDueDate(),
                borrowingRecord.getReturnDate(),
                borrowingRecord.getStatus(),
                borrowingRecord.getNotes(),
                borrowingRecord.getBook() != null ? borrowingRecord.getBook().getId() : null,
                null
        );
        
        if (borrowingRecord.getBook() != null) {
            dto.setBook(BookDTO.fromEntity(borrowingRecord.getBook()));
        }
        
        return dto;
    }

    public boolean isOverdue() {
        return status == BorrowingStatus.OVERDUE ||
               (status == BorrowingStatus.ACTIVE && dueDate != null && LocalDate.now().isAfter(dueDate));
    }

    public long getDaysOverdue() {
        if (!isOverdue()) {
            return 0;
        }
        return LocalDate.now().toEpochDay() - dueDate.toEpochDay();
    }
}