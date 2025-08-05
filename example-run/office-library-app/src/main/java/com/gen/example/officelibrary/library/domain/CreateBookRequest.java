package com.gen.example.officelibrary.library.domain;

import com.gen.example.officelibrary.author.domain.Author;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookRequest {

    @NotNull(message = "ISBN is required")
    @Size(min = 10, max = 17, message = "ISBN must be between 10 and 17 characters")
    private String isbn;

    @NotNull(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;

    @NotNull(message = "Author ID is required")
    private UUID authorId;

    @Size(max = 255, message = "Publisher must not exceed 255 characters")
    private String publisher;

    @Min(value = 1000, message = "Publication year must be at least 1000")
    @Max(value = 9999, message = "Publication year must not exceed 9999")
    private Integer publicationYear;

    @Size(max = 100, message = "Genre must not exceed 100 characters")
    private String genre;

    @Size(max = 100, message = "Location must not exceed 100 characters")
    private String location;

    public Book toEntity(Author author) {
        return new Book(isbn, title, author, publisher, publicationYear,
                       genre, BookStatus.AVAILABLE, LocalDate.now(), location);
    }
}