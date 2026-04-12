package com.NagarSetu.Backend.Ward;

import com.NagarSetu.Backend.Entities.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WardRepository extends JpaRepository<Ward, UUID> , WardRepositoryCustom {

     boolean existsById(UUID uuid);

    boolean existsByCity_IdAndWardNumber(UUID cityId, Integer wardNumber);
    boolean existsByCity_IdAndWardNumberAndIdNot(UUID cityId, Integer wardNumber, UUID wardId);

    @Query("""
    SELECT COALESCE(MAX(w.wardNumber), 0) + 1
    FROM Ward w
    WHERE w.city.id = :cityId
""")
    Integer getNextWardNumber(UUID cityId);

    boolean existsByCity_IdAndName(UUID cityId, String name);

    @Query(value = """
    SELECT 
        id,
        name,
        ward_number,
        population,
        area_sq_km
    FROM wards
    WHERE city_id = :cityId
    ORDER BY ward_number ASC
    """, nativeQuery = true)
    List<Object[]> getWardListByCity(UUID cityId);



    @Query(value = """
    SELECT * FROM wards w
    WHERE ST_Contains(w.geometry, ST_SetSRID(ST_Point(:lng, :lat), 4326))
    LIMIT 1
    """, nativeQuery = true)
    Optional<Ward> findWardByLocation(double lat, double lng);




}
