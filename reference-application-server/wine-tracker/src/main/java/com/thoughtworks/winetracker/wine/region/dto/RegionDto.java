package com.thoughtworks.winetracker.wine.region.dto;

import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegionDto {
    private UUID id;
    private String name;
    private String country;
    private String description;
    private String climate;
}