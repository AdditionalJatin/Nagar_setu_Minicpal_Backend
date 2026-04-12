package com.NagarSetu.Backend.Ward;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import jakarta.persistence.Query;
import java.util.HashMap;

import java.util.Map;
import java.util.UUID;

@Repository
public class WardRepositoryImpl implements WardRepositoryCustom{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Map<String, Object> insertWard(
            String name,
            UUID cityId,
            Integer wardNumber,
            Long population,
            String geoJson
    ) {

        String sql = """
    
                WITH geom AS (
                                                       SELECT ST_SetSRID(
                                                           ST_GeometryN(
                                                               ST_CollectionExtract(
                                                                   ST_MakeValid(ST_GeomFromGeoJSON(:geoJson)),
                                                                   3
                                                               ),
                                                               1
                                                           ),
                                                           4326
                                                       ) AS g
                                                   ),
                                                   city_geom AS (
                                                       SELECT geometry AS cg\s
                                                       FROM cities\s
                                                       WHERE id = :cityId
                                                   )
                                                   
                                                   INSERT INTO wards (
                                                       id,
                                                       name,
                                                       city_id,
                                                       ward_number,
                                                       population,
                                                       area_sq_km,
                                                       geometry,
                                                       center,
                                                       created_at,
                                                       updated_at
                                                   )
                                                   SELECT
                                                       gen_random_uuid(),
                                                       :name,
                                                       :cityId,
                                                       :wardNumber,
                                                       :population,
                                                       ST_Area(geom.g::geography) / 1000000,
                                                       geom.g,
                                                       ST_PointOnSurface(geom.g),
                                                       NOW(),
                                                       NOW()
                                                   FROM geom, city_geom
                                                   WHERE city_geom.cg IS NOT NULL          
                                                     AND geom.g IS NOT NULL               
                                                     AND ST_CoveredBy(geom.g, city_geom.cg)  
                                                     AND NOT EXISTS (                      
                                                         SELECT 1 FROM wards w
                                                         WHERE w.city_id = :cityId
                                                           AND ST_Intersects(w.geometry, geom.g)
                                                     )
                                                   
                                                   RETURNING
                                                       id,
                                                       name,
                                                       city_id,
                                                       ward_number,
                                                       population,
                                                       area_sq_km,
                                                       ST_AsGeoJSON(geometry) AS geometry,
                                                       ST_AsGeoJSON(center) AS center,
                                                       created_at,
                                                       updated_at;
    """;

        Query query = entityManager.createNativeQuery(sql);

        query.setParameter("name", name);
        query.setParameter("cityId", cityId);
        query.setParameter("wardNumber", wardNumber);
        query.setParameter("population", population);
        query.setParameter("geoJson", geoJson);

        Object resultObj = query.getResultStream().findFirst().orElse(null);

        if (resultObj == null) {
            throw new RuntimeException("City does not exist OR Ward is outside city boundary");
        }

        Object[] result = (Object[]) resultObj;

        Map<String, Object> map = new HashMap<>();

        map.put("id", result[0]);
        map.put("name", result[1]);
        map.put("city_id", result[2]);
        map.put("ward_number", result[3]);
        map.put("population", result[4]);
        map.put("area_sq_km", result[5]);
        map.put("geometry", result[6]);
        map.put("center", result[7]);
        map.put("created_at", result[8]);
        map.put("updated_at", result[9]);

        return map;
    }
    @Override
    public Map<String, Object> updateWardWithoutGeometry(
            UUID wardId,
            String name,
            Integer wardNumber,
            Long population
    ) {

        String sql = """
        UPDATE wards w
        SET
            name = COALESCE(:name, w.name),
            ward_number = COALESCE(:wardNumber, w.ward_number),
            population = COALESCE(:population, w.population),
            updated_at = NOW()
        WHERE w.id = :wardId

        RETURNING
            w.id,
            w.name,
            w.city_id,
            w.ward_number,
            w.population,
            w.area_sq_km,
            ST_AsGeoJSON(w.geometry) AS geometry,
            ST_AsGeoJSON(w.center) AS center,
            w.created_at,
            w.updated_at
        """;

        Query query = entityManager.createNativeQuery(sql);

        query.setParameter("wardId", wardId);
        query.setParameter("name", name);
        query.setParameter("wardNumber", wardNumber);
        query.setParameter("population", population);

        Object resultObj = query.getResultStream().findFirst().orElse(null);

        if (resultObj == null) {
            throw new RuntimeException("Ward update failed");
        }

        return mapResult((Object[]) resultObj);
    }

