package com.gen.example.officelibrary.library.domain;

import com.gen.example.officelibrary.shared.domain.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "borrowing_records")
@Getter
@Setter
@NoArgsConstructor
public class BorrowingRecord extends BaseEntity {

    @Column(nullable = false)
    @NotNull
    @Size(min = 1, max = 255)
    private String borrowerName;

    @Column(nullable = false)
    @NotNull
    @Email
    @Size(max = 255)
    private String borrowerEmail;

    @Column(nullable = false)
    @NotNull
    private LocalDate borrowDate;

    @Column(nullable = false)
    @NotNull
    private LocalDate dueDate;

    @Column
    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private BorrowingStatus status;

    @Column(length = 500)
    @Size(max = 500)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    @NotNull
    private Book book;

    public BorrowingRecord(String borrowerName, String borrowerEmail, 
                          LocalDate borrowDate, LocalDate dueDate, 
                          BorrowingStatus status, Book book) {
        this.borrowerName = borrowerName;
        this.borrowerEmail = borrowerEmail;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.status = status;
        this.book = book;
    }

    public BorrowingRecord(String borrowerName, String borrowerEmail, 
                          LocalDate borrowDate, LocalDate dueDate, 
                          BorrowingStatus status, String notes, Book book) {
        this.borrowerName = borrowerName;
        this.borrowerEmail = borrowerEmail;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.status = status;
        this.notes = notes;
        this.book = book;
    }

    public boolean isActive() {
        return status == BorrowingStatus.ACTIVE;
    }

    public boolean isOverdue() {
        return status == BorrowingStatus.OVERDUE || 
               (status == BorrowingStatus.ACTIVE && LocalDate.now().isAfter(dueDate));
    }

    public boolean isReturned() {
        return status == BorrowingStatus.RETURNED;
    }

    public void markAsReturned(LocalDate returnDate) {
        this.returnDate = returnDate;
        this.status = BorrowingStatus.RETURNED;
    }

    public void markAsOverdue() {
        this.status = BorrowingStatus.OVERDUE;
    }

    public long getDaysOverdue() {
        if (!isOverdue()) {
            return 0;
        }
        return LocalDate.now().toEpochDay() - dueDate.toEpochDay();
    }
}