package com.gen.example.officelibrary.shared.domain;

import com.gen.example.officelibrary.author.domain.Author;
import com.gen.example.officelibrary.author.domain.AuthorDTO;
import com.gen.example.officelibrary.author.domain.CreateAuthorRequest;
import com.gen.example.officelibrary.author.domain.UpdateAuthorRequest;
import com.gen.example.officelibrary.library.domain.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DTOMappingTest {

    @Test
    void shouldMapAuthorToAuthorDTO() {
        // Given
        Author author = new Author("John", "Doe", "Famous author", 
                                  LocalDate.of(1970, 1, 1), "American", "john.doe@example.com");
        author.setId(UUID.randomUUID());
        
        // When
        AuthorDTO dto = AuthorDTO.fromEntity(author);
        
        // Then
        assertThat(dto.getId()).isEqualTo(author.getId());
        assertThat(dto.getFirstName()).isEqualTo("John");
        assertThat(dto.getLastName()).isEqualTo("Doe");
        assertThat(dto.getBiography()).isEqualTo("Famous author");
        assertThat(dto.getBirthDate()).isEqualTo(LocalDate.of(1970, 1, 1));
        assertThat(dto.getNationality()).isEqualTo("American");
        assertThat(dto.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(dto.getFullName()).isEqualTo("John Doe");
    }

    @Test
    void shouldMapAuthorDTOToAuthor() {
        // Given
        UUID authorId = UUID.randomUUID();
        AuthorDTO dto = new AuthorDTO(authorId, "Jane", "Smith", "Bestselling author", 
                                     LocalDate.of(1980, 5, 15), "British", "jane.smith@example.com");
        
        // When
        Author author = dto.toEntity();
        
        // Then
        assertThat(author.getId()).isEqualTo(authorId);
        assertThat(author.getFirstName()).isEqualTo("Jane");
        assertThat(author.getLastName()).isEqualTo("Smith");
        assertThat(author.getBiography()).isEqualTo("Bestselling author");
        assertThat(author.getBirthDate()).isEqualTo(LocalDate.of(1980, 5, 15));
        assertThat(author.getNationality()).isEqualTo("British");
        assertThat(author.getEmail()).isEqualTo("jane.smith@example.com");
    }

    @Test
    void shouldMapCreateAuthorRequestToAuthor() {
        // Given
        CreateAuthorRequest request = new CreateAuthorRequest("Bob", "Wilson", "New author", 
                                                             LocalDate.of(1990, 3, 10), "Canadian", "bob.wilson@example.com");
        
        // When
        Author author = request.toEntity();
        
        // Then
        assertThat(author.getFirstName()).isEqualTo("Bob");
        assertThat(author.getLastName()).isEqualTo("Wilson");
        assertThat(author.getBiography()).isEqualTo("New author");
        assertThat(author.getBirthDate()).isEqualTo(LocalDate.of(1990, 3, 10));
        assertThat(author.getNationality()).isEqualTo("Canadian");
        assertThat(author.getEmail()).isEqualTo("bob.wilson@example.com");
    }

    @Test
    void shouldUpdateAuthorFromUpdateAuthorRequest() {
        // Given
        Author author = new Author("Old", "Name");
        UpdateAuthorRequest request = new UpdateAuthorRequest("New", "Name", "Updated bio", 
                                                             LocalDate.of(1985, 7, 20), "Australian", "new.name@example.com");
        
        // When
        request.updateEntity(author);
        
        // Then
        assertThat(author.getFirstName()).isEqualTo("New");
        assertThat(author.getLastName()).isEqualTo("Name");
        assertThat(author.getBiography()).isEqualTo("Updated bio");
        assertThat(author.getBirthDate()).isEqualTo(LocalDate.of(1985, 7, 20));
        assertThat(author.getNationality()).isEqualTo("Australian");
        assertThat(author.getEmail()).isEqualTo("new.name@example.com");
    }

    @Test
    void shouldMapBookToBookDTO() {
        // Given
        Author author = new Author("Test", "Author");
        author.setId(UUID.randomUUID());
        Book book = new Book("978-0123456789", "Test Book", author, "Test Publisher",
                           2023, "Fiction", BookStatus.AVAILABLE, LocalDate.now(), "A1");
        book.setId(UUID.randomUUID());
        
        // When
        BookDTO dto = BookDTO.fromEntity(book);
        
        // Then
        assertThat(dto.getId()).isEqualTo(book.getId());
        assertThat(dto.getIsbn()).isEqualTo("978-0123456789");
        assertThat(dto.getTitle()).isEqualTo("Test Book");
        assertThat(dto.getAuthorId()).isEqualTo(author.getId());
        assertThat(dto.getPublisher()).isEqualTo("Test Publisher");
        assertThat(dto.getPublicationYear()).isEqualTo(2023);
        assertThat(dto.getGenre()).isEqualTo("Fiction");
        assertThat(dto.getStatus()).isEqualTo(BookStatus.AVAILABLE);
        assertThat(dto.getLocation()).isEqualTo("A1");
    }

    @Test
    void shouldMapCreateBookRequestToBook() {
        // Given
        UUID authorId = UUID.randomUUID();
        Author author = new Author("Test", "Author");
        author.setId(authorId);
        CreateBookRequest request = new CreateBookRequest("978-0123456789", "New Book", authorId,
                                                        "New Publisher", 2024, "Science", "B2");
        
        // When
        Book book = request.toEntity(author);
        
        // Then
        assertThat(book.getIsbn()).isEqualTo("978-0123456789");
        assertThat(book.getTitle()).isEqualTo("New Book");
        assertThat(book.getAuthorId()).isEqualTo(authorId);
        assertThat(book.getPublisher()).isEqualTo("New Publisher");
        assertThat(book.getPublicationYear()).isEqualTo(2024);
        assertThat(book.getGenre()).isEqualTo("Science");
        assertThat(book.getStatus()).isEqualTo(BookStatus.AVAILABLE);
        assertThat(book.getDateAdded()).isEqualTo(LocalDate.now());
        assertThat(book.getLocation()).isEqualTo("B2");
    }

    @Test
    void shouldUpdateBookFromUpdateBookRequest() {
        // Given
        UUID oldAuthorId = UUID.randomUUID();
        UUID newAuthorId = UUID.randomUUID();
        Author oldAuthor = new Author("Old", "Author");
        oldAuthor.setId(oldAuthorId);
        Author newAuthor = new Author("New", "Author");
        newAuthor.setId(newAuthorId);
        Book book = new Book("978-0000000000", "Old Title", oldAuthor, BookStatus.AVAILABLE, LocalDate.now());
        UpdateBookRequest request = new UpdateBookRequest("978-1111111111", "New Title", newAuthorId,
                                                         "New Publisher", 2024, "Mystery", "C3");
        
        // When
        request.updateEntity(book, newAuthor);
        
        // Then
        assertThat(book.getIsbn()).isEqualTo("978-1111111111");
        assertThat(book.getTitle()).isEqualTo("New Title");
        assertThat(book.getAuthorId()).isEqualTo(newAuthorId);
        assertThat(book.getPublisher()).isEqualTo("New Publisher");
        assertThat(book.getPublicationYear()).isEqualTo(2024);
        assertThat(book.getGenre()).isEqualTo("Mystery");
        assertThat(book.getLocation()).isEqualTo("C3");
    }

    @Test
    void shouldMapBorrowingRecordToBorrowingRecordDTO() {
        // Given
        Author author = new Author("Test", "Author");
        author.setId(UUID.randomUUID());
        Book book = new Book("978-0123456789", "Test Book", author, BookStatus.BORROWED, LocalDate.now());
        book.setId(UUID.randomUUID());
        
        BorrowingRecord record = new BorrowingRecord("John Smith", "john@example.com", 
                                                   LocalDate.now(), LocalDate.now().plusDays(14),
                                                   BorrowingStatus.ACTIVE, "Test notes", book);
        record.setId(UUID.randomUUID());
        
        // When
        BorrowingRecordDTO dto = BorrowingRecordDTO.fromEntity(record);
        
        // Then
        assertThat(dto.getId()).isEqualTo(record.getId());
        assertThat(dto.getBorrowerName()).isEqualTo("John Smith");
        assertThat(dto.getBorrowerEmail()).isEqualTo("john@example.com");
        assertThat(dto.getBorrowDate()).isEqualTo(record.getBorrowDate());
        assertThat(dto.getDueDate()).isEqualTo(record.getDueDate());
        assertThat(dto.getStatus()).isEqualTo(BorrowingStatus.ACTIVE);
        assertThat(dto.getNotes()).isEqualTo("Test notes");
        assertThat(dto.getBookId()).isEqualTo(book.getId());
    }

    @Test
    void shouldMapBorrowBookRequestToBorrowingRecord() {
        // Given
        Author author = new Author("Test", "Author");
        author.setId(UUID.randomUUID());
        Book book = new Book("978-0123456789", "Test Book", author, BookStatus.AVAILABLE, LocalDate.now());
        BorrowBookRequest request = new BorrowBookRequest("Jane Doe", "jane@example.com", "Urgent request");
        
        // When
        BorrowingRecord record = request.toEntity(book);
        
        // Then
        assertThat(record.getBorrowerName()).isEqualTo("Jane Doe");
        assertThat(record.getBorrowerEmail()).isEqualTo("jane@example.com");
        assertThat(record.getBorrowDate()).isEqualTo(LocalDate.now());
        assertThat(record.getDueDate()).isEqualTo(LocalDate.now().plusDays(14));
        assertThat(record.getStatus()).isEqualTo(BorrowingStatus.ACTIVE);
        assertThat(record.getNotes()).isEqualTo("Urgent request");
        assertThat(record.getBook()).isEqualTo(book);
    }

    @Test
    void shouldCalculateOverdueDaysCorrectly() {
        // Given
        LocalDate pastDueDate = LocalDate.now().minusDays(5);
        BorrowingRecordDTO overdueDto = new BorrowingRecordDTO();
        overdueDto.setStatus(BorrowingStatus.ACTIVE);
        overdueDto.setDueDate(pastDueDate);
        
        BorrowingRecordDTO currentDto = new BorrowingRecordDTO();
        currentDto.setStatus(BorrowingStatus.ACTIVE);
        currentDto.setDueDate(LocalDate.now().plusDays(5));
        
        // When & Then
        assertThat(overdueDto.isOverdue()).isTrue();
        assertThat(overdueDto.getDaysOverdue()).isEqualTo(5);
        
        assertThat(currentDto.isOverdue()).isFalse();
        assertThat(currentDto.getDaysOverdue()).isEqualTo(0);
    }

    @Test
    void shouldHandleBookStatusMethods() {
        // Given
        Author author = new Author("Test", "Author");
        author.setId(UUID.randomUUID());
        Book availableBook = new Book("978-0123456789", "Available Book", author, BookStatus.AVAILABLE, LocalDate.now());
        Book borrowedBook = new Book("978-0123456790", "Borrowed Book", author, BookStatus.BORROWED, LocalDate.now());
        
        // When & Then
        assertThat(availableBook.isAvailable()).isTrue();
        assertThat(availableBook.isBorrowed()).isFalse();
        
        assertThat(borrowedBook.isAvailable()).isFalse();
        assertThat(borrowedBook.isBorrowed()).isTrue();
        
        // Test status change methods
        availableBook.markAsBorrowed();
        assertThat(availableBook.getStatus()).isEqualTo(BookStatus.BORROWED);
        
        borrowedBook.markAsAvailable();
        assertThat(borrowedBook.getStatus()).isEqualTo(BookStatus.AVAILABLE);
    }

    @Test
    void shouldHandleBorrowingRecordStatusMethods() {
        // Given
        Author author = new Author("Test", "Author");
        author.setId(UUID.randomUUID());
        Book book = new Book("978-0123456789", "Test Book", author, BookStatus.BORROWED, LocalDate.now());
        
        BorrowingRecord activeRecord = new BorrowingRecord("John Smith", "john@example.com", 
                                                         LocalDate.now(), LocalDate.now().plusDays(14),
                                                         BorrowingStatus.ACTIVE, book);
        BorrowingRecord overdueRecord = new BorrowingRecord("Jane Smith", "jane@example.com", 
                                                          LocalDate.now().minusDays(20), LocalDate.now().minusDays(6),
                                                          BorrowingStatus.ACTIVE, book);
        
        // When & Then
        assertThat(activeRecord.isActive()).isTrue();
        assertThat(activeRecord.isOverdue()).isFalse();
        assertThat(activeRecord.isReturned()).isFalse();
        
        assertThat(overdueRecord.isOverdue()).isTrue();
        assertThat(overdueRecord.getDaysOverdue()).isEqualTo(6);
        
        // Test status change methods
        activeRecord.markAsReturned(LocalDate.now());
        assertThat(activeRecord.getStatus()).isEqualTo(BorrowingStatus.RETURNED);
        assertThat(activeRecord.getReturnDate()).isEqualTo(LocalDate.now());
        assertThat(activeRecord.isReturned()).isTrue();
        
        overdueRecord.markAsOverdue();
        assertThat(overdueRecord.getStatus()).isEqualTo(BorrowingStatus.OVERDUE);
    }
}