    // =========================
    // ✅ UPDATE WITH GEOMETRY
    // =========================
    @Override
    public Map<String, Object> updateWardWithGeometry(
            UUID wardId,
            String name,
            Integer wardNumber,
            Long population,
            String geoJson
    ) {

        String sql = """
        WITH geom AS (
            SELECT ST_SetSRID(
                ST_MakeValid(ST_GeomFromGeoJSON(:geoJson)),
                4326
            ) AS g
        ),
        ward_city AS (
            SELECT w.city_id, c.geometry AS cg
            FROM wards w
            JOIN cities c ON w.city_id = c.id
            WHERE w.id = :wardId
        )

        UPDATE wards w
        SET
            name = COALESCE(:name, w.name),
            ward_number = COALESCE(:wardNumber, w.ward_number),
            population = COALESCE(:population, w.population),

            geometry = geom.g,
            center = ST_PointOnSurface(geom.g),
            area_sq_km = ST_Area(geom.g::geography) / 1000000,

            updated_at = NOW()

        FROM geom, ward_city
        WHERE w.id = :wardId
          AND ST_CoveredBy(geom.g, ward_city.cg)
          AND NOT EXISTS (
                SELECT 1 FROM wards w2
                WHERE w2.city_id = ward_city.city_id
                  AND w2.id != :wardId
                  AND ST_Intersects(w2.geometry, geom.g)
          )

        RETURNING
            w.id,
            w.name,
            w.city_id,
            w.ward_number,
            w.population,
            w.area_sq_km,
            ST_AsGeoJSON(w.geometry) AS geometry,
            ST_AsGeoJSON(w.center) AS center,
            w.created_at,
            w.updated_at
        """;

        Query query = entityManager.createNativeQuery(sql);

        query.setParameter("wardId", wardId);
        query.setParameter("name", name);
        query.setParameter("wardNumber", wardNumber);
        query.setParameter("population", population);
        query.setParameter("geoJson", geoJson);

        Object resultObj = query.getResultStream().findFirst().orElse(null);

        if (resultObj == null) {
            throw new RuntimeException(
                    "Update failed: invalid geometry OR outside city OR overlap"
            );
        }

        return mapResult((Object[]) resultObj);
    }

    @Override
    public void deleteWard(UUID wardId) {

        String sql = """
        DELETE FROM wards
        WHERE id = :wardId
    """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("wardId", wardId);

        int rows = query.executeUpdate();

        if (rows == 0) {
            throw new RuntimeException("Ward not found");
        }
    }

