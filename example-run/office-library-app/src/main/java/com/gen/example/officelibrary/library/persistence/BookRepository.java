package com.gen.example.officelibrary.library.persistence;

import com.gen.example.officelibrary.library.domain.Book;
import com.gen.example.officelibrary.library.domain.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {

    List<Book> findByStatus(BookStatus status);

    List<Book> findByAuthor_Id(UUID authorId);

    Optional<Book> findByIsbn(String isbn);

    List<Book> findByGenreIgnoreCase(String genre);

    List<Book> findByPublisherIgnoreCase(String publisher);

    List<Book> findByPublicationYear(Integer publicationYear);

    @Query("SELECT b FROM Book b WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.isbn) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.publisher) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Book> searchBooks(@Param("searchTerm") String searchTerm);

    @Query("SELECT b FROM Book b WHERE " +
           "(:status IS NULL OR b.status = :status) AND " +
           "(:genre IS NULL OR LOWER(b.genre) = LOWER(:genre)) AND " +
           "(:authorId IS NULL OR b.author.id = :authorId)")
    List<Book> findBooksWithFilters(@Param("status") BookStatus status,
                                   @Param("genre") String genre,
                                   @Param("authorId") UUID authorId);

    boolean existsByIsbn(String isbn);

    @Query("SELECT COUNT(b) > 0 FROM Book b JOIN b.borrowingRecords br " +
           "WHERE b.id = :bookId AND br.status = 'ACTIVE'")
    boolean hasActiveBorrowingRecords(@Param("bookId") UUID bookId);
}