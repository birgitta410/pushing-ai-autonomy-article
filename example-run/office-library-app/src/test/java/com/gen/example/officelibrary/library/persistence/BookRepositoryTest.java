package com.gen.example.officelibrary.library.persistence;

import com.gen.example.officelibrary.author.domain.Author;
import com.gen.example.officelibrary.library.domain.Book;
import com.gen.example.officelibrary.library.domain.BookStatus;
import com.gen.example.officelibrary.library.domain.BorrowingRecord;
import com.gen.example.officelibrary.library.domain.BorrowingStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class BookRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookRepository bookRepository;

    @Test
    void shouldSaveAndFindBook() {
        // Given
        Author author = new Author("John", "Doe");
        entityManager.persistAndFlush(author);
        
        Book book = new Book("978-0123456789", "Test Book", author,
                           "Test Publisher", 2023, "Fiction",
                           BookStatus.AVAILABLE, LocalDate.now(), "A1");
        
        // When
        Book savedBook = bookRepository.save(book);
        
        // Then
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo("978-0123456789");
        assertThat(savedBook.getTitle()).isEqualTo("Test Book");
        assertThat(savedBook.getAuthorId()).isEqualTo(author.getId());
        assertThat(savedBook.getPublisher()).isEqualTo("Test Publisher");
        assertThat(savedBook.getPublicationYear()).isEqualTo(2023);
        assertThat(savedBook.getGenre()).isEqualTo("Fiction");
        assertThat(savedBook.getStatus()).isEqualTo(BookStatus.AVAILABLE);
        assertThat(savedBook.getLocation()).isEqualTo("A1");
    }

    @Test
    void shouldFindByStatus() {
        // Given
        Author author = new Author("John", "Doe");
        entityManager.persistAndFlush(author);
        
        Book availableBook = new Book("978-0123456789", "Available Book", author,
                                    BookStatus.AVAILABLE, LocalDate.now());
        Book borrowedBook = new Book("978-0123456790", "Borrowed Book", author,
                                   BookStatus.BORROWED, LocalDate.now());
        
        entityManager.persistAndFlush(availableBook);
        entityManager.persistAndFlush(borrowedBook);
        
        // When
        List<Book> availableBooks = bookRepository.findByStatus(BookStatus.AVAILABLE);
        
        // Then
        assertThat(availableBooks).hasSize(1);
        assertThat(availableBooks.get(0).getTitle()).isEqualTo("Available Book");
    }

    @Test
    void shouldFindByAuthorId() {
        // Given
        Author author1 = new Author("John", "Doe");
        Author author2 = new Author("Jane", "Smith");
        entityManager.persistAndFlush(author1);
        entityManager.persistAndFlush(author2);
        
        Book book1 = new Book("978-0123456789", "Book 1", author1,
                            BookStatus.AVAILABLE, LocalDate.now());
        Book book2 = new Book("978-0123456790", "Book 2", author1,
                            BookStatus.AVAILABLE, LocalDate.now());
        Book book3 = new Book("978-0123456791", "Book 3", author2,
                            BookStatus.AVAILABLE, LocalDate.now());
        
        entityManager.persistAndFlush(book1);
        entityManager.persistAndFlush(book2);
        entityManager.persistAndFlush(book3);
        
        // When
        List<Book> author1Books = bookRepository.findByAuthor_Id(author1.getId());
        
        // Then
        assertThat(author1Books).hasSize(2);
        assertThat(author1Books).extracting(Book::getTitle)
            .containsExactlyInAnyOrder("Book 1", "Book 2");
    }

    @Test
    void shouldFindByIsbn() {
        // Given
        Author author = new Author("John", "Doe");
        entityManager.persistAndFlush(author);
        
        Book book = new Book("978-0123456789", "Test Book", author,
                           BookStatus.AVAILABLE, LocalDate.now());
        entityManager.persistAndFlush(book);
        
        // When
        Optional<Book> foundBook = bookRepository.findByIsbn("978-0123456789");
        
        // Then
        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getTitle()).isEqualTo("Test Book");
    }

    @Test
    void shouldFindByGenre() {
        // Given
        Author author = new Author("John", "Doe");
        entityManager.persistAndFlush(author);
        
        Book fictionBook = new Book("978-0123456789", "Fiction Book", author,
                                  "Publisher", 2023, "Fiction",
                                  BookStatus.AVAILABLE, LocalDate.now(), "A1");
        Book scienceBook = new Book("978-0123456790", "Science Book", author,
                                  "Publisher", 2023, "Science",
                                  BookStatus.AVAILABLE, LocalDate.now(), "B1");
        
        entityManager.persistAndFlush(fictionBook);
        entityManager.persistAndFlush(scienceBook);
        
        // When
        List<Book> fictionBooks = bookRepository.findByGenreIgnoreCase("fiction");
        
        // Then
        assertThat(fictionBooks).hasSize(1);
        assertThat(fictionBooks.get(0).getTitle()).isEqualTo("Fiction Book");
    }

    @Test
    void shouldSearchBooks() {
        // Given
        Author author = new Author("John", "Doe");
        entityManager.persistAndFlush(author);
        
        Book book1 = new Book("978-0123456789", "Java Programming", author,
                            "Tech Publisher", 2023, "Programming",
                            BookStatus.AVAILABLE, LocalDate.now(), "A1");
        Book book2 = new Book("978-0123456790", "Python Guide", author,
                            "Tech Publisher", 2023, "Programming",
                            BookStatus.AVAILABLE, LocalDate.now(), "A2");
        
        entityManager.persistAndFlush(book1);
        entityManager.persistAndFlush(book2);
        
        // When
        List<Book> javaBooks = bookRepository.searchBooks("java");
        
        // Then
        assertThat(javaBooks).hasSize(1);
        assertThat(javaBooks.get(0).getTitle()).isEqualTo("Java Programming");
    }

    @Test
    void shouldFindBooksWithFilters() {
        // Given
        Author author = new Author("John", "Doe");
        entityManager.persistAndFlush(author);
        
        Book book1 = new Book("978-0123456789", "Available Fiction", author,
                            "Publisher", 2023, "Fiction",
                            BookStatus.AVAILABLE, LocalDate.now(), "A1");
        Book book2 = new Book("978-0123456790", "Borrowed Fiction", author,
                            "Publisher", 2023, "Fiction",
                            BookStatus.BORROWED, LocalDate.now(), "A2");
        Book book3 = new Book("978-0123456791", "Available Science", author,
                            "Publisher", 2023, "Science",
                            BookStatus.AVAILABLE, LocalDate.now(), "B1");
        
        entityManager.persistAndFlush(book1);
        entityManager.persistAndFlush(book2);
        entityManager.persistAndFlush(book3);
        
        // When
        List<Book> availableFictionBooks = bookRepository.findBooksWithFilters(
            BookStatus.AVAILABLE, "Fiction", null);
        
        // Then
        assertThat(availableFictionBooks).hasSize(1);
        assertThat(availableFictionBooks.get(0).getTitle()).isEqualTo("Available Fiction");
    }

    @Test
    void shouldCheckIfIsbnExists() {
        // Given
        Author author = new Author("John", "Doe");
        entityManager.persistAndFlush(author);
        
        Book book = new Book("978-0123456789", "Test Book", author,
                           BookStatus.AVAILABLE, LocalDate.now());
        entityManager.persistAndFlush(book);
        
        // When & Then
        assertThat(bookRepository.existsByIsbn("978-0123456789")).isTrue();
        assertThat(bookRepository.existsByIsbn("978-0123456790")).isFalse();
    }

    @Test
    void shouldCheckForActiveBorrowingRecords() {
        // Given
        Author author = new Author("John", "Doe");
        entityManager.persistAndFlush(author);
        
        Book book = new Book("978-0123456789", "Test Book", author,
                           BookStatus.BORROWED, LocalDate.now());
        entityManager.persistAndFlush(book);
        
        BorrowingRecord activeRecord = new BorrowingRecord(
            "John Smith", "john@example.com", 
            LocalDate.now(), LocalDate.now().plusDays(14),
            BorrowingStatus.ACTIVE, book);
        entityManager.persistAndFlush(activeRecord);
        
        // When & Then
        assertThat(bookRepository.hasActiveBorrowingRecords(book.getId())).isTrue();
    }

    @Test
    void shouldDeleteBook() {
        // Given
        Author author = new Author("John", "Doe");
        entityManager.persistAndFlush(author);
        
        Book book = new Book("978-0123456789", "Test Book", author,
                           BookStatus.AVAILABLE, LocalDate.now());
        Book savedBook = entityManager.persistAndFlush(book);
        
        // When
        bookRepository.deleteById(savedBook.getId());
        
        // Then
        Optional<Book> foundBook = bookRepository.findById(savedBook.getId());
        assertThat(foundBook).isEmpty();
    }
}