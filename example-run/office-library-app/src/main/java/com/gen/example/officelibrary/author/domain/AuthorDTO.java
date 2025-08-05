package com.gen.example.officelibrary.author.domain;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDTO {

    private UUID id;

    @NotNull
    @Size(min = 1, max = 100)
    private String firstName;

    @NotNull
    @Size(min = 1, max = 100)
    private String lastName;

    @Size(max = 1000)
    private String biography;

    private LocalDate birthDate;

    @Size(max = 100)
    private String nationality;

    @Email
    @Size(max = 255)
    private String email;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public static AuthorDTO fromEntity(Author author) {
        return new AuthorDTO(
                author.getId(),
                author.getFirstName(),
                author.getLastName(),
                author.getBiography(),
                author.getBirthDate(),
                author.getNationality(),
                author.getEmail()
        );
    }

    public Author toEntity() {
        Author author = new Author(firstName, lastName, biography, birthDate, nationality, email);
        author.setId(this.id);
        return author;
    }
}