    // =========================
    // ✅ COMMON MAPPING
    // =========================
    private Map<String, Object> mapResult(Object[] result) {

        Map<String, Object> map = new HashMap<>();

        map.put("id", result[0]);
        map.put("name", result[1]);
        map.put("city_id", result[2]);
        map.put("ward_number", result[3]);
        map.put("population", result[4]);
        map.put("area_sq_km", result[5]);
        map.put("geometry", result[6]);
        map.put("center", result[7]);
        map.put("created_at", result[8]);
        map.put("updated_at", result[9]);

        return map;
    }






//    @Override
//    public Map<String, Object> updateWard(
//            UUID wardId,
//            String name,
//            Integer wardNumber,
//            Long population,
//            String geoJson
//    ) {
//
//        String sql = """
//
//                WITH geom AS (
//                                                                                                SELECT\s
//                                                                                                    CASE\s
//                                                                                                        WHEN :geoJson IS NULL THEN NULL
//                                                                                                        ELSE ST_SetSRID(
//                                                                                                                ST_MakeValid(ST_GeomFromGeoJSON(:geoJson)),
//                                                                                                                4326
//                                                                                                             )
//                                                                                                    END AS g
//                                                                                            ),
//                                                                                            ward_city AS (
//                                                                                                SELECT w.city_id, c.geometry AS cg
//                                                                                                FROM wards w
//                                                                                                JOIN cities c ON w.city_id = c.id
//                                                                                                WHERE w.id = :wardId
//                                                                                            )
//
//                                                                                            UPDATE wards w
//                                                                                            SET
//                                                                                                name = COALESCE(:name, w.name),
//                                                                                                ward_number = COALESCE(:wardNumber, w.ward_number),
//                                                                                                population = COALESCE(:population, w.population),
//
//                                                                                                geometry = COALESCE(geom.g, w.geometry),
//
//                                                                                                center = CASE\s
//                                                                                                    WHEN geom.g IS NOT NULL\s
//                                                                                                    THEN ST_PointOnSurface(geom.g)
//                                                                                                    ELSE w.center
//                                                                                                END,
//
//                                                                                                area_sq_km = CASE\s
//                                                                                                    WHEN geom.g IS NOT NULL\s
//                                                                                                    THEN ST_Area(geom.g::geography) / 1000000
//                                                                                                    ELSE w.area_sq_km
//                                                                                                END,
//
//                                                                                                updated_at = NOW()
//
//                                                                                            FROM geom, ward_city
//                                                                                            WHERE w.id = :wardId
//                                                                                              AND (
//                                                                                                    geom.g IS NULL OR (
//                                                                                                        ST_CoveredBy(geom.g, ward_city.cg)
//                                                                                                        AND NOT EXISTS (
//                                                                                                            SELECT 1 FROM wards w2
//                                                                                                            WHERE w2.city_id = ward_city.city_id
//                                                                                                              AND w2.id != :wardId
//                                                                                                              AND ST_Intersects(w2.geometry, geom.g)
//                                                                                                        )
//                                                                                                    )
//                                                                                              )
//
//                                                                                            RETURNING
//                                                                                                w.id,
//                                                                                                w.name,
//                                                                                                w.city_id,
//                                                                                                w.ward_number,
//                                                                                                w.population,
//                                                                                                w.area_sq_km,
//                                                                                                ST_AsGeoJSON(w.geometry) AS geometry,
//                                                                                                ST_AsGeoJSON(w.center) AS center,
//                                                                                                w.created_at,
//                                                                                                w.updated_at;
//    """;
//
//        Query query = entityManager.createNativeQuery(sql);
//
//        query.setParameter("wardId", wardId);
//        query.setParameter("name", name);
//        query.setParameter("wardNumber", wardNumber);
//        query.setParameter("population", population);
//        query.setParameter("geoJson", geoJson);
//
//        Object resultObj = query.getResultStream().findFirst().orElse(null);
//
//        if (resultObj == null) {
//            throw new RuntimeException("Ward not found OR geometry outside city");
//        }
//
//        Object[] result = (Object[]) resultObj;
//
//        Map<String, Object> map = new HashMap<>();
//
//        map.put("id", result[0]);
//        map.put("name", result[1]);
//        map.put("city_id", result[2]);
//        map.put("ward_number", result[3]);
//        map.put("population", result[4]);
//        map.put("area_sq_km", result[5]);
//        map.put("geometry", result[6]);
//        map.put("center", result[7]);
//        map.put("created_at", result[8]);
//        map.put("updated_at", result[9]);
//
//        return map;
//    }



}
