package com.thoughtworks.winetracker.wine.producer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.thoughtworks.winetracker.wine.producer.repository.ProducerRepository;
import com.thoughtworks.winetracker.wine.producer.entity.Producer;
import com.thoughtworks.winetracker.wine.producer.dto.ProducerDto;
import com.thoughtworks.winetracker.wine.producer.dto.CreateProducerRequest;
import com.thoughtworks.winetracker.wine.producer.dto.UpdateProducerRequest;
import com.thoughtworks.winetracker.wine.producer.mapper.ProducerMapper;
import com.thoughtworks.winetracker.wine.producer.exception.ProducerNotFoundException;
import com.thoughtworks.winetracker.wine.region.repository.RegionRepository;
import com.thoughtworks.winetracker.wine.region.entity.Region;
import com.thoughtworks.winetracker.wine.region.dto.RegionDto;
import com.thoughtworks.winetracker.wine.region.exception.RegionNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProducerServiceTest {

    @Mock
    private ProducerRepository repository;

    @Mock
    private RegionRepository regionRepository;

    @Mock
    private ProducerMapper mapper;

    @InjectMocks
    private ProducerService producerService;

    private UUID producerId;
    private UUID regionId;
    private Producer producer;
    private Region region;
    private ProducerDto producerDto;
    private RegionDto regionDto;
    private CreateProducerRequest createRequest;
    private UpdateProducerRequest updateRequest;

    @BeforeEach
    void setUp() {
        producerId = UUID.randomUUID();
        regionId = UUID.randomUUID();
        region = new Region("Bordeaux", "France");
        producer = new Producer("Château Margaux", "Famous winery", 1815, "https://chateau-margaux.com", region);
        regionDto = new RegionDto(regionId, "Bordeaux", "France", null, null);
        producerDto = new ProducerDto(producerId, "Château Margaux", "Famous winery", 1815, "https://chateau-margaux.com", regionDto);
        createRequest = new CreateProducerRequest("Château Margaux", "Famous winery", 1815, "https://chateau-margaux.com", regionId);
        updateRequest = new UpdateProducerRequest("Updated Château Margaux", "Updated description", 1815, "https://updated-url.com", regionId);
    }

    @Test
    void create_ShouldReturnProducerDto_WhenValidRequest() {
        // Given
        when(regionRepository.findById(regionId)).thenReturn(Optional.of(region));
        when(mapper.toEntity(createRequest, region)).thenReturn(producer);
        when(repository.save(producer)).thenReturn(producer);
        when(mapper.toDto(producer)).thenReturn(producerDto);

        // When
        ProducerDto result = producerService.create(createRequest);

        // Then
        assertNotNull(result);
        assertEquals(producerDto, result);
        verify(regionRepository).findById(regionId);
        verify(mapper).toEntity(createRequest, region);
        verify(repository).save(producer);
        verify(mapper).toDto(producer);
    }

    @Test
    void create_ShouldThrowRegionNotFoundException_WhenRegionDoesNotExist() {
        // Given
        when(regionRepository.findById(regionId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RegionNotFoundException.class, () -> producerService.create(createRequest));
        verify(regionRepository).findById(regionId);
        verify(mapper, never()).toEntity(any(), any());
        verify(repository, never()).save(any());
    }

    @Test
    void findAll_ShouldReturnListOfProducerDtos() {
        // Given
        List<Producer> producers = Arrays.asList(producer);
        when(repository.findAll()).thenReturn(producers);
        when(mapper.toDto(producer)).thenReturn(producerDto);

        // When
        List<ProducerDto> result = producerService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(producerDto, result.get(0));
        verify(repository).findAll();
        verify(mapper).toDto(producer);
    }

    @Test
    void findById_ShouldReturnProducerDto_WhenProducerExists() {
        // Given
        when(repository.findById(producerId)).thenReturn(Optional.of(producer));
        when(mapper.toDto(producer)).thenReturn(producerDto);

        // When
        ProducerDto result = producerService.findById(producerId);

        // Then
        assertNotNull(result);
        assertEquals(producerDto, result);
        verify(repository).findById(producerId);
        verify(mapper).toDto(producer);
    }

    @Test
    void findById_ShouldThrowProducerNotFoundException_WhenProducerDoesNotExist() {
        // Given
        when(repository.findById(producerId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ProducerNotFoundException.class, () -> producerService.findById(producerId));
        verify(repository).findById(producerId);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void update_ShouldReturnUpdatedProducerDto_WhenProducerExists() {
        // Given
        when(repository.findById(producerId)).thenReturn(Optional.of(producer));
        when(regionRepository.findById(regionId)).thenReturn(Optional.of(region));
        when(repository.save(producer)).thenReturn(producer);
        when(mapper.toDto(producer)).thenReturn(producerDto);

        // When
        ProducerDto result = producerService.update(producerId, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals(producerDto, result);
        verify(repository).findById(producerId);
        verify(regionRepository).findById(regionId);
        verify(mapper).updateEntity(producer, updateRequest, region);
        verify(repository).save(producer);
        verify(mapper).toDto(producer);
    }

    @Test
    void update_ShouldThrowProducerNotFoundException_WhenProducerDoesNotExist() {
        // Given
        when(repository.findById(producerId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ProducerNotFoundException.class, () -> producerService.update(producerId, updateRequest));
        verify(repository).findById(producerId);
        verify(regionRepository, never()).findById(any());
        verify(mapper, never()).updateEntity(any(), any(), any());
        verify(repository, never()).save(any());
    }

    @Test
    void update_ShouldThrowRegionNotFoundException_WhenRegionDoesNotExist() {
        // Given
        when(repository.findById(producerId)).thenReturn(Optional.of(producer));
        when(regionRepository.findById(regionId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RegionNotFoundException.class, () -> producerService.update(producerId, updateRequest));
        verify(repository).findById(producerId);
        verify(regionRepository).findById(regionId);
        verify(mapper, never()).updateEntity(any(), any(), any());
        verify(repository, never()).save(any());
    }

    @Test
    void update_ShouldUpdateWithoutRegion_WhenRegionIdIsNull() {
        // Given
        UpdateProducerRequest requestWithoutRegion = new UpdateProducerRequest("Updated Name", "Updated desc", 1815, "https://url.com", null);
        when(repository.findById(producerId)).thenReturn(Optional.of(producer));
        when(repository.save(producer)).thenReturn(producer);
        when(mapper.toDto(producer)).thenReturn(producerDto);

        // When
        ProducerDto result = producerService.update(producerId, requestWithoutRegion);

        // Then
        assertNotNull(result);
        verify(repository).findById(producerId);
        verify(regionRepository, never()).findById(any());
        verify(mapper).updateEntity(producer, requestWithoutRegion, null);
        verify(repository).save(producer);
        verify(mapper).toDto(producer);
    }

    @Test
    void deleteById_ShouldDeleteProducer_WhenProducerExists() {
        // Given
        when(repository.existsById(producerId)).thenReturn(true);

        // When
        producerService.deleteById(producerId);

        // Then
        verify(repository).existsById(producerId);
        verify(repository).deleteById(producerId);
    }

    @Test
    void deleteById_ShouldThrowProducerNotFoundException_WhenProducerDoesNotExist() {
        // Given
        when(repository.existsById(producerId)).thenReturn(false);

        // When & Then
        assertThrows(ProducerNotFoundException.class, () -> producerService.deleteById(producerId));
        verify(repository).existsById(producerId);
        verify(repository, never()).deleteById(any());
    }
}