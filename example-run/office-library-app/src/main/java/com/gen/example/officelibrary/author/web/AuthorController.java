package com.gen.example.officelibrary.author.web;

import com.gen.example.officelibrary.author.application.AuthorService;
import com.gen.example.officelibrary.author.domain.AuthorDTO;
import com.gen.example.officelibrary.author.domain.CreateAuthorRequest;
import com.gen.example.officelibrary.author.domain.UpdateAuthorRequest;
import com.gen.example.officelibrary.library.application.BookService;
import com.gen.example.officelibrary.library.domain.BookDTO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/authors")
public class AuthorController {
    
    private final AuthorService authorService;
    private final BookService bookService;

    public AuthorController(AuthorService authorService, BookService bookService) {
        this.authorService = authorService;
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<AuthorDTO> create(@Valid @RequestBody CreateAuthorRequest request) {
        log.debug("Creating new author with request: {}", request);
        AuthorDTO created = authorService.create(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AuthorDTO>> findAll(
            @RequestParam(required = false) String nationality) {
        log.debug("Fetching authors with nationality filter: {}", nationality);
        
        List<AuthorDTO> authors;
        if (nationality != null && !nationality.trim().isEmpty()) {
            authors = authorService.findByNationality(nationality);
        } else {
            authors = authorService.findAll();
        }
        
        return ResponseEntity.ok(authors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDTO> findById(@PathVariable UUID id) {
        log.debug("Fetching author with ID: {}", id);
        AuthorDTO author = authorService.findById(id);
        return ResponseEntity.ok(author);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorDTO> update(@PathVariable UUID id, @Valid @RequestBody UpdateAuthorRequest request) {
        log.debug("Updating author with ID: {}", id);
        AuthorDTO updated = authorService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        log.debug("Deleting author with ID: {}", id);
        authorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<AuthorDTO>> searchByName(@RequestParam String query) {
        log.debug("Searching authors by name: {}", query);
        List<AuthorDTO> authors = authorService.searchByName(query);
        return ResponseEntity.ok(authors);
    }

    @GetMapping("/{id}/books")
    public ResponseEntity<List<BookDTO>> findBooksByAuthor(@PathVariable UUID id) {
        log.debug("Fetching books for author: {}", id);
        List<BookDTO> books = bookService.findByAuthorId(id);
        return ResponseEntity.ok(books);
    }
}