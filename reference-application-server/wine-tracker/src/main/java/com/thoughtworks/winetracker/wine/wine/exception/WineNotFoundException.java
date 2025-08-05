package com.thoughtworks.winetracker.wine.wine.exception;

import java.util.UUID;

public class WineNotFoundException extends RuntimeException {
    public WineNotFoundException(UUID id) {
        super("Wine not found with id: " + id);
    }
    
    public WineNotFoundException(String message) {
        super(message);
    }
}