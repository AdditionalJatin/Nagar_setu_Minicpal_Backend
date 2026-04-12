package com.NagarSetu.Backend.City;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import jakarta.persistence.Query;
import java.util.HashMap;

import java.util.Map;
import java.util.UUID;

@Repository
public class CityRepositoryImpl implements CityRepositoryCustom{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void deleteCity(UUID cityId) {

        String sql = """
        DELETE FROM cities
        WHERE id = :cityId
    """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("cityId", cityId);

        int rows = query.executeUpdate();

        if (rows == 0) {
            throw new RuntimeException("City not found");
        }
    }



    @Override
    public Map<String, Object> insertCity(
            String name,
            String state,
            String country,
            Long population,
            String geoJson
    ) {

        String sql = """
                WITH geom AS (
                    SELECT ST_SetSRID(
                        ST_MakeValid(ST_GeomFromGeoJSON(:geoJson)),
                        4326
                    ) AS g
                )
                INSERT INTO cities
                (id, city_name, state, country, population, area_sq_km, geometry, center, is_active, created_at, updated_at)
                SELECT
                    gen_random_uuid(),
                    :name,
                    :state,
                    :country,
                    :population,
                    ST_Area(g::geography) / 1000000,
                    g,
                    ST_PointOnSurface(g),
                    true,
                    now(),
                    now() FROM geom
                                     RETURNING\s
                                         id,
                                         city_name,
                                         state,
                                         country,
                                         population,
                                         area_sq_km,
                                         ST_AsGeoJSON(geometry),
                                         ST_AsGeoJSON(center),
                                         is_active,
                                         created_at,
                                         updated_at
                """;

        Query query = entityManager.createNativeQuery(sql);

        query.setParameter("name", name);
        query.setParameter("state", state);
        query.setParameter("country", country);
        query.setParameter("population", population);
        query.setParameter("geoJson", geoJson);

        Object[] result = (Object[]) query.getSingleResult();

        Map<String, Object> map = new HashMap<>();

        map.put("id", result[0]);
        map.put("city_name", result[1]);
        map.put("state", result[2]);
        map.put("country", result[3]);
        map.put("population", result[4]);
        map.put("area_sq_km", result[5]);
        map.put("geometry", result[6]);
        map.put("center", result[7]);
        map.put("is_active", result[8]);
        map.put("created_at", result[9]);
        map.put("updated_at", result[10]);

        return map;
    }

    @Override
    public Map<String, Object> updateCityWithoutGeometry(
            UUID cityId,
            String name,
            String state,
            String country,
            Long population
    ) {

        String sql = """
        UPDATE cities c
        SET
            city_name = COALESCE(:name, c.city_name),
            state = COALESCE(:state, c.state),
            country = COALESCE(:country, c.country),
            population = COALESCE(:population, c.population),
            updated_at = now()
        WHERE c.id = :cityId

        RETURNING
            c.id,
            c.city_name,
            c.state,
            c.country,
            c.population,
            c.area_sq_km,
            ST_AsGeoJSON(c.geometry) AS geometry,
            ST_AsGeoJSON(c.center) AS center,
            c.is_active,
            c.created_at,
            c.updated_at
    """;

        Query query = entityManager.createNativeQuery(sql);

        query.setParameter("cityId", cityId);
        query.setParameter("name", name);
        query.setParameter("state", state);
        query.setParameter("country", country);
        query.setParameter("population", population);

        Object resultObj = query.getResultStream().findFirst().orElse(null);

        if (resultObj == null) return null;

        return mapResult((Object[]) resultObj);
    }


