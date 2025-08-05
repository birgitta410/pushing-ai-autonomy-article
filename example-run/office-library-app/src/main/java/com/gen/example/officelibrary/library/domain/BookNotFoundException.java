package com.gen.example.officelibrary.library.domain;

import com.gen.example.officelibrary.shared.exception.ResourceNotFoundException;

import java.util.UUID;

public class BookNotFoundException extends ResourceNotFoundException {
    
    public BookNotFoundException(UUID id) {
        super("Book", id);
    }
    
    public BookNotFoundException(String message) {
        super(message);
    }
}