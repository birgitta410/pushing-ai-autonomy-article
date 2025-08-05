package com.thoughtworks.winetracker.wine.wine.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.thoughtworks.winetracker.wine.wine.repository.WineRepository;
import com.thoughtworks.winetracker.wine.wine.entity.Wine;
import com.thoughtworks.winetracker.wine.wine.dto.WineDto;
import com.thoughtworks.winetracker.wine.wine.dto.CreateWineRequest;
import com.thoughtworks.winetracker.wine.wine.dto.UpdateWineRequest;
import com.thoughtworks.winetracker.wine.wine.mapper.WineMapper;
import com.thoughtworks.winetracker.wine.wine.exception.WineNotFoundException;
import com.thoughtworks.winetracker.wine.producer.repository.ProducerRepository;
import com.thoughtworks.winetracker.wine.producer.entity.Producer;
import com.thoughtworks.winetracker.wine.producer.dto.ProducerDto;
import com.thoughtworks.winetracker.wine.producer.exception.ProducerNotFoundException;
import com.thoughtworks.winetracker.wine.region.repository.RegionRepository;
import com.thoughtworks.winetracker.wine.region.entity.Region;
import com.thoughtworks.winetracker.wine.region.dto.RegionDto;
import com.thoughtworks.winetracker.wine.region.exception.RegionNotFoundException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WineServiceTest {

    @Mock
    private WineRepository repository;

    @Mock
    private ProducerRepository producerRepository;

    @Mock
    private RegionRepository regionRepository;

    @Mock
    private WineMapper mapper;

    @InjectMocks
    private WineService wineService;

    private UUID wineId;
    private UUID producerId;
    private UUID regionId;
    private Wine wine;
    private Producer producer;
    private Region region;
    private WineDto wineDto;
    private ProducerDto producerDto;
    private RegionDto regionDto;
    private CreateWineRequest createRequest;
    private UpdateWineRequest updateRequest;

    @BeforeEach
    void setUp() {
        wineId = UUID.randomUUID();
        producerId = UUID.randomUUID();
        regionId = UUID.randomUUID();
        
        region = new Region("Bordeaux", "France");
        producer = new Producer("Château Margaux", "Famous winery", 1815, "https://chateau-margaux.com", region);
        wine = new Wine("Château Margaux 2015", 2015, 13.5, "Red", LocalDate.of(2023, 6, 15), 9, "Excellent wine", 500.0, producer, region);
        
        regionDto = new RegionDto(regionId, "Bordeaux", "France", null, null);
        producerDto = new ProducerDto(producerId, "Château Margaux", "Famous winery", 1815, "https://chateau-margaux.com", regionDto);
        wineDto = new WineDto(wineId, "Château Margaux 2015", 2015, 13.5, "Red", LocalDate.of(2023, 6, 15), 9, "Excellent wine", 500.0, producerDto, regionDto);
        
        createRequest = new CreateWineRequest("Château Margaux 2015", 2015, 13.5, "Red", LocalDate.of(2023, 6, 15), 9, "Excellent wine", 500.0, producerId, regionId);
        updateRequest = new UpdateWineRequest("Updated Wine", 2016, 14.0, "Red", LocalDate.of(2023, 7, 15), 8, "Updated notes", 600.0, producerId, regionId);
    }

    @Test
    void create_ShouldReturnWineDto_WhenValidRequest() {
        // Given
        when(producerRepository.findById(producerId)).thenReturn(Optional.of(producer));
        when(regionRepository.findById(regionId)).thenReturn(Optional.of(region));
        when(mapper.toEntity(createRequest, producer, region)).thenReturn(wine);
        when(repository.save(wine)).thenReturn(wine);
        when(mapper.toDto(wine)).thenReturn(wineDto);

        // When
        WineDto result = wineService.create(createRequest);

        // Then
        assertNotNull(result);
        assertEquals(wineDto, result);
        verify(producerRepository).findById(producerId);
        verify(regionRepository).findById(regionId);
        verify(mapper).toEntity(createRequest, producer, region);
        verify(repository).save(wine);
        verify(mapper).toDto(wine);
    }

    @Test
    void create_ShouldThrowProducerNotFoundException_WhenProducerDoesNotExist() {
        // Given
        when(producerRepository.findById(producerId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ProducerNotFoundException.class, () -> wineService.create(createRequest));
        verify(producerRepository).findById(producerId);
        verify(regionRepository, never()).findById(any());
        verify(mapper, never()).toEntity(any(), any(), any());
        verify(repository, never()).save(any());
    }

    @Test
    void create_ShouldThrowRegionNotFoundException_WhenRegionDoesNotExist() {
        // Given
        when(producerRepository.findById(producerId)).thenReturn(Optional.of(producer));
        when(regionRepository.findById(regionId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RegionNotFoundException.class, () -> wineService.create(createRequest));
        verify(producerRepository).findById(producerId);
        verify(regionRepository).findById(regionId);
        verify(mapper, never()).toEntity(any(), any(), any());
        verify(repository, never()).save(any());
    }

    @Test
    void findAll_ShouldReturnListOfWineDtos() {
        // Given
        List<Wine> wines = Arrays.asList(wine);
        when(repository.findAll()).thenReturn(wines);
        when(mapper.toDto(wine)).thenReturn(wineDto);

        // When
        List<WineDto> result = wineService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(wineDto, result.get(0));
        verify(repository).findAll();
        verify(mapper).toDto(wine);
    }

    @Test
    void findById_ShouldReturnWineDto_WhenWineExists() {
        // Given
        when(repository.findById(wineId)).thenReturn(Optional.of(wine));
        when(mapper.toDto(wine)).thenReturn(wineDto);

        // When
        WineDto result = wineService.findById(wineId);

        // Then
        assertNotNull(result);
        assertEquals(wineDto, result);
        verify(repository).findById(wineId);
        verify(mapper).toDto(wine);
    }

    @Test
    void findById_ShouldThrowWineNotFoundException_WhenWineDoesNotExist() {
        // Given
        when(repository.findById(wineId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(WineNotFoundException.class, () -> wineService.findById(wineId));
        verify(repository).findById(wineId);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void update_ShouldReturnUpdatedWineDto_WhenWineExists() {
        // Given
        when(repository.findById(wineId)).thenReturn(Optional.of(wine));
        when(producerRepository.findById(producerId)).thenReturn(Optional.of(producer));
        when(regionRepository.findById(regionId)).thenReturn(Optional.of(region));
        when(repository.save(wine)).thenReturn(wine);
        when(mapper.toDto(wine)).thenReturn(wineDto);

        // When
        WineDto result = wineService.update(wineId, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals(wineDto, result);
        verify(repository).findById(wineId);
        verify(producerRepository).findById(producerId);
        verify(regionRepository).findById(regionId);
        verify(mapper).updateEntity(wine, updateRequest, producer, region);
        verify(repository).save(wine);
        verify(mapper).toDto(wine);
    }

    @Test
    void update_ShouldThrowWineNotFoundException_WhenWineDoesNotExist() {
        // Given
        when(repository.findById(wineId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(WineNotFoundException.class, () -> wineService.update(wineId, updateRequest));
        verify(repository).findById(wineId);
        verify(producerRepository, never()).findById(any());
        verify(regionRepository, never()).findById(any());
        verify(mapper, never()).updateEntity(any(), any(), any(), any());
        verify(repository, never()).save(any());
    }

    @Test
    void update_ShouldUpdateWithoutProducerAndRegion_WhenIdsAreNull() {
        // Given
        UpdateWineRequest requestWithoutIds = new UpdateWineRequest("Updated Wine", 2016, 14.0, "Red", LocalDate.of(2023, 7, 15), 8, "Updated notes", 600.0, null, null);
        when(repository.findById(wineId)).thenReturn(Optional.of(wine));
        when(repository.save(wine)).thenReturn(wine);
        when(mapper.toDto(wine)).thenReturn(wineDto);

        // When
        WineDto result = wineService.update(wineId, requestWithoutIds);

        // Then
        assertNotNull(result);
        verify(repository).findById(wineId);
        verify(producerRepository, never()).findById(any());
        verify(regionRepository, never()).findById(any());
        verify(mapper).updateEntity(wine, requestWithoutIds, null, null);
        verify(repository).save(wine);
        verify(mapper).toDto(wine);
    }

    @Test
    void deleteById_ShouldDeleteWine_WhenWineExists() {
        // Given
        when(repository.existsById(wineId)).thenReturn(true);

        // When
        wineService.deleteById(wineId);

        // Then
        verify(repository).existsById(wineId);
        verify(repository).deleteById(wineId);
    }

    @Test
    void deleteById_ShouldThrowWineNotFoundException_WhenWineDoesNotExist() {
        // Given
        when(repository.existsById(wineId)).thenReturn(false);

        // When & Then
        assertThrows(WineNotFoundException.class, () -> wineService.deleteById(wineId));
        verify(repository).existsById(wineId);
        verify(repository, never()).deleteById(any());
    }
}