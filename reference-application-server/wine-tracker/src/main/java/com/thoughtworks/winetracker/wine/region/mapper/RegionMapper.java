package com.thoughtworks.winetracker.wine.region.mapper;

import org.springframework.stereotype.Component;
import com.thoughtworks.winetracker.wine.region.entity.Region;
import com.thoughtworks.winetracker.wine.region.dto.RegionDto;
import com.thoughtworks.winetracker.wine.region.dto.CreateRegionRequest;
import com.thoughtworks.winetracker.wine.region.dto.UpdateRegionRequest;

@Component
public class RegionMapper {
    
    public RegionDto toDto(Region region) {
        if (region == null) {
            return null;
        }
        
        return new RegionDto(
            region.getId(),
            region.getName(),
            region.getCountry(),
            region.getDescription(),
            region.getClimate()
        );
    }
    
    public Region toEntity(CreateRegionRequest request) {
        if (request == null) {
            return null;
        }
        
        return new Region(
            request.getName(),
            request.getCountry(),
            request.getDescription(),
            request.getClimate()
        );
    }
    
    public void updateEntity(Region region, UpdateRegionRequest request) {
        if (region == null || request == null) {
            return;
        }
        
        if (request.getName() != null) {
            region.setName(request.getName());
        }
        if (request.getCountry() != null) {
            region.setCountry(request.getCountry());
        }
        if (request.getDescription() != null) {
            region.setDescription(request.getDescription());
        }
        if (request.getClimate() != null) {
            region.setClimate(request.getClimate());
        }
    }
}