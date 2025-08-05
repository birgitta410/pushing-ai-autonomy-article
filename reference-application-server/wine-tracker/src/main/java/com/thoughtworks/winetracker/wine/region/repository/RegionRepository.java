package com.thoughtworks.winetracker.wine.region.repository;

import java.util.UUID;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.thoughtworks.winetracker.wine.region.entity.Region;

@Repository
public interface RegionRepository extends JpaRepository<Region, UUID> {
    
    Optional<Region> findByNameAndCountry(String name, String country);
    
}