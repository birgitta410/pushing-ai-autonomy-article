package com.thoughtworks.winetracker.wine.wine.mapper;

import org.springframework.stereotype.Component;
import com.thoughtworks.winetracker.wine.wine.entity.Wine;
import com.thoughtworks.winetracker.wine.wine.dto.WineDto;
import com.thoughtworks.winetracker.wine.wine.dto.CreateWineRequest;
import com.thoughtworks.winetracker.wine.wine.dto.UpdateWineRequest;
import com.thoughtworks.winetracker.wine.producer.entity.Producer;
import com.thoughtworks.winetracker.wine.producer.mapper.ProducerMapper;
import com.thoughtworks.winetracker.wine.region.entity.Region;
import com.thoughtworks.winetracker.wine.region.mapper.RegionMapper;
import java.time.LocalDate;

@Component
public class WineMapper {
    
    private final ProducerMapper producerMapper;
    private final RegionMapper regionMapper;
    
    public WineMapper(ProducerMapper producerMapper, RegionMapper regionMapper) {
        this.producerMapper = producerMapper;
        this.regionMapper = regionMapper;
    }
    
    public WineDto toDto(Wine wine) {
        if (wine == null) {
            return null;
        }
        
        return new WineDto(
            wine.getId(),
            wine.getName(),
            wine.getVintage(),
            wine.getAlcoholContent(),
            wine.getColor(),
            wine.getDrinkingDate(),
            wine.getPersonalRating(),
            wine.getTastingNotes(),
            wine.getPrice(),
            producerMapper.toDto(wine.getProducer()),
            regionMapper.toDto(wine.getRegion())
        );
    }
    
    public Wine toEntity(CreateWineRequest request, Producer producer, Region region) {
        if (request == null) {
            return null;
        }
        
        // Validate business rule: drinking date cannot be in the future
        if (request.getDrinkingDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Drinking date cannot be in the future");
        }
        
        return new Wine(
            request.getName(),
            request.getVintage(),
            request.getAlcoholContent(),
            request.getColor(),
            request.getDrinkingDate(),
            request.getPersonalRating(),
            request.getTastingNotes(),
            request.getPrice(),
            producer,
            region
        );
    }
    
    public void updateEntity(Wine wine, UpdateWineRequest request, Producer producer, Region region) {
        if (wine == null || request == null) {
            return;
        }
        
        if (request.getName() != null) {
            wine.setName(request.getName());
        }
        if (request.getVintage() != null) {
            wine.setVintage(request.getVintage());
        }
        if (request.getAlcoholContent() != null) {
            wine.setAlcoholContent(request.getAlcoholContent());
        }
        if (request.getColor() != null) {
            wine.setColor(request.getColor());
        }
        if (request.getDrinkingDate() != null) {
            // Validate business rule: drinking date cannot be in the future
            if (request.getDrinkingDate().isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Drinking date cannot be in the future");
            }
            wine.setDrinkingDate(request.getDrinkingDate());
        }
        if (request.getPersonalRating() != null) {
            wine.setPersonalRating(request.getPersonalRating());
        }
        if (request.getTastingNotes() != null) {
            wine.setTastingNotes(request.getTastingNotes());
        }
        if (request.getPrice() != null) {
            wine.setPrice(request.getPrice());
        }
        if (producer != null) {
            wine.setProducer(producer);
        }
        if (region != null) {
            wine.setRegion(region);
        }
    }
}