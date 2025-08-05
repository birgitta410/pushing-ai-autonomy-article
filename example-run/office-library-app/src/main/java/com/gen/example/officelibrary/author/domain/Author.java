package com.gen.example.officelibrary.author.domain;

import com.gen.example.officelibrary.shared.domain.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "authors")
@Getter
@Setter
@NoArgsConstructor
public class Author extends BaseEntity {

    @Column(nullable = false)
    @NotNull
    @Size(min = 1, max = 100)
    private String firstName;

    @Column(nullable = false)
    @NotNull
    @Size(min = 1, max = 100)
    private String lastName;

    @Column(length = 1000)
    @Size(max = 1000)
    private String biography;

    @Column
    private LocalDate birthDate;

    @Column(length = 100)
    @Size(max = 100)
    private String nationality;

    @Column(length = 255)
    @Email
    @Size(max = 255)
    private String email;

    // Note: OneToMany relationship with Book will be handled by Book entity
    // to avoid circular dependencies in the domain model

    public Author(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Author(String firstName, String lastName, String biography, 
                  LocalDate birthDate, String nationality, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.biography = biography;
        this.birthDate = birthDate;
        this.nationality = nationality;
        this.email = email;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}