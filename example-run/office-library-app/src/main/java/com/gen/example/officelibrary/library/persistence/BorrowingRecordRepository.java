package com.gen.example.officelibrary.library.persistence;

import com.gen.example.officelibrary.library.domain.Book;
import com.gen.example.officelibrary.library.domain.BorrowingRecord;
import com.gen.example.officelibrary.library.domain.BorrowingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BorrowingRecordRepository extends JpaRepository<BorrowingRecord, UUID> {

    List<BorrowingRecord> findByStatus(BorrowingStatus status);

    List<BorrowingRecord> findByBook(Book book);

    List<BorrowingRecord> findByBookId(UUID bookId);

    List<BorrowingRecord> findByBorrowerEmailIgnoreCase(String borrowerEmail);

    List<BorrowingRecord> findByBorrowerNameContainingIgnoreCase(String borrowerName);

    @Query("SELECT br FROM BorrowingRecord br WHERE br.status = 'ACTIVE' AND br.dueDate < :currentDate")
    List<BorrowingRecord> findOverdueRecords(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT br FROM BorrowingRecord br WHERE br.book.id = :bookId AND br.status = 'ACTIVE'")
    Optional<BorrowingRecord> findActiveRecordByBookId(@Param("bookId") UUID bookId);

    @Query("SELECT br FROM BorrowingRecord br WHERE " +
           "(:status IS NULL OR br.status = :status) AND " +
           "(:borrowerEmail IS NULL OR LOWER(br.borrowerEmail) = LOWER(:borrowerEmail)) AND " +
           "(:fromDate IS NULL OR br.borrowDate >= :fromDate) AND " +
           "(:toDate IS NULL OR br.borrowDate <= :toDate)")
    List<BorrowingRecord> findRecordsWithFilters(@Param("status") BorrowingStatus status,
                                                @Param("borrowerEmail") String borrowerEmail,
                                                @Param("fromDate") LocalDate fromDate,
                                                @Param("toDate") LocalDate toDate);

    @Query("SELECT br FROM BorrowingRecord br WHERE br.book.id = :bookId ORDER BY br.borrowDate DESC")
    List<BorrowingRecord> findBorrowingHistoryByBookId(@Param("bookId") UUID bookId);

    boolean existsByBookAndStatus(Book book, BorrowingStatus status);

    @Query("SELECT COUNT(br) FROM BorrowingRecord br WHERE br.borrowerEmail = :email AND br.status = 'ACTIVE'")
    long countActiveBorrowingsByEmail(@Param("email") String email);
}