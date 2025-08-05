package com.gen.example.officelibrary.author.domain;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAuthorRequest {

    @NotNull(message = "First name is required")
    @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
    private String firstName;

    @NotNull(message = "Last name is required")
    @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
    private String lastName;

    @Size(max = 1000, message = "Biography must not exceed 1000 characters")
    private String biography;

    private LocalDate birthDate;

    @Size(max = 100, message = "Nationality must not exceed 100 characters")
    private String nationality;

    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    public void updateEntity(Author author) {
        author.setFirstName(this.firstName);
        author.setLastName(this.lastName);
        author.setBiography(this.biography);
        author.setBirthDate(this.birthDate);
        author.setNationality(this.nationality);
        author.setEmail(this.email);
    }
}