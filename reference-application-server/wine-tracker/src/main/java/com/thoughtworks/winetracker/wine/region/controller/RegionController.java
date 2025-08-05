package com.thoughtworks.winetracker.wine.region.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.thoughtworks.winetracker.wine.region.service.RegionService;
import com.thoughtworks.winetracker.wine.region.dto.RegionDto;
import com.thoughtworks.winetracker.wine.region.dto.CreateRegionRequest;
import com.thoughtworks.winetracker.wine.region.dto.UpdateRegionRequest;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/regions")
public class RegionController {
    private final RegionService service;

    public RegionController(RegionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<RegionDto> create(@Valid @RequestBody CreateRegionRequest request) {
        log.debug("Creating new region with request: {}", request);
        RegionDto created = service.create(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RegionDto>> findAll() {
        log.debug("Fetching all regions");
        List<RegionDto> entities = service.findAll();
        return ResponseEntity.ok(entities);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegionDto> findById(@PathVariable UUID id) {
        log.debug("Fetching region with ID: {}", id);
        RegionDto entity = service.findById(id);
        return ResponseEntity.ok(entity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegionDto> update(@PathVariable UUID id, @Valid @RequestBody UpdateRegionRequest request) {
        log.debug("Updating region with ID: {}", id);
        RegionDto updated = service.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        log.debug("Deleting region with ID: {}", id);
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}