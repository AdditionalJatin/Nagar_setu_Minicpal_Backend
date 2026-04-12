package com.NagarSetu.Backend.Ward;

import java.util.Map;
import java.util.UUID;

public interface WardRepositoryCustom {
    Map<String, Object> insertWard(
            String name,
            UUID cityId,
            Integer wardNumber,
            Long population,
            String geoJson
    );


     Map<String, Object> updateWardWithoutGeometry(
            UUID wardId,
            String name,
            Integer wardNumber,
            Long population
    );

     Map<String, Object> updateWardWithGeometry(
            UUID wardId,
            String name,
            Integer wardNumber,
            Long population,
            String geoJson
    );

     void deleteWard(UUID wardId);


}
