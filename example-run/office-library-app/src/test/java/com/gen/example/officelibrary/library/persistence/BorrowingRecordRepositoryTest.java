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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class BorrowingRecordRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BorrowingRecordRepository borrowingRecordRepository;

    @Test
    void shouldSaveAndFindBorrowingRecord() {
        // Given
        Author author = new Author("John", "Doe");
        entityManager.persistAndFlush(author);
        
        Book book = new Book("978-0123456789", "Test Book", author,
                           BookStatus.BORROWED, LocalDate.now());
        entityManager.persistAndFlush(book);
        
        BorrowingRecord record = new BorrowingRecord(
            "Jane Smith", "jane@example.com",
            LocalDate.now(), LocalDate.now().plusDays(14),
            BorrowingStatus.ACTIVE, "First borrowing", book);
        
        // When
        BorrowingRecord savedRecord = borrowingRecordRepository.save(record);
        
        // Then
        assertThat(savedRecord.getId()).isNotNull();
        assertThat(savedRecord.getBorrowerName()).isEqualTo("Jane Smith");
        assertThat(savedRecord.getBorrowerEmail()).isEqualTo("jane@example.com");
        assertThat(savedRecord.getStatus()).isEqualTo(BorrowingStatus.ACTIVE);
        assertThat(savedRecord.getNotes()).isEqualTo("First borrowing");
        assertThat(savedRecord.getBook()).isEqualTo(book);
    }

    @Test
    void shouldFindByStatus() {
        // Given
        Author author = new Author("John", "Doe");
        entityManager.persistAndFlush(author);
        
        Book book1 = new Book("978-0123456789", "Book 1", author,
                            BookStatus.BORROWED, LocalDate.now());
        Book book2 = new Book("978-0123456790", "Book 2", author,
                            BookStatus.AVAILABLE, LocalDate.now());
        entityManager.persistAndFlush(book1);
        entityManager.persistAndFlush(book2);
        
        BorrowingRecord activeRecord = new BorrowingRecord(
            "Jane Smith", "jane@example.com",
            LocalDate.now(), LocalDate.now().plusDays(14),
            BorrowingStatus.ACTIVE, book1);
        BorrowingRecord returnedRecord = new BorrowingRecord(
            "John Smith", "john@example.com",
            LocalDate.now().minusDays(30), LocalDate.now().minusDays(16),
            BorrowingStatus.RETURNED, book2);
        returnedRecord.setReturnDate(LocalDate.now().minusDays(20));
        
        entityManager.persistAndFlush(activeRecord);
        entityManager.persistAndFlush(returnedRecord);
        
        // When
        List<BorrowingRecord> activeRecords = borrowingRecordRepository.findByStatus(BorrowingStatus.ACTIVE);
        
        // Then
        assertThat(activeRecords).hasSize(1);
        assertThat(activeRecords.get(0).getBorrowerName()).isEqualTo("Jane Smith");
    }

    @Test
    void shouldFindByBook() {
        // Given
        Author author = new Author("John", "Doe");
        entityManager.persistAndFlush(author);
        
        Book book1 = new Book("978-0123456789", "Book 1", author,
                            BookStatus.AVAILABLE, LocalDate.now());
        Book book2 = new Book("978-0123456790", "Book 2", author,
                            BookStatus.AVAILABLE, LocalDate.now());
        entityManager.persistAndFlush(book1);
        entityManager.persistAndFlush(book2);
        
        BorrowingRecord record1 = new BorrowingRecord(
            "Jane Smith", "jane@example.com",
            LocalDate.now().minusDays(30), LocalDate.now().minusDays(16),
            BorrowingStatus.RETURNED, book1);
        BorrowingRecord record2 = new BorrowingRecord(
            "John Smith", "john@example.com",
            LocalDate.now().minusDays(60), LocalDate.now().minusDays(46),
            BorrowingStatus.RETURNED, book1);
        BorrowingRecord record3 = new BorrowingRecord(
            "Bob Johnson", "bob@example.com",
            LocalDate.now(), LocalDate.now().plusDays(14),
            BorrowingStatus.ACTIVE, book2);
        
        entityManager.persistAndFlush(record1);
        entityManager.persistAndFlush(record2);
        entityManager.persistAndFlush(record3);
        
        // When
        List<BorrowingRecord> book1Records = borrowingRecordRepository.findByBook(book1);
        
        // Then
        assertThat(book1Records).hasSize(2);
        assertThat(book1Records).extracting(BorrowingRecord::getBorrowerName)
            .containsExactlyInAnyOrder("Jane Smith", "John Smith");
    }

    @Test
    void shouldFindByBorrowerEmail() {
        // Given
        Author author = new Author("John", "Doe");
        entityManager.persistAndFlush(author);
        
        Book book1 = new Book("978-0123456789", "Book 1", author,
                            BookStatus.AVAILABLE, LocalDate.now());
        Book book2 = new Book("978-0123456790", "Book 2", author,
                            BookStatus.BORROWED, LocalDate.now());
        entityManager.persistAndFlush(book1);
        entityManager.persistAndFlush(book2);
        
        BorrowingRecord record1 = new BorrowingRecord(
            "Jane Smith", "jane@example.com",
            LocalDate.now().minusDays(30), LocalDate.now().minusDays(16),
            BorrowingStatus.RETURNED, book1);
        BorrowingRecord record2 = new BorrowingRecord(
            "Jane Smith", "jane@example.com",
            LocalDate.now(), LocalDate.now().plusDays(14),
            BorrowingStatus.ACTIVE, book2);
        
        entityManager.persistAndFlush(record1);
        entityManager.persistAndFlush(record2);
        
        // When
        List<BorrowingRecord> janeRecords = borrowingRecordRepository.findByBorrowerEmailIgnoreCase("JANE@EXAMPLE.COM");
        
        // Then
        assertThat(janeRecords).hasSize(2);
        assertThat(janeRecords).allMatch(record -> record.getBorrowerEmail().equals("jane@example.com"));
    }

    @Test
    void shouldFindOverdueRecords() {
        // Given
        Author author = new Author("John", "Doe");
        entityManager.persistAndFlush(author);
        
        Book book1 = new Book("978-0123456789", "Book 1", author,
                            BookStatus.BORROWED, LocalDate.now());
        Book book2 = new Book("978-0123456790", "Book 2", author,
                            BookStatus.BORROWED, LocalDate.now());
        entityManager.persistAndFlush(book1);
        entityManager.persistAndFlush(book2);
        
        BorrowingRecord overdueRecord = new BorrowingRecord(
            "Jane Smith", "jane@example.com",
            LocalDate.now().minusDays(20), LocalDate.now().minusDays(6),
            BorrowingStatus.ACTIVE, book1);
        BorrowingRecord currentRecord = new BorrowingRecord(
            "John Smith", "john@example.com",
            LocalDate.now(), LocalDate.now().plusDays(14),
            BorrowingStatus.ACTIVE, book2);
        
        entityManager.persistAndFlush(overdueRecord);
        entityManager.persistAndFlush(currentRecord);
        
        // When
        List<BorrowingRecord> overdueRecords = borrowingRecordRepository.findOverdueRecords(LocalDate.now());
        
        // Then
        assertThat(overdueRecords).hasSize(1);
        assertThat(overdueRecords.get(0).getBorrowerName()).isEqualTo("Jane Smith");
    }

    @Test
    void shouldFindActiveRecordByBookId() {
        // Given
        Author author = new Author("John", "Doe");
        entityManager.persistAndFlush(author);
        
        Book book = new Book("978-0123456789", "Test Book", author,
                           BookStatus.BORROWED, LocalDate.now());
        entityManager.persistAndFlush(book);
        
        BorrowingRecord activeRecord = new BorrowingRecord(
            "Jane Smith", "jane@example.com",
            LocalDate.now(), LocalDate.now().plusDays(14),
            BorrowingStatus.ACTIVE, book);
        entityManager.persistAndFlush(activeRecord);
        
        // When
        Optional<BorrowingRecord> foundRecord = borrowingRecordRepository.findActiveRecordByBookId(book.getId());
        
        // Then
        assertThat(foundRecord).isPresent();
        assertThat(foundRecord.get().getBorrowerName()).isEqualTo("Jane Smith");
        assertThat(foundRecord.get().getStatus()).isEqualTo(BorrowingStatus.ACTIVE);
    }

    @Test
    void shouldFindRecordsWithFilters() {
        // Given
        Author author = new Author("John", "Doe");
        entityManager.persistAndFlush(author);
        
        Book book = new Book("978-0123456789", "Test Book", author,
                           BookStatus.AVAILABLE, LocalDate.now());
        entityManager.persistAndFlush(book);
        
        BorrowingRecord record1 = new BorrowingRecord(
            "Jane Smith", "jane@example.com",
            LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 15),
            BorrowingStatus.RETURNED, book);
        BorrowingRecord record2 = new BorrowingRecord(
            "John Smith", "john@example.com",
            LocalDate.of(2023, 2, 1), LocalDate.of(2023, 2, 15),
            BorrowingStatus.RETURNED, book);
        
        entityManager.persistAndFlush(record1);
        entityManager.persistAndFlush(record2);
        
        // When
        List<BorrowingRecord> filteredRecords = borrowingRecordRepository.findRecordsWithFilters(
            BorrowingStatus.RETURNED, "jane@example.com", 
            LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 31));
        
        // Then
        assertThat(filteredRecords).hasSize(1);
        assertThat(filteredRecords.get(0).getBorrowerName()).isEqualTo("Jane Smith");
    }

    @Test
    void shouldFindBorrowingHistoryByBookId() {
        // Given
        Author author = new Author("John", "Doe");
        entityManager.persistAndFlush(author);
        
        Book book = new Book("978-0123456789", "Test Book", author,
                           BookStatus.AVAILABLE, LocalDate.now());
        entityManager.persistAndFlush(book);
        
        BorrowingRecord oldRecord = new BorrowingRecord(
            "Jane Smith", "jane@example.com",
            LocalDate.now().minusDays(60), LocalDate.now().minusDays(46),
            BorrowingStatus.RETURNED, book);
        BorrowingRecord recentRecord = new BorrowingRecord(
            "John Smith", "john@example.com",
            LocalDate.now().minusDays(30), LocalDate.now().minusDays(16),
            BorrowingStatus.RETURNED, book);
        
        entityManager.persistAndFlush(oldRecord);
        entityManager.persistAndFlush(recentRecord);
        
        // When
        List<BorrowingRecord> history = borrowingRecordRepository.findBorrowingHistoryByBookId(book.getId());
        
        // Then
        assertThat(history).hasSize(2);
        // Should be ordered by borrow date DESC (most recent first)
        assertThat(history.get(0).getBorrowerName()).isEqualTo("John Smith");
        assertThat(history.get(1).getBorrowerName()).isEqualTo("Jane Smith");
    }

    @Test
    void shouldCountActiveBorrowingsByEmail() {
        // Given
        Author author = new Author("John", "Doe");
        entityManager.persistAndFlush(author);
        
        Book book1 = new Book("978-0123456789", "Book 1", author,
                            BookStatus.BORROWED, LocalDate.now());
        Book book2 = new Book("978-0123456790", "Book 2", author,
                            BookStatus.BORROWED, LocalDate.now());
        entityManager.persistAndFlush(book1);
        entityManager.persistAndFlush(book2);
        
        BorrowingRecord activeRecord1 = new BorrowingRecord(
            "Jane Smith", "jane@example.com",
            LocalDate.now(), LocalDate.now().plusDays(14),
            BorrowingStatus.ACTIVE, book1);
        BorrowingRecord activeRecord2 = new BorrowingRecord(
            "Jane Smith", "jane@example.com",
            LocalDate.now(), LocalDate.now().plusDays(14),
            BorrowingStatus.ACTIVE, book2);
        
        entityManager.persistAndFlush(activeRecord1);
        entityManager.persistAndFlush(activeRecord2);
        
        // When
        long count = borrowingRecordRepository.countActiveBorrowingsByEmail("jane@example.com");
        
        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    void shouldDeleteBorrowingRecord() {
        // Given
        Author author = new Author("John", "Doe");
        entityManager.persistAndFlush(author);
        
        Book book = new Book("978-0123456789", "Test Book", author,
                           BookStatus.AVAILABLE, LocalDate.now());
        entityManager.persistAndFlush(book);
        
        BorrowingRecord record = new BorrowingRecord(
            "Jane Smith", "jane@example.com",
            LocalDate.now().minusDays(30), LocalDate.now().minusDays(16),
            BorrowingStatus.RETURNED, book);
        BorrowingRecord savedRecord = entityManager.persistAndFlush(record);
        
        // When
        borrowingRecordRepository.deleteById(savedRecord.getId());
        
        // Then
        Optional<BorrowingRecord> foundRecord = borrowingRecordRepository.findById(savedRecord.getId());
        assertThat(foundRecord).isEmpty();
    }
}