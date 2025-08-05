package com.thoughtworks.winetracker.wine.producer.mapper;

import org.springframework.stereotype.Component;
import com.thoughtworks.winetracker.wine.producer.entity.Producer;
import com.thoughtworks.winetracker.wine.producer.dto.ProducerDto;
import com.thoughtworks.winetracker.wine.producer.dto.CreateProducerRequest;
import com.thoughtworks.winetracker.wine.producer.dto.UpdateProducerRequest;
import com.thoughtworks.winetracker.wine.region.entity.Region;
import com.thoughtworks.winetracker.wine.region.mapper.RegionMapper;

@Component
public class ProducerMapper {
    
    private final RegionMapper regionMapper;
    
    public ProducerMapper(RegionMapper regionMapper) {
        this.regionMapper = regionMapper;
    }
    
    public ProducerDto toDto(Producer producer) {
        if (producer == null) {
            return null;
        }
        
        return new ProducerDto(
            producer.getId(),
            producer.getName(),
            producer.getDescription(),
            producer.getFoundedYear(),
            producer.getWebsite(),
            regionMapper.toDto(producer.getRegion())
        );
    }
    
    public Producer toEntity(CreateProducerRequest request, Region region) {
        if (request == null) {
            return null;
        }
        
        return new Producer(
            request.getName(),
            request.getDescription(),
            request.getFoundedYear(),
            request.getWebsite(),
            region
        );
    }
    
    public void updateEntity(Producer producer, UpdateProducerRequest request, Region region) {
        if (producer == null || request == null) {
            return;
        }
        
        if (request.getName() != null) {
            producer.setName(request.getName());
        }
        if (request.getDescription() != null) {
            producer.setDescription(request.getDescription());
        }
        if (request.getFoundedYear() != null) {
            producer.setFoundedYear(request.getFoundedYear());
        }
        if (request.getWebsite() != null) {
            producer.setWebsite(request.getWebsite());
        }
        if (region != null) {
            producer.setRegion(region);
        }
    }
}