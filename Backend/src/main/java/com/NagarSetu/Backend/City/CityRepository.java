package com.NagarSetu.Backend.City;

import com.NagarSetu.Backend.Entities.City;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CityRepository extends JpaRepository<City, UUID> , CityRepositoryCustom {


    boolean existsById(UUID uuid);

    boolean existsByName(String name);

    List<City> findAllByOrderByNameAsc();


    @Query(value = """
    SELECT * FROM cities c
    WHERE ST_Contains(c.geometry, ST_SetSRID(ST_Point(:lng, :lat), 4326))
    LIMIT 1
    """, nativeQuery = true)
    Optional<City> findCityByLocation(double lat, double lng);



}
