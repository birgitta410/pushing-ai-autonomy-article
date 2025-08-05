package com.thoughtworks.winetracker.wine.producer.repository;

import java.util.UUID;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.thoughtworks.winetracker.wine.producer.entity.Producer;
import com.thoughtworks.winetracker.wine.region.entity.Region;

@Repository
public interface ProducerRepository extends JpaRepository<Producer, UUID> {
    
    List<Producer> findByRegion(Region region);
    
}