package com.gen.example.officelibrary.author.persistence;

import com.gen.example.officelibrary.author.domain.Author;
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
class AuthorRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    void shouldSaveAndFindAuthor() {
        // Given
        Author author = new Author("John", "Doe", "Famous author", 
                                  LocalDate.of(1970, 1, 1), "American", "john.doe@example.com");
        
        // When
        Author savedAuthor = authorRepository.save(author);
        
        // Then
        assertThat(savedAuthor.getId()).isNotNull();
        assertThat(savedAuthor.getFirstName()).isEqualTo("John");
        assertThat(savedAuthor.getLastName()).isEqualTo("Doe");
        assertThat(savedAuthor.getBiography()).isEqualTo("Famous author");
        assertThat(savedAuthor.getBirthDate()).isEqualTo(LocalDate.of(1970, 1, 1));
        assertThat(savedAuthor.getNationality()).isEqualTo("American");
        assertThat(savedAuthor.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void shouldFindByFirstNameOrLastNameContaining() {
        // Given
        Author author1 = new Author("John", "Smith");
        Author author2 = new Author("Jane", "Doe");
        Author author3 = new Author("Bob", "Johnson");
        
        entityManager.persistAndFlush(author1);
        entityManager.persistAndFlush(author2);
        entityManager.persistAndFlush(author3);
        
        // When
        List<Author> johnsAuthors = authorRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase("john", "john");
        
        // Then
        assertThat(johnsAuthors).hasSize(2);
        assertThat(johnsAuthors).extracting(Author::getFirstName)
            .containsExactlyInAnyOrder("John", "Bob");
    }

    @Test
    void shouldSearchByFullName() {
        // Given
        Author author1 = new Author("John", "Smith");
        Author author2 = new Author("Jane", "Doe");
        Author author3 = new Author("John", "Doe");
        
        entityManager.persistAndFlush(author1);
        entityManager.persistAndFlush(author2);
        entityManager.persistAndFlush(author3);
        
        // When
        List<Author> johnDoeAuthors = authorRepository.searchByFullName("john doe");
        
        // Then
        assertThat(johnDoeAuthors).hasSize(1);
        assertThat(johnDoeAuthors.get(0).getFirstName()).isEqualTo("John");
        assertThat(johnDoeAuthors.get(0).getLastName()).isEqualTo("Doe");
    }

    @Test
    void shouldFindByNationality() {
        // Given
        Author author1 = new Author("John", "Smith", null, null, "American", null);
        Author author2 = new Author("Jane", "Doe", null, null, "British", null);
        Author author3 = new Author("Bob", "Johnson", null, null, "american", null);
        
        entityManager.persistAndFlush(author1);
        entityManager.persistAndFlush(author2);
        entityManager.persistAndFlush(author3);
        
        // When
        List<Author> americanAuthors = authorRepository.findByNationalityIgnoreCase("American");
        
        // Then
        assertThat(americanAuthors).hasSize(2);
        assertThat(americanAuthors).extracting(Author::getFirstName)
            .containsExactlyInAnyOrder("John", "Bob");
    }

    @Test
    void shouldCheckIfEmailExists() {
        // Given
        Author author = new Author("John", "Doe", null, null, null, "john.doe@example.com");
        entityManager.persistAndFlush(author);
        
        // When & Then
        assertThat(authorRepository.existsByEmail("john.doe@example.com")).isTrue();
        assertThat(authorRepository.existsByEmail("jane.doe@example.com")).isFalse();
    }

    @Test
    void shouldFindAllAuthors() {
        // Given
        Author author1 = new Author("John", "Smith");
        Author author2 = new Author("Jane", "Doe");
        
        entityManager.persistAndFlush(author1);
        entityManager.persistAndFlush(author2);
        
        // When
        List<Author> authors = authorRepository.findAll();
        
        // Then
        assertThat(authors).hasSize(2);
        assertThat(authors).extracting(Author::getFirstName)
            .containsExactlyInAnyOrder("John", "Jane");
    }

    @Test
    void shouldDeleteAuthor() {
        // Given
        Author author = new Author("John", "Doe");
        Author savedAuthor = entityManager.persistAndFlush(author);
        
        // When
        authorRepository.deleteById(savedAuthor.getId());
        
        // Then
        Optional<Author> foundAuthor = authorRepository.findById(savedAuthor.getId());
        assertThat(foundAuthor).isEmpty();
    }

    @Test
    void shouldReturnEmptyListWhenNoAuthorsMatchSearch() {
        // Given
        Author author = new Author("John", "Smith");
        entityManager.persistAndFlush(author);
        
        // When
        List<Author> results = authorRepository.searchByFullName("nonexistent author");
        
        // Then
        assertThat(results).isEmpty();
    }
}