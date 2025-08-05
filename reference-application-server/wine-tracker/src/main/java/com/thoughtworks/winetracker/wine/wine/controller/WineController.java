package com.thoughtworks.winetracker.wine.wine.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.thoughtworks.winetracker.wine.wine.service.WineService;
import com.thoughtworks.winetracker.wine.wine.dto.WineDto;
import com.thoughtworks.winetracker.wine.wine.dto.CreateWineRequest;
import com.thoughtworks.winetracker.wine.wine.dto.UpdateWineRequest;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/wines")
public class WineController {
    private final WineService service;

    public WineController(WineService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<WineDto> create(@Valid @RequestBody CreateWineRequest request) {
        log.debug("Creating new wine with request: {}", request);
        WineDto created = service.create(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<WineDto>> findAll() {
        log.debug("Fetching all wines");
        List<WineDto> entities = service.findAll();
        return ResponseEntity.ok(entities);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WineDto> findById(@PathVariable UUID id) {
        log.debug("Fetching wine with ID: {}", id);
        WineDto entity = service.findById(id);
        return ResponseEntity.ok(entity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WineDto> update(@PathVariable UUID id, @Valid @RequestBody UpdateWineRequest request) {
        log.debug("Updating wine with ID: {}", id);
        WineDto updated = service.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        log.debug("Deleting wine with ID: {}", id);
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}