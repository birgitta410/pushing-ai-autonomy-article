package com.thoughtworks.winetracker.wine.producer.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.thoughtworks.winetracker.wine.producer.entity.Producer;
import com.thoughtworks.winetracker.wine.region.entity.Region;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ProducerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProducerRepository producerRepository;

    @Test
    void shouldSaveAndFindProducer() {
        // Given
        Region region = new Region("Bordeaux", "France");
        entityManager.persistAndFlush(region);
        
        Producer producer = new Producer("Château Margaux", "Premier Grand Cru Classé", 1815, "https://chateau-margaux.com", region);
        
        // When
        Producer savedProducer = producerRepository.save(producer);
        
        // Then
        assertThat(savedProducer.getId()).isNotNull();
        assertThat(savedProducer.getName()).isEqualTo("Château Margaux");
        assertThat(savedProducer.getDescription()).isEqualTo("Premier Grand Cru Classé");
        assertThat(savedProducer.getFoundedYear()).isEqualTo(1815);
        assertThat(savedProducer.getWebsite()).isEqualTo("https://chateau-margaux.com");
        assertThat(savedProducer.getRegion()).isEqualTo(region);
    }

    @Test
    void shouldFindByRegion() {
        // Given
        Region bordeaux = new Region("Bordeaux", "France");
        Region tuscany = new Region("Tuscany", "Italy");
        entityManager.persistAndFlush(bordeaux);
        entityManager.persistAndFlush(tuscany);
        
        Producer producer1 = new Producer("Château Margaux", "Premier Grand Cru Classé", 1815, "https://chateau-margaux.com", bordeaux);
        Producer producer2 = new Producer("Château Latour", "Premier Grand Cru Classé", 1670, "https://chateau-latour.com", bordeaux);
        Producer producer3 = new Producer("Antinori", "Historic Tuscan producer", 1385, "https://antinori.it", tuscany);
        
        entityManager.persistAndFlush(producer1);
        entityManager.persistAndFlush(producer2);
        entityManager.persistAndFlush(producer3);
        
        // When
        List<Producer> bordeauxProducers = producerRepository.findByRegion(bordeaux);
        
        // Then
        assertThat(bordeauxProducers).hasSize(2);
        assertThat(bordeauxProducers).extracting(Producer::getName)
            .containsExactlyInAnyOrder("Château Margaux", "Château Latour");
    }

    @Test
    void shouldFindEmptyListWhenNoProducersInRegion() {
        // Given
        Region emptyRegion = new Region("Empty Region", "Country");
        entityManager.persistAndFlush(emptyRegion);
        
        // When
        List<Producer> producers = producerRepository.findByRegion(emptyRegion);
        
        // Then
        assertThat(producers).isEmpty();
    }

    @Test
    void shouldFindAllProducers() {
        // Given
        Region region = new Region("Champagne", "France");
        entityManager.persistAndFlush(region);
        
        Producer producer1 = new Producer("Dom Pérignon");
        Producer producer2 = new Producer("Krug");
        entityManager.persistAndFlush(producer1);
        entityManager.persistAndFlush(producer2);
        
        // When
        var producers = producerRepository.findAll();
        
        // Then
        assertThat(producers).hasSize(2);
        assertThat(producers).extracting(Producer::getName).containsExactlyInAnyOrder("Dom Pérignon", "Krug");
    }

    @Test
    void shouldDeleteProducer() {
        // Given
        Producer producer = new Producer("Test Producer");
        Producer savedProducer = entityManager.persistAndFlush(producer);
        
        // When
        producerRepository.deleteById(savedProducer.getId());
        
        // Then
        Optional<Producer> foundProducer = producerRepository.findById(savedProducer.getId());
        assertThat(foundProducer).isEmpty();
    }
}