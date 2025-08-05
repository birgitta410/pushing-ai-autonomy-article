package com.gen.example.officelibrary.library.domain;

import com.gen.example.officelibrary.shared.exception.ResourceNotFoundException;

import java.util.UUID;

public class BorrowingRecordNotFoundException extends ResourceNotFoundException {
    
    public BorrowingRecordNotFoundException(UUID id) {
        super("BorrowingRecord", id);
    }
    
    public BorrowingRecordNotFoundException(String message) {
        super(message);
    }
}