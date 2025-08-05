package com.thoughtworks.winetracker.wine.producer.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.thoughtworks.winetracker.wine.producer.service.ProducerService;
import com.thoughtworks.winetracker.wine.producer.dto.ProducerDto;
import com.thoughtworks.winetracker.wine.producer.dto.CreateProducerRequest;
import com.thoughtworks.winetracker.wine.producer.dto.UpdateProducerRequest;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/producers")
public class ProducerController {
    private final ProducerService service;

    public ProducerController(ProducerService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ProducerDto> create(@Valid @RequestBody CreateProducerRequest request) {
        log.debug("Creating new producer with request: {}", request);
        log.info("Putting a change here to create a local change set for testing");
        ProducerDto created = service.create(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProducerDto>> findAll() {
        log.debug("Fetching all producers");
        List<ProducerDto> entities = service.findAll();
        return ResponseEntity.ok(entities);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProducerDto> findById(@PathVariable UUID id) {
        log.debug("Fetching producer with ID: {}", id);
        ProducerDto entity = service.findById(id);
        return ResponseEntity.ok(entity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProducerDto> update(@PathVariable UUID id, @Valid @RequestBody UpdateProducerRequest request) {
        log.debug("Updating producer with ID: {}", id);
        ProducerDto updated = service.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        log.debug("Deleting producer with ID: {}", id);
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}