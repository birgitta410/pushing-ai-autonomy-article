package com.thoughtworks.winetracker.wine.region.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.winetracker.wine.region.service.RegionService;
import com.thoughtworks.winetracker.wine.region.dto.RegionDto;
import com.thoughtworks.winetracker.wine.region.dto.CreateRegionRequest;
import com.thoughtworks.winetracker.wine.region.dto.UpdateRegionRequest;
import com.thoughtworks.winetracker.wine.region.exception.RegionNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegionController.class)
class RegionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegionService regionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create_ShouldReturnCreatedRegion_WhenValidRequest() throws Exception {
        // Given
        CreateRegionRequest request = new CreateRegionRequest();
        request.setName("Bordeaux");
        request.setCountry("France");
        request.setDescription("Famous wine region in France");
        request.setClimate("Oceanic");

        RegionDto expectedDto = new RegionDto();
        expectedDto.setId(UUID.randomUUID());
        expectedDto.setName("Bordeaux");
        expectedDto.setCountry("France");
        expectedDto.setDescription("Famous wine region in France");
        expectedDto.setClimate("Oceanic");

        when(regionService.create(any(CreateRegionRequest.class))).thenReturn(expectedDto);

        // When & Then
        mockMvc.perform(post("/api/v1/regions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expectedDto.getId().toString()))
                .andExpect(jsonPath("$.name").value("Bordeaux"))
                .andExpect(jsonPath("$.country").value("France"))
                .andExpect(jsonPath("$.description").value("Famous wine region in France"))
                .andExpect(jsonPath("$.climate").value("Oceanic"));

        verify(regionService).create(any(CreateRegionRequest.class));
    }

    @Test
    void findAll_ShouldReturnListOfRegions() throws Exception {
        // Given
        RegionDto region1 = new RegionDto();
        region1.setId(UUID.randomUUID());
        region1.setName("Bordeaux");
        region1.setCountry("France");

        RegionDto region2 = new RegionDto();
        region2.setId(UUID.randomUUID());
        region2.setName("Tuscany");
        region2.setCountry("Italy");

        List<RegionDto> regions = Arrays.asList(region1, region2);
        when(regionService.findAll()).thenReturn(regions);

        // When & Then
        mockMvc.perform(get("/api/v1/regions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Bordeaux"))
                .andExpect(jsonPath("$[0].country").value("France"))
                .andExpect(jsonPath("$[1].name").value("Tuscany"))
                .andExpect(jsonPath("$[1].country").value("Italy"));

        verify(regionService).findAll();
    }

    @Test
    void findById_ShouldReturnRegion_WhenRegionExists() throws Exception {
        // Given
        UUID regionId = UUID.randomUUID();
        RegionDto expectedDto = new RegionDto();
        expectedDto.setId(regionId);
        expectedDto.setName("Bordeaux");
        expectedDto.setCountry("France");

        when(regionService.findById(regionId)).thenReturn(expectedDto);

        // When & Then
        mockMvc.perform(get("/api/v1/regions/{id}", regionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(regionId.toString()))
                .andExpect(jsonPath("$.name").value("Bordeaux"))
                .andExpect(jsonPath("$.country").value("France"));

        verify(regionService).findById(regionId);
    }

    @Test
    void findById_ShouldReturnNotFound_WhenRegionDoesNotExist() throws Exception {
        // Given
        UUID regionId = UUID.randomUUID();
        when(regionService.findById(regionId)).thenThrow(new RegionNotFoundException(regionId));

        // When & Then
        mockMvc.perform(get("/api/v1/regions/{id}", regionId))
                .andExpect(status().isNotFound());

        verify(regionService).findById(regionId);
    }

    @Test
    void update_ShouldReturnUpdatedRegion_WhenValidRequest() throws Exception {
        // Given
        UUID regionId = UUID.randomUUID();
        UpdateRegionRequest request = new UpdateRegionRequest();
        request.setName("Updated Bordeaux");
        request.setDescription("Updated description");
        request.setClimate("Updated climate");

        RegionDto expectedDto = new RegionDto();
        expectedDto.setId(regionId);
        expectedDto.setName("Updated Bordeaux");
        expectedDto.setCountry("France");
        expectedDto.setDescription("Updated description");
        expectedDto.setClimate("Updated climate");

        when(regionService.update(eq(regionId), any(UpdateRegionRequest.class))).thenReturn(expectedDto);

        // When & Then
        mockMvc.perform(put("/api/v1/regions/{id}", regionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(regionId.toString()))
                .andExpect(jsonPath("$.name").value("Updated Bordeaux"))
                .andExpect(jsonPath("$.country").value("France"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.climate").value("Updated climate"));

        verify(regionService).update(eq(regionId), any(UpdateRegionRequest.class));
    }

    @Test
    void update_ShouldReturnNotFound_WhenRegionDoesNotExist() throws Exception {
        // Given
        UUID regionId = UUID.randomUUID();
        UpdateRegionRequest request = new UpdateRegionRequest();
        request.setName("Updated Region");

        when(regionService.update(eq(regionId), any(UpdateRegionRequest.class)))
                .thenThrow(new RegionNotFoundException(regionId));

        // When & Then
        mockMvc.perform(put("/api/v1/regions/{id}", regionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(regionService).update(eq(regionId), any(UpdateRegionRequest.class));
    }

    @Test
    void deleteById_ShouldReturnNoContent_WhenRegionExists() throws Exception {
        // Given
        UUID regionId = UUID.randomUUID();
        doNothing().when(regionService).deleteById(regionId);

        // When & Then
        mockMvc.perform(delete("/api/v1/regions/{id}", regionId))
                .andExpect(status().isNoContent());

        verify(regionService).deleteById(regionId);
    }

    @Test
    void deleteById_ShouldReturnNotFound_WhenRegionDoesNotExist() throws Exception {
        // Given
        UUID regionId = UUID.randomUUID();
        doThrow(new RegionNotFoundException(regionId)).when(regionService).deleteById(regionId);

        // When & Then
        mockMvc.perform(delete("/api/v1/regions/{id}", regionId))
                .andExpect(status().isNotFound());

        verify(regionService).deleteById(regionId);
    }

    @Test
    void create_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        // Given
        CreateRegionRequest request = new CreateRegionRequest();
        // Missing required fields

        // When & Then
        mockMvc.perform(post("/api/v1/regions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(regionService, never()).create(any(CreateRegionRequest.class));
    }
}