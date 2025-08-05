package com.thoughtworks.winetracker.wine.region.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.thoughtworks.winetracker.wine.region.entity.Region;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class RegionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RegionRepository regionRepository;

    @Test
    void shouldSaveAndFindRegion() {
        // Given
        Region region = new Region("Bordeaux", "France", "Famous wine region", "Maritime");
        
        // When
        Region savedRegion = regionRepository.save(region);
        
        // Then
        assertThat(savedRegion.getId()).isNotNull();
        assertThat(savedRegion.getName()).isEqualTo("Bordeaux");
        assertThat(savedRegion.getCountry()).isEqualTo("France");
        assertThat(savedRegion.getDescription()).isEqualTo("Famous wine region");
        assertThat(savedRegion.getClimate()).isEqualTo("Maritime");
    }

    @Test
    void shouldFindByNameAndCountry() {
        // Given
        Region region = new Region("Tuscany", "Italy", "Beautiful wine region", "Mediterranean");
        entityManager.persistAndFlush(region);
        
        // When
        Optional<Region> foundRegion = regionRepository.findByNameAndCountry("Tuscany", "Italy");
        
        // Then
        assertThat(foundRegion).isPresent();
        assertThat(foundRegion.get().getName()).isEqualTo("Tuscany");
        assertThat(foundRegion.get().getCountry()).isEqualTo("Italy");
    }

    @Test
    void shouldNotFindByNameAndCountryWhenNotExists() {
        // When
        Optional<Region> foundRegion = regionRepository.findByNameAndCountry("NonExistent", "Country");
        
        // Then
        assertThat(foundRegion).isEmpty();
    }

    @Test
    void shouldFindAllRegions() {
        // Given
        Region region1 = new Region("Bordeaux", "France");
        Region region2 = new Region("Tuscany", "Italy");
        entityManager.persistAndFlush(region1);
        entityManager.persistAndFlush(region2);
        
        // When
        var regions = regionRepository.findAll();
        
        // Then
        assertThat(regions).hasSize(2);
        assertThat(regions).extracting(Region::getName).containsExactlyInAnyOrder("Bordeaux", "Tuscany");
    }

    @Test
    void shouldDeleteRegion() {
        // Given
        Region region = new Region("Rioja", "Spain");
        Region savedRegion = entityManager.persistAndFlush(region);
        
        // When
        regionRepository.deleteById(savedRegion.getId());
        
        // Then
        Optional<Region> foundRegion = regionRepository.findById(savedRegion.getId());
        assertThat(foundRegion).isEmpty();
    }
}