package com.thoughtworks.winetracker.wine.region.exception;

import java.util.UUID;

public class RegionNotFoundException extends RuntimeException {
    public RegionNotFoundException(UUID id) {
        super("Region not found with id: " + id);
    }
    
    public RegionNotFoundException(String message) {
        super(message);
    }
}