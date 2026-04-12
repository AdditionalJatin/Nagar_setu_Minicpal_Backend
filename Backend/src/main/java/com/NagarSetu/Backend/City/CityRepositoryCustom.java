package com.NagarSetu.Backend.City;

import java.util.Map;
import java.util.UUID;

public interface CityRepositoryCustom {
    Map<String, Object> insertCity(
            String name,
            String state,
            String country,
            Long population,
            String geoJson
    );

    Map<String, Object> updateCityWithoutGeometry(
            UUID cityId,
            String name,
            String state,
            String country,
            Long population
    );

    Map<String, Object> updateCityWithGeometry(
            UUID cityId,
            String name,
            String state,
            String country,
            Long population,
            String geoJson
    );

    void deleteCity(UUID cityId);


}
