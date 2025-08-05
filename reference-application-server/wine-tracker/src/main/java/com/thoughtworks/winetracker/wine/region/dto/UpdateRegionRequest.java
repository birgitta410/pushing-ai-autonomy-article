package com.thoughtworks.winetracker.wine.region.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRegionRequest {
    
    @Size(min = 1, max = 200)
    private String name;
    
    @Size(min = 1, max = 100)
    private String country;
    
    @Size(max = 500)
    private String description;
    
    @Size(max = 200)
    private String climate;
}