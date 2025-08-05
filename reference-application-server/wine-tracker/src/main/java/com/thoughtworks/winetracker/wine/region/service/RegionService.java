package com.thoughtworks.winetracker.wine.region.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.thoughtworks.winetracker.wine.region.repository.RegionRepository;
import com.thoughtworks.winetracker.wine.region.entity.Region;
import com.thoughtworks.winetracker.wine.region.dto.RegionDto;
import com.thoughtworks.winetracker.wine.region.dto.CreateRegionRequest;
import com.thoughtworks.winetracker.wine.region.dto.UpdateRegionRequest;
import com.thoughtworks.winetracker.wine.region.mapper.RegionMapper;
import com.thoughtworks.winetracker.wine.region.exception.RegionNotFoundException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class RegionService {
    private final RegionRepository repository;
    private final RegionMapper mapper;

    public RegionService(RegionRepository repository, RegionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public RegionDto create(CreateRegionRequest request) {
        Region region = mapper.toEntity(request);
        Region savedRegion = repository.save(region);
        return mapper.toDto(savedRegion);
    }

    @Transactional(readOnly = true)
    public List<RegionDto> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RegionDto findById(UUID id) {
        Region region = repository.findById(id)
                .orElseThrow(() -> new RegionNotFoundException(id));
        return mapper.toDto(region);
    }

    public RegionDto update(UUID id, UpdateRegionRequest request) {
        Region region = repository.findById(id)
                .orElseThrow(() -> new RegionNotFoundException(id));
        
        mapper.updateEntity(region, request);
        Region updatedRegion = repository.save(region);
        return mapper.toDto(updatedRegion);
    }

    public void deleteById(UUID id) {
        if (!repository.existsById(id)) {
            throw new RegionNotFoundException(id);
        }
        repository.deleteById(id);
    }
}