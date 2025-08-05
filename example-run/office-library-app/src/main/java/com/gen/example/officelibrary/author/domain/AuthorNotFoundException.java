package com.gen.example.officelibrary.author.domain;

import com.gen.example.officelibrary.shared.exception.ResourceNotFoundException;

import java.util.UUID;

public class AuthorNotFoundException extends ResourceNotFoundException {
    
    public AuthorNotFoundException(UUID id) {
        super("Author", id);
    }
    
    public AuthorNotFoundException(String message) {
        super(message);
    }
}