package com.gen.example.officelibrary.author.domain;

import org.springframework.stereotype.Component;

@Component
public class AuthorMapper {

    public AuthorDTO toDto(Author author) {
        if (author == null) {
            return null;
        }
        
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

    public Author toEntity(CreateAuthorRequest request) {
        if (request == null) {
            return null;
        }
        
        return new Author(
            request.getFirstName(),
            request.getLastName(),
            request.getBiography(),
            request.getBirthDate(),
            request.getNationality(),
            request.getEmail()
        );
    }

    public void updateEntity(Author author, UpdateAuthorRequest request) {
        if (author == null || request == null) {
            return;
        }
        
        if (request.getFirstName() != null) {
            author.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            author.setLastName(request.getLastName());
        }
        if (request.getBiography() != null) {
            author.setBiography(request.getBiography());
        }
        if (request.getBirthDate() != null) {
            author.setBirthDate(request.getBirthDate());
        }
        if (request.getNationality() != null) {
            author.setNationality(request.getNationality());
        }
        if (request.getEmail() != null) {
            author.setEmail(request.getEmail());
        }
    }
}