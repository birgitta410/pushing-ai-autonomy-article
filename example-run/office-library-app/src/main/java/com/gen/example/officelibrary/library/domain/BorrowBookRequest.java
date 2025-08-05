package com.gen.example.officelibrary.library.domain;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowBookRequest {

    @NotNull(message = "Borrower name is required")
    @Size(min = 1, max = 255, message = "Borrower name must be between 1 and 255 characters")
    private String borrowerName;

    @NotNull(message = "Borrower email is required")
    @Email(message = "Borrower email must be valid")
    @Size(max = 255, message = "Borrower email must not exceed 255 characters")
    private String borrowerEmail;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;

    public BorrowingRecord toEntity(Book book) {
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(14); // 14 days borrowing period as per business rules
        
        return new BorrowingRecord(
                borrowerName,
                borrowerEmail,
                borrowDate,
                dueDate,
                BorrowingStatus.ACTIVE,
                notes,
                book
        );
    }
}