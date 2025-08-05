package com.gen.example.officelibrary.author.persistence;

import com.gen.example.officelibrary.author.domain.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuthorRepository extends JpaRepository<Author, UUID> {

    List<Author> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName, String lastName);

    @Query("SELECT a FROM Author a WHERE " +
           "LOWER(CONCAT(a.firstName, ' ', a.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Author> searchByFullName(@Param("searchTerm") String searchTerm);

    List<Author> findByNationalityIgnoreCase(String nationality);

    boolean existsByEmail(String email);
}