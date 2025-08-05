package com.thoughtworks.winetracker.wine.region.entity;

import java.util.UUID;
import java.util.List;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import com.thoughtworks.winetracker.wine.wine.entity.Wine;
import com.thoughtworks.winetracker.wine.producer.entity.Producer;

@Entity
@Table(name = "regions")
@Getter
@Setter
@NoArgsConstructor
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @NotNull
    @Size(min = 1, max = 200)
    private String name;

    @Column(nullable = false)
    @NotNull
    @Size(min = 1, max = 100)
    private String country;

    @Column
    @Size(max = 500)
    private String description;

    @Column
    @Size(max = 200)
    private String climate;

    @OneToMany(mappedBy = "region", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Wine> wines;

    @OneToMany(mappedBy = "region", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Producer> producers;

    public Region(String name, String country) {
        this.name = name;
        this.country = country;
    }

    public Region(String name, String country, String description, String climate) {
        this.name = name;
        this.country = country;
        this.description = description;
        this.climate = climate;
    }
}