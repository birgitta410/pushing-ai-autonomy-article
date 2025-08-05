package com.thoughtworks.winetracker.wine.producer.exception;

import java.util.UUID;

public class ProducerNotFoundException extends RuntimeException {
    public ProducerNotFoundException(UUID id) {
        super("Producer not found with id: " + id);
    }
    
    public ProducerNotFoundException(String message) {
        super(message);
    }
}