package com.thoughtworks.winetracker.wine.producer.dto;

import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProducerRequest {
    
    @NotNull
    @Size(min = 1, max = 200)
    private String name;
    
    @Size(max = 500)
    private String description;
    
    @Min(1000)
    private Integer foundedYear;
    
    @Size(max = 255)
    private String website;
    
    private UUID regionId;
}