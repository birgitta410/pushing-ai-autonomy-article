package com.thoughtworks.winetracker.wine.producer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.thoughtworks.winetracker.wine.producer.repository.ProducerRepository;
import com.thoughtworks.winetracker.wine.producer.entity.Producer;
import com.thoughtworks.winetracker.wine.producer.dto.ProducerDto;
import com.thoughtworks.winetracker.wine.producer.dto.CreateProducerRequest;
import com.thoughtworks.winetracker.wine.producer.dto.UpdateProducerRequest;
import com.thoughtworks.winetracker.wine.producer.mapper.ProducerMapper;
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
public class ProducerService {
    private final ProducerRepository repository;
    private final RegionRepository regionRepository;
    private final ProducerMapper mapper;

    public ProducerService(ProducerRepository repository, RegionRepository regionRepository, ProducerMapper mapper) {
        this.repository = repository;
        this.regionRepository = regionRepository;
        this.mapper = mapper;
    }

    public ProducerDto create(CreateProducerRequest request) {
        log.info("Putting a change here to create a local change set for testing");
        Region region = regionRepository.findById(request.getRegionId())
                .orElseThrow(() -> new RegionNotFoundException(request.getRegionId()));
        
        Producer producer = mapper.toEntity(request, region);
        Producer savedProducer = repository.save(producer);
        return mapper.toDto(savedProducer);
    }

    @Transactional(readOnly = true)
    public List<ProducerDto> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProducerDto findById(UUID id) {
        Producer producer = repository.findById(id)
                .orElseThrow(() -> new ProducerNotFoundException(id));
        return mapper.toDto(producer);
    }

    public ProducerDto update(UUID id, UpdateProducerRequest request) {
        Producer producer = repository.findById(id)
                .orElseThrow(() -> new ProducerNotFoundException(id));
        
        Region region = null;
        if (request.getRegionId() != null) {
            region = regionRepository.findById(request.getRegionId())
                    .orElseThrow(() -> new RegionNotFoundException(request.getRegionId()));
        }
        
        mapper.updateEntity(producer, request, region);
        Producer updatedProducer = repository.save(producer);
        return mapper.toDto(updatedProducer);
    }

    public void deleteById(UUID id) {
        if (!repository.existsById(id)) {
            throw new ProducerNotFoundException(id);
        }
        repository.deleteById(id);
    }
}