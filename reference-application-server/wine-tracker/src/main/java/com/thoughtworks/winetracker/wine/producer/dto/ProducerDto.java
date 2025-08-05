package com.thoughtworks.winetracker.wine.producer.dto;

import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.thoughtworks.winetracker.wine.region.dto.RegionDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProducerDto {
    private UUID id;
    private String name;
    private String description;
    private Integer foundedYear;
    private String website;
    private RegionDto region;
}