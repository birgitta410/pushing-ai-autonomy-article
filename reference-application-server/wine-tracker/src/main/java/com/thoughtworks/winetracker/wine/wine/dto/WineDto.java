package com.thoughtworks.winetracker.wine.wine.dto;

import java.util.UUID;
import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.thoughtworks.winetracker.wine.producer.dto.ProducerDto;
import com.thoughtworks.winetracker.wine.region.dto.RegionDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WineDto {
    private UUID id;
    private String name;
    private Integer vintage;
    private Double alcoholContent;
    private String color;
    private LocalDate drinkingDate;
    private Integer personalRating;
    private String tastingNotes;
    private Double price;
    private ProducerDto producer;
    private RegionDto region;
}