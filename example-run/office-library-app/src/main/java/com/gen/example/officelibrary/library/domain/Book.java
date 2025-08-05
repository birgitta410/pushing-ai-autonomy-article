package com.gen.example.officelibrary.library.domain;

import com.gen.example.officelibrary.author.domain.Author;
import com.gen.example.officelibrary.shared.domain.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
public class Book extends BaseEntity {

    @Column(nullable = false, unique = true)
    @NotNull
    @Size(min = 10, max = 17)
    private String isbn;

    @Column(nullable = false)
    @NotNull
    @Size(min = 1, max = 255)
    private String title;


    @Column(length = 255)
    @Size(max = 255)
    private String publisher;

    @Column
    @Min(1000)
    @Max(9999)
    private Integer publicationYear;

    @Column(length = 100)
    @Size(max = 100)
    private String genre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private BookStatus status;

    @Column(nullable = false)
    @NotNull
    private LocalDate dateAdded;

    @Column(length = 100)
    @Size(max = 100)
    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Author author;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BorrowingRecord> borrowingRecords = new ArrayList<>();

    public Book(String isbn, String title, Author author, BookStatus status, LocalDate dateAdded) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.status = status;
        this.dateAdded = dateAdded;
    }

    public Book(String isbn, String title, Author author, String publisher,
                Integer publicationYear, String genre, BookStatus status,
                LocalDate dateAdded, String location) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.genre = genre;
        this.status = status;
        this.dateAdded = dateAdded;
        this.location = location;
    }

    public UUID getAuthorId() {
        return author != null ? author.getId() : null;
    }

    public boolean isAvailable() {
        return status == BookStatus.AVAILABLE;
    }

    public boolean isBorrowed() {
        return status == BookStatus.BORROWED;
    }

    public void markAsBorrowed() {
        this.status = BookStatus.BORROWED;
    }

    public void markAsAvailable() {
        this.status = BookStatus.AVAILABLE;
    }
}