    @Override
    public Map<String, Object> updateCityWithGeometry(
            UUID cityId,
            String name,
            String state,
            String country,
            Long population,
            String geoJson
    ) {

        String sql = """
        WITH geom AS (
            SELECT ST_SetSRID(
                ST_MakeValid(ST_GeomFromGeoJSON(:geoJson)),
                4326
            ) AS g
        )
        UPDATE cities c
        SET
            city_name = COALESCE(:name, c.city_name),
            state = COALESCE(:state, c.state),
            country = COALESCE(:country, c.country),
            population = COALESCE(:population, c.population),

            geometry = geom.g,
            center = ST_PointOnSurface(geom.g),
            area_sq_km = ST_Area(geom.g::geography) / 1000000,

            updated_at = now()

        FROM geom
        WHERE c.id = :cityId

        RETURNING
            c.id,
            c.city_name,
            c.state,
            c.country,
            c.population,
            c.area_sq_km,
            ST_AsGeoJSON(c.geometry) AS geometry,
            ST_AsGeoJSON(c.center) AS center,
            c.is_active,
            c.created_at,
            c.updated_at
    """;

        Query query = entityManager.createNativeQuery(sql);

        query.setParameter("cityId", cityId);
        query.setParameter("name", name);
        query.setParameter("state", state);
        query.setParameter("country", country);
        query.setParameter("population", population);
        query.setParameter("geoJson", geoJson);

        Object resultObj = query.getResultStream().findFirst().orElse(null);

        if (resultObj == null) return null;

        return mapResult((Object[]) resultObj);
    }
    private Map<String, Object> mapResult(Object[] result) {

        Map<String, Object> map = new HashMap<>();

        map.put("id", result[0]);
        map.put("city_name", result[1]);
        map.put("state", result[2]);
        map.put("country", result[3]);
        map.put("population", result[4]);
        map.put("area_sq_km", result[5]);
        map.put("geometry", result[6]);
        map.put("center", result[7]);
        map.put("is_active", result[8]);
        map.put("created_at", result[9]);
        map.put("updated_at", result[10]);

        return map;
    }


//
//    @Override
//    public Map<String, Object> updateCity(
//            UUID cityId,
//            String name,
//            String state,
//            String country,
//            Long population,
//            String geoJson
//    ) {
//
//        String sql = """
//        WITH geom AS (
//            SELECT
//                CASE
//                    WHEN :geoJson IS NOT NULL
//                    THEN ST_SetSRID(
//                            ST_MakeValid(ST_GeomFromGeoJSON(:geoJson)),
//                            4326
//                         )
//                    ELSE NULL
//                END AS g
//        )
//        UPDATE cities c
//        SET
//            city_name = COALESCE(:name, c.city_name),
//            state = COALESCE(:state, c.state),
//            country = COALESCE(:country, c.country),
//            population = COALESCE(:population, c.population),
//
//            geometry = COALESCE(geom.g, c.geometry),
//
//            center = CASE
//                WHEN geom.g IS NOT NULL
//                THEN ST_PointOnSurface(geom.g)
//                ELSE c.center
//            END,
//
//            area_sq_km = CASE
//                WHEN geom.g IS NOT NULL
//                THEN ST_Area(geom.g::geography) / 1000000
//                ELSE c.area_sq_km
//            END,
//
//            updated_at = now()
//
//        FROM geom
//        WHERE c.id = :cityId
//
//        RETURNING
//            c.id,
//            c.city_name,
//            c.state,
//            c.country,
//            c.population,
//            c.area_sq_km,
//            ST_AsGeoJSON(c.geometry) AS geometry,
//            ST_AsGeoJSON(c.center) AS center,
//            c.is_active,
//            c.created_at,
//            c.updated_at
//    """;
//
//        Query query = entityManager.createNativeQuery(sql);
//
//        query.setParameter("cityId", cityId);
//        query.setParameter("name", name);
//        query.setParameter("state", state);
//        query.setParameter("country", country);
//        query.setParameter("population", population);
//        query.setParameter("geoJson", geoJson);
//
//        Object resultObj = query.getResultStream().findFirst().orElse(null);
//
//        if (resultObj == null) {
//            return null; // city not found
//        }
//
//        Object[] result = (Object[]) resultObj;
//
//        Map<String, Object> map = new HashMap<>();
//
//        map.put("id", result[0]);
//        map.put("city_name", result[1]);
//        map.put("state", result[2]);
//        map.put("country", result[3]);
//        map.put("population", result[4]);
//        map.put("area_sq_km", result[5]);
//        map.put("geometry", result[6]); // now NOT null because alias fixed
//        map.put("center", result[7]);
//        map.put("is_active", result[8]);
//        map.put("created_at", result[9]);
//        map.put("updated_at", result[10]);
//
//        return map;
//    }

}
