package com.thoughtworks.winetracker.wine.wine.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.thoughtworks.winetracker.wine.wine.repository.WineRepository;
import com.thoughtworks.winetracker.wine.wine.entity.Wine;
import com.thoughtworks.winetracker.wine.wine.dto.WineDto;
import com.thoughtworks.winetracker.wine.wine.dto.CreateWineRequest;
import com.thoughtworks.winetracker.wine.wine.dto.UpdateWineRequest;
import com.thoughtworks.winetracker.wine.wine.mapper.WineMapper;
import com.thoughtworks.winetracker.wine.wine.exception.WineNotFoundException;
import com.thoughtworks.winetracker.wine.producer.repository.ProducerRepository;
import com.thoughtworks.winetracker.wine.producer.entity.Producer;
import com.thoughtworks.winetracker.wine.producer.exception.ProducerNotFoundException;
import com.thoughtworks.winetracker.wine.region.repository.RegionRepository;
import com.thoughtworks.winetracker.wine.region.entity.Region;
import com.thoughtworks.winetracker.wine.region.exception.RegionNotFoundException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class WineService {
    private final WineRepository repository;
    private final ProducerRepository producerRepository;
    private final RegionRepository regionRepository;
    private final WineMapper mapper;

    public WineService(WineRepository repository, ProducerRepository producerRepository, 
                      RegionRepository regionRepository, WineMapper mapper) {
        this.repository = repository;
        this.producerRepository = producerRepository;
        this.regionRepository = regionRepository;
        this.mapper = mapper;
    }

    public WineDto create(CreateWineRequest request) {
        log.info("Putting a change here to create a local change set for testing");
        Producer producer = producerRepository.findById(request.getProducerId())
                .orElseThrow(() -> new ProducerNotFoundException(request.getProducerId()));
        
        Region region = regionRepository.findById(request.getRegionId())
                .orElseThrow(() -> new RegionNotFoundException(request.getRegionId()));
        
        Wine wine = mapper.toEntity(request, producer, region);
        Wine savedWine = repository.save(wine);
        return mapper.toDto(savedWine);
    }

    @Transactional(readOnly = true)
    public List<WineDto> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WineDto findById(UUID id) {
        Wine wine = repository.findById(id)
                .orElseThrow(() -> new WineNotFoundException(id));
        return mapper.toDto(wine);
    }

    public WineDto update(UUID id, UpdateWineRequest request) {
        Wine wine = repository.findById(id)
                .orElseThrow(() -> new WineNotFoundException(id));
        
        Producer producer = null;
        if (request.getProducerId() != null) {
            producer = producerRepository.findById(request.getProducerId())
                    .orElseThrow(() -> new ProducerNotFoundException(request.getProducerId()));
        }
        
        Region region = null;
        if (request.getRegionId() != null) {
            region = regionRepository.findById(request.getRegionId())
                    .orElseThrow(() -> new RegionNotFoundException(request.getRegionId()));
        }
        
        mapper.updateEntity(wine, request, producer, region);
        Wine updatedWine = repository.save(wine);
        return mapper.toDto(updatedWine);
    }

    public void deleteById(UUID id) {
        if (!repository.existsById(id)) {
            throw new WineNotFoundException(id);
        }
        repository.deleteById(id);
    }
}