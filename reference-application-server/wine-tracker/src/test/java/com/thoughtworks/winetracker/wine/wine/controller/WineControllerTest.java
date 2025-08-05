package com.thoughtworks.winetracker.wine.wine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.winetracker.wine.wine.service.WineService;
import com.thoughtworks.winetracker.wine.wine.dto.WineDto;
import com.thoughtworks.winetracker.wine.wine.dto.CreateWineRequest;
import com.thoughtworks.winetracker.wine.wine.dto.UpdateWineRequest;
import com.thoughtworks.winetracker.wine.wine.exception.WineNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WineController.class)
class WineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WineService wineService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create_ShouldReturnCreatedWine_WhenValidRequest() throws Exception {
        // Given
        CreateWineRequest request = new CreateWineRequest();
        request.setName("Test Wine");
        request.setVintage(2020);
        request.setAlcoholContent(13.5);
        request.setColor("Red");
        request.setDrinkingDate(LocalDate.now());
        request.setPersonalRating(8);
        request.setTastingNotes("Excellent wine");
        request.setPrice(25.99);
        request.setProducerId(UUID.randomUUID());
        request.setRegionId(UUID.randomUUID());

        WineDto expectedDto = new WineDto();
        expectedDto.setId(UUID.randomUUID());
        expectedDto.setName("Test Wine");
        expectedDto.setVintage(2020);
        expectedDto.setAlcoholContent(13.5);
        expectedDto.setColor("Red");
        expectedDto.setDrinkingDate(LocalDate.now());
        expectedDto.setPersonalRating(8);
        expectedDto.setTastingNotes("Excellent wine");
        expectedDto.setPrice(25.99);

        when(wineService.create(any(CreateWineRequest.class))).thenReturn(expectedDto);

        // When & Then
        mockMvc.perform(post("/api/v1/wines")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expectedDto.getId().toString()))
                .andExpect(jsonPath("$.name").value("Test Wine"))
                .andExpect(jsonPath("$.vintage").value(2020))
                .andExpect(jsonPath("$.alcoholContent").value(13.5))
                .andExpect(jsonPath("$.color").value("Red"))
                .andExpect(jsonPath("$.personalRating").value(8))
                .andExpect(jsonPath("$.tastingNotes").value("Excellent wine"))
                .andExpect(jsonPath("$.price").value(25.99));

        verify(wineService).create(any(CreateWineRequest.class));
    }

    @Test
    void findAll_ShouldReturnListOfWines() throws Exception {
        // Given
        WineDto wine1 = new WineDto();
        wine1.setId(UUID.randomUUID());
        wine1.setName("Wine 1");
        wine1.setVintage(2020);

        WineDto wine2 = new WineDto();
        wine2.setId(UUID.randomUUID());
        wine2.setName("Wine 2");
        wine2.setVintage(2021);

        List<WineDto> wines = Arrays.asList(wine1, wine2);
        when(wineService.findAll()).thenReturn(wines);

        // When & Then
        mockMvc.perform(get("/api/v1/wines"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Wine 1"))
                .andExpect(jsonPath("$[1].name").value("Wine 2"));

        verify(wineService).findAll();
    }

    @Test
    void findById_ShouldReturnWine_WhenWineExists() throws Exception {
        // Given
        UUID wineId = UUID.randomUUID();
        WineDto expectedDto = new WineDto();
        expectedDto.setId(wineId);
        expectedDto.setName("Test Wine");
        expectedDto.setVintage(2020);

        when(wineService.findById(wineId)).thenReturn(expectedDto);

        // When & Then
        mockMvc.perform(get("/api/v1/wines/{id}", wineId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(wineId.toString()))
                .andExpect(jsonPath("$.name").value("Test Wine"))
                .andExpect(jsonPath("$.vintage").value(2020));

        verify(wineService).findById(wineId);
    }

    @Test
    void findById_ShouldReturnNotFound_WhenWineDoesNotExist() throws Exception {
        // Given
        UUID wineId = UUID.randomUUID();
        when(wineService.findById(wineId)).thenThrow(new WineNotFoundException(wineId));

        // When & Then
        mockMvc.perform(get("/api/v1/wines/{id}", wineId))
                .andExpect(status().isNotFound());

        verify(wineService).findById(wineId);
    }

    @Test
    void update_ShouldReturnUpdatedWine_WhenValidRequest() throws Exception {
        // Given
        UUID wineId = UUID.randomUUID();
        UpdateWineRequest request = new UpdateWineRequest();
        request.setName("Updated Wine");
        request.setVintage(2021);
        request.setPersonalRating(9);

        WineDto expectedDto = new WineDto();
        expectedDto.setId(wineId);
        expectedDto.setName("Updated Wine");
        expectedDto.setVintage(2021);
        expectedDto.setPersonalRating(9);

        when(wineService.update(eq(wineId), any(UpdateWineRequest.class))).thenReturn(expectedDto);

        // When & Then
        mockMvc.perform(put("/api/v1/wines/{id}", wineId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(wineId.toString()))
                .andExpect(jsonPath("$.name").value("Updated Wine"))
                .andExpect(jsonPath("$.vintage").value(2021))
                .andExpect(jsonPath("$.personalRating").value(9));

        verify(wineService).update(eq(wineId), any(UpdateWineRequest.class));
    }

    @Test
    void update_ShouldReturnNotFound_WhenWineDoesNotExist() throws Exception {
        // Given
        UUID wineId = UUID.randomUUID();
        UpdateWineRequest request = new UpdateWineRequest();
        request.setName("Updated Wine");

        when(wineService.update(eq(wineId), any(UpdateWineRequest.class)))
                .thenThrow(new WineNotFoundException(wineId));

        // When & Then
        mockMvc.perform(put("/api/v1/wines/{id}", wineId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(wineService).update(eq(wineId), any(UpdateWineRequest.class));
    }

    @Test
    void deleteById_ShouldReturnNoContent_WhenWineExists() throws Exception {
        // Given
        UUID wineId = UUID.randomUUID();
        doNothing().when(wineService).deleteById(wineId);

        // When & Then
        mockMvc.perform(delete("/api/v1/wines/{id}", wineId))
                .andExpect(status().isNoContent());

        verify(wineService).deleteById(wineId);
    }

    @Test
    void deleteById_ShouldReturnNotFound_WhenWineDoesNotExist() throws Exception {
        // Given
        UUID wineId = UUID.randomUUID();
        doThrow(new WineNotFoundException(wineId)).when(wineService).deleteById(wineId);

        // When & Then
        mockMvc.perform(delete("/api/v1/wines/{id}", wineId))
                .andExpect(status().isNotFound());

        verify(wineService).deleteById(wineId);
    }

    @Test
    void create_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        // Given
        CreateWineRequest request = new CreateWineRequest();
        // Missing required fields

        // When & Then
        mockMvc.perform(post("/api/v1/wines")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(wineService, never()).create(any(CreateWineRequest.class));
    }
}