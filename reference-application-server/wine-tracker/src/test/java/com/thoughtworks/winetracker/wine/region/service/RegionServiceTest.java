package com.thoughtworks.winetracker.wine.region.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.thoughtworks.winetracker.wine.region.repository.RegionRepository;
import com.thoughtworks.winetracker.wine.region.entity.Region;
import com.thoughtworks.winetracker.wine.region.dto.RegionDto;
import com.thoughtworks.winetracker.wine.region.dto.CreateRegionRequest;
import com.thoughtworks.winetracker.wine.region.dto.UpdateRegionRequest;
import com.thoughtworks.winetracker.wine.region.mapper.RegionMapper;
import com.thoughtworks.winetracker.wine.region.exception.RegionNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegionServiceTest {

    @Mock
    private RegionRepository repository;

    @Mock
    private RegionMapper mapper;

    @InjectMocks
    private RegionService regionService;

    private UUID regionId;
    private Region region;
    private RegionDto regionDto;
    private CreateRegionRequest createRequest;
    private UpdateRegionRequest updateRequest;

    @BeforeEach
    void setUp() {
        regionId = UUID.randomUUID();
        region = new Region("Bordeaux", "France", "Famous wine region", "Maritime");
        regionDto = new RegionDto(regionId, "Bordeaux", "France", "Famous wine region", "Maritime");
        createRequest = new CreateRegionRequest("Bordeaux", "France", "Famous wine region", "Maritime");
        updateRequest = new UpdateRegionRequest("Updated Bordeaux", "France", "Updated description", "Maritime");
    }

    @Test
    void create_ShouldReturnRegionDto_WhenValidRequest() {
        // Given
        when(mapper.toEntity(createRequest)).thenReturn(region);
        when(repository.save(region)).thenReturn(region);
        when(mapper.toDto(region)).thenReturn(regionDto);

        // When
        RegionDto result = regionService.create(createRequest);

        // Then
        assertNotNull(result);
        assertEquals(regionDto, result);
        verify(mapper).toEntity(createRequest);
        verify(repository).save(region);
        verify(mapper).toDto(region);
    }

    @Test
    void findAll_ShouldReturnListOfRegionDtos() {
        // Given
        List<Region> regions = Arrays.asList(region);
        List<RegionDto> regionDtos = Arrays.asList(regionDto);
        when(repository.findAll()).thenReturn(regions);
        when(mapper.toDto(region)).thenReturn(regionDto);

        // When
        List<RegionDto> result = regionService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(regionDto, result.get(0));
        verify(repository).findAll();
        verify(mapper).toDto(region);
    }

    @Test
    void findById_ShouldReturnRegionDto_WhenRegionExists() {
        // Given
        when(repository.findById(regionId)).thenReturn(Optional.of(region));
        when(mapper.toDto(region)).thenReturn(regionDto);

        // When
        RegionDto result = regionService.findById(regionId);

        // Then
        assertNotNull(result);
        assertEquals(regionDto, result);
        verify(repository).findById(regionId);
        verify(mapper).toDto(region);
    }

    @Test
    void findById_ShouldThrowRegionNotFoundException_WhenRegionDoesNotExist() {
        // Given
        when(repository.findById(regionId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RegionNotFoundException.class, () -> regionService.findById(regionId));
        verify(repository).findById(regionId);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void update_ShouldReturnUpdatedRegionDto_WhenRegionExists() {
        // Given
        when(repository.findById(regionId)).thenReturn(Optional.of(region));
        when(repository.save(region)).thenReturn(region);
        when(mapper.toDto(region)).thenReturn(regionDto);

        // When
        RegionDto result = regionService.update(regionId, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals(regionDto, result);
        verify(repository).findById(regionId);
        verify(mapper).updateEntity(region, updateRequest);
        verify(repository).save(region);
        verify(mapper).toDto(region);
    }

    @Test
    void update_ShouldThrowRegionNotFoundException_WhenRegionDoesNotExist() {
        // Given
        when(repository.findById(regionId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RegionNotFoundException.class, () -> regionService.update(regionId, updateRequest));
        verify(repository).findById(regionId);
        verify(mapper, never()).updateEntity(any(), any());
        verify(repository, never()).save(any());
    }

    @Test
    void deleteById_ShouldDeleteRegion_WhenRegionExists() {
        // Given
        when(repository.existsById(regionId)).thenReturn(true);

        // When
        regionService.deleteById(regionId);

        // Then
        verify(repository).existsById(regionId);
        verify(repository).deleteById(regionId);
    }

    @Test
    void deleteById_ShouldThrowRegionNotFoundException_WhenRegionDoesNotExist() {
        // Given
        when(repository.existsById(regionId)).thenReturn(false);

        // When & Then
        assertThrows(RegionNotFoundException.class, () -> regionService.deleteById(regionId));
        verify(repository).existsById(regionId);
        verify(repository, never()).deleteById(any());
    }
}