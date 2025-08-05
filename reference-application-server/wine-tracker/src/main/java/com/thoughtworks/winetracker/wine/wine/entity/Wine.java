package com.thoughtworks.winetracker.wine.wine.entity;

import java.util.UUID;
import java.time.LocalDate;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import com.thoughtworks.winetracker.wine.producer.entity.Producer;
import com.thoughtworks.winetracker.wine.region.entity.Region;

@Entity
@Table(name = "wines")
@Getter
@Setter
@NoArgsConstructor
public class Wine {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @NotNull
    @Size(min = 1, max = 200)
    private String name;

    @Column
    @Min(1800)
    @Max(2030)
    private Integer vintage;

    @Column
    @DecimalMin("0.0")
    @DecimalMax("50.0")
    private Double alcoholContent;

    @Column(nullable = false)
    @NotNull
    private String color;

    @Column(nullable = false)
    @NotNull
    private LocalDate drinkingDate;

    @Column
    @Min(1)
    @Max(10)
    private Integer personalRating;

    @Column
    @Size(max = 1000)
    private String tastingNotes;

    @Column
    @DecimalMin("0.0")
    private Double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producer_id", nullable = false)
    @NotNull
    private Producer producer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    @NotNull
    private Region region;

    public Wine(String name, String color, LocalDate drinkingDate, Producer producer, Region region) {
        this.name = name;
        this.color = color;
        this.drinkingDate = drinkingDate;
        this.producer = producer;
        this.region = region;
    }

    public Wine(String name, Integer vintage, Double alcoholContent, String color, 
                LocalDate drinkingDate, Integer personalRating, String tastingNotes, 
                Double price, Producer producer, Region region) {
        this.name = name;
        this.vintage = vintage;
        this.alcoholContent = alcoholContent;
        this.color = color;
        this.drinkingDate = drinkingDate;
        this.personalRating = personalRating;
        this.tastingNotes = tastingNotes;
        this.price = price;
        this.producer = producer;
        this.region = region;
    }
}