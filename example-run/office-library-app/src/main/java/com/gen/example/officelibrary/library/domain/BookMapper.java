package com.gen.example.officelibrary.library.domain;

import com.gen.example.officelibrary.author.domain.Author;
import com.gen.example.officelibrary.author.domain.AuthorDTO;
import com.gen.example.officelibrary.author.domain.AuthorMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class BookMapper {

    private final AuthorMapper authorMapper;

    public BookMapper(AuthorMapper authorMapper) {
        this.authorMapper = authorMapper;
    }

    public BookDTO toDto(Book book) {
        if (book == null) {
            return null;
        }
        
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
            dto.setAuthor(authorMapper.toDto(book.getAuthor()));
        }
        
        return dto;
    }

    public Book toEntity(CreateBookRequest request, Author author) {
        if (request == null) {
            return null;
        }
        
        return new Book(
            request.getIsbn(),
            request.getTitle(),
            author,
            request.getPublisher(),
            request.getPublicationYear(),
            request.getGenre(),
            BookStatus.AVAILABLE,
            LocalDate.now(),
            request.getLocation()
        );
    }

    public void updateEntity(Book book, UpdateBookRequest request, Author author) {
        if (book == null || request == null) {
            return;
        }
        
        book.setIsbn(request.getIsbn());
        book.setTitle(request.getTitle());
        book.setAuthor(author);
        book.setPublisher(request.getPublisher());
        book.setPublicationYear(request.getPublicationYear());
        book.setGenre(request.getGenre());
        book.setLocation(request.getLocation());
    }
}