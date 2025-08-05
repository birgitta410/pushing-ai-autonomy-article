package com.gen.example.officelibrary.library.domain;

import com.gen.example.officelibrary.author.domain.Author;
import com.gen.example.officelibrary.author.domain.AuthorDTO;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {

    private UUID id;

    @NotNull
    @Size(min = 10, max = 17)
    private String isbn;

    @NotNull
    @Size(min = 1, max = 255)
    private String title;

    @NotNull
    private UUID authorId;

    @Size(max = 255)
    private String publisher;

    @Min(1000)
    @Max(9999)
    private Integer publicationYear;

    @Size(max = 100)
    private String genre;

    @NotNull
    private BookStatus status;

    @NotNull
    private LocalDate dateAdded;

    @Size(max = 100)
    private String location;

    // Optional author information for display purposes
    private AuthorDTO author;

    public static BookDTO fromEntity(Book book) {
        BookDTO dto = new BookDTO(
                book.getId(),
                book.getIsbn(),
                book.getTitle(),
                book.getAuthorId(),
                book.getPublisher(),
                book.getPublicationYear(),
                book.getGenre(),
                book.getStatus(),
                book.getDateAdded(),
                book.getLocation(),
                null
        );
        
        if (book.getAuthor() != null) {
            dto.setAuthor(AuthorDTO.fromEntity(book.getAuthor()));
        }
        
        return dto;
    }

    public Book toEntity(Author author) {
        return new Book(isbn, title, author, publisher, publicationYear,
                       genre, status, dateAdded, location);
    }
}