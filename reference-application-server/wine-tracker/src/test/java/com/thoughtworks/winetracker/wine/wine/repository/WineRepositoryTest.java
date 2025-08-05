package com.thoughtworks.winetracker.wine.wine.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.thoughtworks.winetracker.wine.wine.entity.Wine;
import com.thoughtworks.winetracker.wine.producer.entity.Producer;
import com.thoughtworks.winetracker.wine.region.entity.Region;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class WineRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WineRepository wineRepository;

    @Test
    void shouldSaveAndFindWine() {
        // Given
        Region region = new Region("Bordeaux", "France");
        Producer producer = new Producer("Château Margaux");
        entityManager.persistAndFlush(region);
        entityManager.persistAndFlush(producer);
        
        Wine wine = new Wine("Château Margaux 2015", 2015, 13.5, "Red", 
                           LocalDate.of(2023, 6, 15), 9, "Excellent vintage", 
                           450.0, producer, region);
        
        // When
        Wine savedWine = wineRepository.save(wine);
        
        // Then
        assertThat(savedWine.getId()).isNotNull();
        assertThat(savedWine.getName()).isEqualTo("Château Margaux 2015");
        assertThat(savedWine.getVintage()).isEqualTo(2015);
        assertThat(savedWine.getAlcoholContent()).isEqualTo(13.5);
        assertThat(savedWine.getColor()).isEqualTo("Red");
        assertThat(savedWine.getDrinkingDate()).isEqualTo(LocalDate.of(2023, 6, 15));
        assertThat(savedWine.getPersonalRating()).isEqualTo(9);
        assertThat(savedWine.getTastingNotes()).isEqualTo("Excellent vintage");
        assertThat(savedWine.getPrice()).isEqualTo(450.0);
        assertThat(savedWine.getProducer()).isEqualTo(producer);
        assertThat(savedWine.getRegion()).isEqualTo(region);
    }

    @Test
    void shouldFindByProducer() {
        // Given
        Region region = new Region("Bordeaux", "France");
        Producer producer1 = new Producer("Château Margaux");
        Producer producer2 = new Producer("Château Latour");
        entityManager.persistAndFlush(region);
        entityManager.persistAndFlush(producer1);
        entityManager.persistAndFlush(producer2);
        
        Wine wine1 = new Wine("Margaux 2015", "Red", LocalDate.now(), producer1, region);
        Wine wine2 = new Wine("Margaux 2016", "Red", LocalDate.now(), producer1, region);
        Wine wine3 = new Wine("Latour 2015", "Red", LocalDate.now(), producer2, region);
        
        entityManager.persistAndFlush(wine1);
        entityManager.persistAndFlush(wine2);
        entityManager.persistAndFlush(wine3);
        
        // When
        List<Wine> margauxWines = wineRepository.findByProducer(producer1);
        
        // Then
        assertThat(margauxWines).hasSize(2);
        assertThat(margauxWines).extracting(Wine::getName)
            .containsExactlyInAnyOrder("Margaux 2015", "Margaux 2016");
    }

    @Test
    void shouldFindByRegion() {
        // Given
        Region bordeaux = new Region("Bordeaux", "France");
        Region tuscany = new Region("Tuscany", "Italy");
        Producer producer = new Producer("Test Producer");
        entityManager.persistAndFlush(bordeaux);
        entityManager.persistAndFlush(tuscany);
        entityManager.persistAndFlush(producer);
        
        Wine wine1 = new Wine("Bordeaux Wine 1", "Red", LocalDate.now(), producer, bordeaux);
        Wine wine2 = new Wine("Bordeaux Wine 2", "Red", LocalDate.now(), producer, bordeaux);
        Wine wine3 = new Wine("Tuscan Wine", "Red", LocalDate.now(), producer, tuscany);
        
        entityManager.persistAndFlush(wine1);
        entityManager.persistAndFlush(wine2);
        entityManager.persistAndFlush(wine3);
        
        // When
        List<Wine> bordeauxWines = wineRepository.findByRegion(bordeaux);
        
        // Then
        assertThat(bordeauxWines).hasSize(2);
        assertThat(bordeauxWines).extracting(Wine::getName)
            .containsExactlyInAnyOrder("Bordeaux Wine 1", "Bordeaux Wine 2");
    }

    @Test
    void shouldSearchWinesByName() {
        // Given
        Region region = new Region("Test Region", "Country");
        Producer producer = new Producer("Test Producer");
        entityManager.persistAndFlush(region);
        entityManager.persistAndFlush(producer);
        
        Wine wine1 = new Wine("Château Margaux", "Red", LocalDate.now(), producer, region);
        Wine wine2 = new Wine("Château Latour", "Red", LocalDate.now(), producer, region);
        Wine wine3 = new Wine("Dom Pérignon", "White", LocalDate.now(), producer, region);
        
        entityManager.persistAndFlush(wine1);
        entityManager.persistAndFlush(wine2);
        entityManager.persistAndFlush(wine3);
        
        // When
        List<Wine> chateauWines = wineRepository.searchWines("château", null, null);
        
        // Then
        assertThat(chateauWines).hasSize(2);
        assertThat(chateauWines).extracting(Wine::getName)
            .containsExactlyInAnyOrder("Château Margaux", "Château Latour");
    }

    @Test
    void shouldSearchWinesByVintage() {
        // Given
        Region region = new Region("Test Region", "Country");
        Producer producer = new Producer("Test Producer");
        entityManager.persistAndFlush(region);
        entityManager.persistAndFlush(producer);
        
        Wine wine1 = new Wine("Wine 2015", 2015, 13.0, "Red", LocalDate.now(), 8, "Good", 100.0, producer, region);
        Wine wine2 = new Wine("Wine 2016", 2016, 13.5, "Red", LocalDate.now(), 9, "Excellent", 120.0, producer, region);
        Wine wine3 = new Wine("Wine 2015 Another", 2015, 12.5, "Red", LocalDate.now(), 7, "Nice", 90.0, producer, region);
        
        entityManager.persistAndFlush(wine1);
        entityManager.persistAndFlush(wine2);
        entityManager.persistAndFlush(wine3);
        
        // When
        List<Wine> wines2015 = wineRepository.searchWines(null, 2015, null);
        
        // Then
        assertThat(wines2015).hasSize(2);
        assertThat(wines2015).extracting(Wine::getName)
            .containsExactlyInAnyOrder("Wine 2015", "Wine 2015 Another");
    }

    @Test
    void shouldSearchWinesByRating() {
        // Given
        Region region = new Region("Test Region", "Country");
        Producer producer = new Producer("Test Producer");
        entityManager.persistAndFlush(region);
        entityManager.persistAndFlush(producer);
        
        Wine wine1 = new Wine("High Rated Wine", 2015, 13.0, "Red", LocalDate.now(), 9, "Excellent", 200.0, producer, region);
        Wine wine2 = new Wine("Medium Rated Wine", 2016, 13.5, "Red", LocalDate.now(), 7, "Good", 100.0, producer, region);
        Wine wine3 = new Wine("Another High Rated", 2017, 12.5, "Red", LocalDate.now(), 9, "Outstanding", 250.0, producer, region);
        
        entityManager.persistAndFlush(wine1);
        entityManager.persistAndFlush(wine2);
        entityManager.persistAndFlush(wine3);
        
        // When
        List<Wine> highRatedWines = wineRepository.searchWines(null, null, 9);
        
        // Then
        assertThat(highRatedWines).hasSize(2);
        assertThat(highRatedWines).extracting(Wine::getName)
            .containsExactlyInAnyOrder("High Rated Wine", "Another High Rated");
    }

    @Test
    void shouldDeleteWine() {
        // Given
        Region region = new Region("Test Region", "Country");
        Producer producer = new Producer("Test Producer");
        entityManager.persistAndFlush(region);
        entityManager.persistAndFlush(producer);
        
        Wine wine = new Wine("Test Wine", "Red", LocalDate.now(), producer, region);
        Wine savedWine = entityManager.persistAndFlush(wine);
        
        // When
        wineRepository.deleteById(savedWine.getId());
        
        // Then
        Optional<Wine> foundWine = wineRepository.findById(savedWine.getId());
        assertThat(foundWine).isEmpty();
    }
}