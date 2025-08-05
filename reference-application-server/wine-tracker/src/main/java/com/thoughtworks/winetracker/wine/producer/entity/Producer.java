package com.thoughtworks.winetracker.wine.producer.entity;

import java.util.UUID;
import java.util.List;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import com.thoughtworks.winetracker.wine.wine.entity.Wine;
import com.thoughtworks.winetracker.wine.region.entity.Region;

@Entity
@Table(name = "producers")
@Getter
@Setter
@NoArgsConstructor
public class Producer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @NotNull
    @Size(min = 1, max = 200)
    private String name;

    @Column
    @Size(max = 500)
    private String description;

    @Column
    @Min(1000)
    private Integer foundedYear;

    @Column
    @Size(max = 255)
    private String website;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region;

    @OneToMany(mappedBy = "producer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Wine> wines;

    public Producer(String name) {
        this.name = name;
    }

    public Producer(String name, String description, Integer foundedYear, String website, Region region) {
        this.name = name;
        this.description = description;
        this.foundedYear = foundedYear;
        this.website = website;
        this.region = region;
    }
}