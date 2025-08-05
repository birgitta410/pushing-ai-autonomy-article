package com.thoughtworks.winetracker.wine.wine.dto;

import java.util.UUID;
import java.time.LocalDate;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWineRequest {
    
    @Size(min = 1, max = 200)
    private String name;
    
    @Min(1800)
    @Max(2030)
    private Integer vintage;
    
    @DecimalMin("0.0")
    @DecimalMax("50.0")
    private Double alcoholContent;
    
    private String color;
    
    private LocalDate drinkingDate;
    
    @Min(1)
    @Max(10)
    private Integer personalRating;
    
    @Size(max = 1000)
    private String tastingNotes;
    
    @DecimalMin("0.0")
    private Double price;
    
    private UUID producerId;
    
    private UUID regionId;
}