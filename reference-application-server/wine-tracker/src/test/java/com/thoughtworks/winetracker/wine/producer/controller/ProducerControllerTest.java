package com.thoughtworks.winetracker.wine.producer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.winetracker.wine.producer.service.ProducerService;
import com.thoughtworks.winetracker.wine.producer.dto.ProducerDto;
import com.thoughtworks.winetracker.wine.producer.dto.CreateProducerRequest;
import com.thoughtworks.winetracker.wine.producer.dto.UpdateProducerRequest;
import com.thoughtworks.winetracker.wine.producer.exception.ProducerNotFoundException;
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

@WebMvcTest(ProducerController.class)
class ProducerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProducerService producerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create_ShouldReturnCreatedProducer_WhenValidRequest() throws Exception {
        // Given
        CreateProducerRequest request = new CreateProducerRequest();
        request.setName("Test Producer");
        request.setDescription("A great wine producer");
        request.setFoundedYear(1950);
        request.setWebsite("https://testproducer.com");
        request.setRegionId(UUID.randomUUID());

        ProducerDto expectedDto = new ProducerDto();
        expectedDto.setId(UUID.randomUUID());
        expectedDto.setName("Test Producer");
        expectedDto.setDescription("A great wine producer");
        expectedDto.setFoundedYear(1950);
        expectedDto.setWebsite("https://testproducer.com");

        when(producerService.create(any(CreateProducerRequest.class))).thenReturn(expectedDto);

        // When & Then
        mockMvc.perform(post("/api/v1/producers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expectedDto.getId().toString()))
                .andExpect(jsonPath("$.name").value("Test Producer"))
                .andExpect(jsonPath("$.description").value("A great wine producer"))
                .andExpect(jsonPath("$.foundedYear").value(1950))
                .andExpect(jsonPath("$.website").value("https://testproducer.com"));

        verify(producerService).create(any(CreateProducerRequest.class));
    }

    @Test
    void findAll_ShouldReturnListOfProducers() throws Exception {
        // Given
        ProducerDto producer1 = new ProducerDto();
        producer1.setId(UUID.randomUUID());
        producer1.setName("Producer 1");
        producer1.setFoundedYear(1950);

        ProducerDto producer2 = new ProducerDto();
        producer2.setId(UUID.randomUUID());
        producer2.setName("Producer 2");
        producer2.setFoundedYear(1960);

        List<ProducerDto> producers = Arrays.asList(producer1, producer2);
        when(producerService.findAll()).thenReturn(producers);

        // When & Then
        mockMvc.perform(get("/api/v1/producers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Producer 1"))
                .andExpect(jsonPath("$[1].name").value("Producer 2"));

        verify(producerService).findAll();
    }

    @Test
    void findById_ShouldReturnProducer_WhenProducerExists() throws Exception {
        // Given
        UUID producerId = UUID.randomUUID();
        ProducerDto expectedDto = new ProducerDto();
        expectedDto.setId(producerId);
        expectedDto.setName("Test Producer");
        expectedDto.setFoundedYear(1950);

        when(producerService.findById(producerId)).thenReturn(expectedDto);

        // When & Then
        mockMvc.perform(get("/api/v1/producers/{id}", producerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(producerId.toString()))
                .andExpect(jsonPath("$.name").value("Test Producer"))
                .andExpect(jsonPath("$.foundedYear").value(1950));

        verify(producerService).findById(producerId);
    }

    @Test
    void findById_ShouldReturnNotFound_WhenProducerDoesNotExist() throws Exception {
        // Given
        UUID producerId = UUID.randomUUID();
        when(producerService.findById(producerId)).thenThrow(new ProducerNotFoundException(producerId));

        // When & Then
        mockMvc.perform(get("/api/v1/producers/{id}", producerId))
                .andExpect(status().isNotFound());

        verify(producerService).findById(producerId);
    }

    @Test
    void update_ShouldReturnUpdatedProducer_WhenValidRequest() throws Exception {
        // Given
        UUID producerId = UUID.randomUUID();
        UpdateProducerRequest request = new UpdateProducerRequest();
        request.setName("Updated Producer");
        request.setDescription("Updated description");
        request.setFoundedYear(1960);

        ProducerDto expectedDto = new ProducerDto();
        expectedDto.setId(producerId);
        expectedDto.setName("Updated Producer");
        expectedDto.setDescription("Updated description");
        expectedDto.setFoundedYear(1960);

        when(producerService.update(eq(producerId), any(UpdateProducerRequest.class))).thenReturn(expectedDto);

        // When & Then
        mockMvc.perform(put("/api/v1/producers/{id}", producerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(producerId.toString()))
                .andExpect(jsonPath("$.name").value("Updated Producer"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.foundedYear").value(1960));

        verify(producerService).update(eq(producerId), any(UpdateProducerRequest.class));
    }

    @Test
    void update_ShouldReturnNotFound_WhenProducerDoesNotExist() throws Exception {
        // Given
        UUID producerId = UUID.randomUUID();
        UpdateProducerRequest request = new UpdateProducerRequest();
        request.setName("Updated Producer");

        when(producerService.update(eq(producerId), any(UpdateProducerRequest.class)))
                .thenThrow(new ProducerNotFoundException(producerId));

        // When & Then
        mockMvc.perform(put("/api/v1/producers/{id}", producerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(producerService).update(eq(producerId), any(UpdateProducerRequest.class));
    }

    @Test
    void deleteById_ShouldReturnNoContent_WhenProducerExists() throws Exception {
        // Given
        UUID producerId = UUID.randomUUID();
        doNothing().when(producerService).deleteById(producerId);

        // When & Then
        mockMvc.perform(delete("/api/v1/producers/{id}", producerId))
                .andExpect(status().isNoContent());

        verify(producerService).deleteById(producerId);
    }

    @Test
    void deleteById_ShouldReturnNotFound_WhenProducerDoesNotExist() throws Exception {
        // Given
        UUID producerId = UUID.randomUUID();
        doThrow(new ProducerNotFoundException(producerId)).when(producerService).deleteById(producerId);

        // When & Then
        mockMvc.perform(delete("/api/v1/producers/{id}", producerId))
                .andExpect(status().isNotFound());

        verify(producerService).deleteById(producerId);
    }

    @Test
    void create_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        // Given
        CreateProducerRequest request = new CreateProducerRequest();
        // Missing required fields

        // When & Then
        mockMvc.perform(post("/api/v1/producers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(producerService, never()).create(any(CreateProducerRequest.class));
    }
}