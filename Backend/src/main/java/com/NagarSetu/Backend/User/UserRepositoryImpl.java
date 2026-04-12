package com.NagarSetu.Backend.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.persistence.Query;
import java.util.HashMap;

import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepoCustom {

    @PersistenceContext
    private EntityManager entityManager;





    @Override
    public Map<String, Object> registerUser(
            String phone,
            String password,
            String name,
            String email,
            String geoJson
    ) {

        String sql = """
        WITH user_point AS (
            SELECT ST_SetSRID(
                ST_GeomFromGeoJSON(:geoJson),
                4326
            ) AS pt
        ),
        ward_match AS (
            SELECT w.id AS ward_id, w.city_id
            FROM wards w, user_point up
            WHERE ST_Covers(w.geometry, up.pt)
            LIMIT 1
        )

        INSERT INTO users (
            id,
            phone,
            password,
            name,
            email,
            role,
            status,
            location,
            ward_id,
            city_id,
            is_verified,
            created_at,
            updated_at
        )
        SELECT
            gen_random_uuid(),
            :phone,
            :password,
            :name,
            :email,
            'CITIZEN',
            'PENDING',
            up.pt,
            wm.ward_id,
            wm.city_id,
            true,
            NOW(),
            NOW()
        FROM user_point up
        JOIN ward_match wm ON true

        RETURNING
            id,
            phone,
            name,
            ward_id,
            city_id,
            ST_AsGeoJSON(location) AS location;
    """;

        Query query = entityManager.createNativeQuery(sql);

        query.setParameter("phone", phone);
        query.setParameter("password", password);
        query.setParameter("name", name);
        query.setParameter("email", email);
        query.setParameter("geoJson", geoJson);

        Object resultObj = query.getResultStream().findFirst().orElse(null);

        if (resultObj == null) {
            throw new RuntimeException("User location not inside any ward");
        }

        Object[] result = (Object[]) resultObj;

        Map<String, Object> map = new HashMap<>();
        map.put("id", result[0]);
        map.put("phone", result[1]);
        map.put("name", result[2]);
        map.put("ward_id", result[3]);
        map.put("city_id", result[4]);
        map.put("location", result[5]);

        return map;
    }



}
