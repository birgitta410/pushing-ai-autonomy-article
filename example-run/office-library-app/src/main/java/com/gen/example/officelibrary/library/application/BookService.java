package com.gen.example.officelibrary.library.application;

import com.gen.example.officelibrary.author.domain.Author;
import com.gen.example.officelibrary.author.domain.AuthorNotFoundException;
import com.gen.example.officelibrary.author.persistence.AuthorRepository;
import com.gen.example.officelibrary.library.domain.*;
import com.gen.example.officelibrary.library.persistence.BookRepository;
import com.gen.example.officelibrary.shared.exception.BusinessRuleException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookMapper bookMapper;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.bookMapper = bookMapper;
    }

    public BookDTO create(CreateBookRequest request) {
        log.info("Creating new book: {}", request.getTitle());
        
        // Check if ISBN already exists
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new BusinessRuleException("Book with ISBN " + request.getIsbn() + " already exists");
        }
        
        // Validate author exists
        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new AuthorNotFoundException(request.getAuthorId()));
        
        Book book = bookMapper.toEntity(request, author);
        Book savedBook = bookRepository.save(book);
        
        log.info("Successfully created book with id: {}", savedBook.getId());
        return bookMapper.toDto(savedBook);
    }

    @Transactional(readOnly = true)
    public List<BookDTO> findAll() {
        log.debug("Retrieving all books");
        return bookRepository.findAll()
                .stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BookDTO findById(UUID id) {
        log.debug("Retrieving book with id: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        return bookMapper.toDto(book);
    }

    @Transactional(readOnly = true)
    public BookDTO findByIsbn(String isbn) {
        log.debug("Retrieving book with ISBN: {}", isbn);
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException("Book with ISBN " + isbn + " not found"));
        return bookMapper.toDto(book);
    }

    @Transactional(readOnly = true)
    public List<BookDTO> findByStatus(BookStatus status) {
        log.debug("Finding books by status: {}", status);
        return bookRepository.findByStatus(status)
                .stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookDTO> findByAuthorId(UUID authorId) {
        log.debug("Finding books by author id: {}", authorId);
        return bookRepository.findByAuthor_Id(authorId)
                .stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookDTO> searchBooks(String searchTerm) {
        log.debug("Searching books with term: {}", searchTerm);
        return bookRepository.searchBooks(searchTerm)
                .stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookDTO> findBooksWithFilters(BookStatus status, String genre, UUID authorId) {
        log.debug("Finding books with filters - status: {}, genre: {}, authorId: {}", status, genre, authorId);
        return bookRepository.findBooksWithFilters(status, genre, authorId)
                .stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookDTO> findAvailableBooks() {
        log.debug("Finding available books");
        return findByStatus(BookStatus.AVAILABLE);
    }

    public BookDTO update(UUID id, UpdateBookRequest request) {
        log.info("Updating book with id: {}", id);
        
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        
        // Check if ISBN is being changed and if new ISBN already exists
        if (!request.getIsbn().equals(book.getIsbn()) && 
            bookRepository.existsByIsbn(request.getIsbn())) {
            throw new BusinessRuleException("Book with ISBN " + request.getIsbn() + " already exists");
        }
        
        // Validate author exists
        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new AuthorNotFoundException(request.getAuthorId()));
        
        bookMapper.updateEntity(book, request, author);
        Book updatedBook = bookRepository.save(book);
        
        log.info("Successfully updated book with id: {}", id);
        return bookMapper.toDto(updatedBook);
    }

    public void deleteById(UUID id) {
        log.info("Deleting book with id: {}", id);
        
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        
        // Check if book has active borrowing records
        if (bookRepository.hasActiveBorrowingRecords(id)) {
            throw new BusinessRuleException("Cannot delete book with active borrowing records");
        }
        
        bookRepository.deleteById(id);
        log.info("Successfully deleted book with id: {}", id);
    }

    public void markAsBorrowed(UUID bookId) {
        log.info("Marking book as borrowed: {}", bookId);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        
        if (!book.isAvailable()) {
            throw new BusinessRuleException("Book is not available for borrowing");
        }
        
        book.markAsBorrowed();
        bookRepository.save(book);
        log.info("Successfully marked book as borrowed: {}", bookId);
    }

    public void markAsAvailable(UUID bookId) {
        log.info("Marking book as available: {}", bookId);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        
        book.markAsAvailable();
        bookRepository.save(book);
        log.info("Successfully marked book as available: {}", bookId);
    }
}