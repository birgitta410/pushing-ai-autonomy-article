package com.thoughtworks.winetracker.wine.wine.repository;

import java.util.UUID;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.thoughtworks.winetracker.wine.wine.entity.Wine;
import com.thoughtworks.winetracker.wine.producer.entity.Producer;
import com.thoughtworks.winetracker.wine.region.entity.Region;

@Repository
public interface WineRepository extends JpaRepository<Wine, UUID> {
    
    List<Wine> findByProducer(Producer producer);
    
    List<Wine> findByRegion(Region region);
    
    @Query("SELECT w FROM Wine w WHERE " +
           "(:name IS NULL OR LOWER(w.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:vintage IS NULL OR w.vintage = :vintage) AND " +
           "(:rating IS NULL OR w.personalRating = :rating)")
    List<Wine> searchWines(@Param("name") String name, 
                          @Param("vintage") Integer vintage, 
                          @Param("rating") Integer rating);
    
}