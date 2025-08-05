package com.gen.example.officelibrary.library.web;

import com.gen.example.officelibrary.library.application.BookService;
import com.gen.example.officelibrary.library.domain.BookDTO;
import com.gen.example.officelibrary.library.domain.BookStatus;
import com.gen.example.officelibrary.library.domain.CreateBookRequest;
import com.gen.example.officelibrary.library.domain.UpdateBookRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/books")
public class BookController {
    
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<BookDTO> create(@Valid @RequestBody CreateBookRequest request) {
        log.debug("Creating new book with request: {}", request);
        BookDTO created = bookService.create(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BookDTO>> findAll(
            @RequestParam(required = false) BookStatus status,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) UUID authorId) {
        log.debug("Fetching books with filters - status: {}, genre: {}, authorId: {}", status, genre, authorId);
        
        List<BookDTO> books;
        if (status != null || genre != null || authorId != null) {
            books = bookService.findBooksWithFilters(status, genre, authorId);
        } else {
            books = bookService.findAll();
        }
        
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> findById(@PathVariable UUID id) {
        log.debug("Fetching book with ID: {}", id);
        BookDTO book = bookService.findById(id);
        return ResponseEntity.ok(book);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> update(@PathVariable UUID id, @Valid @RequestBody UpdateBookRequest request) {
        log.debug("Updating book with ID: {}", id);
        BookDTO updated = bookService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        log.debug("Deleting book with ID: {}", id);
        bookService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookDTO>> searchBooks(@RequestParam String query) {
        log.debug("Searching books with query: {}", query);
        List<BookDTO> books = bookService.searchBooks(query);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/available")
    public ResponseEntity<List<BookDTO>> findAvailableBooks() {
        log.debug("Fetching available books");
        List<BookDTO> books = bookService.findAvailableBooks();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookDTO> findByIsbn(@PathVariable String isbn) {
        log.debug("Fetching book with ISBN: {}", isbn);
        BookDTO book = bookService.findByIsbn(isbn);
        return ResponseEntity.ok(book);
    }
}