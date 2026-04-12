package com.NagarSetu.Backend.Complaint;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import jakarta.persistence.Query;
import java.util.HashMap;

import java.util.Map;
import java.util.UUID;



@Repository
public class ComplaintRepositoryImpl implements ComplaintCustom {
    @PersistenceContext
    private EntityManager entityManager;



    @Override
    public Map<String, Object> registerComplaint(
            String title,
            String description,
            String department,
            String priority,
            UUID userId,
            String geoJson,
            UUID wardId
    ) {

        String sql = """
    WITH complaint_point AS (
        SELECT ST_SetSRID(
            ST_GeomFromGeoJSON(:geoJson),
            4326
        ) AS pt
    ),
    ward_data AS (
        SELECT w.id AS ward_id, w.city_id, w.geometry
        FROM wards w
        WHERE w.id = :wardId
    ),
    validation AS (
        SELECT wd.ward_id, wd.city_id
        FROM ward_data wd, complaint_point cp
        WHERE ST_Covers(wd.geometry, cp.pt)
    )

    INSERT INTO complaints (
        id,
        title,
        description,
        department,
        status,
        complaint_type,
        priority,
        created_by,
        location,
        ward_id,
        city_id,
        photo_count,
        created_at,
        updated_at,
        last_remark,
        remarked_by
    )
    SELECT
        gen_random_uuid(),
        :title,
        :description,
        :department,
        'INITIATED',
        'NORMAL',
        :priority,
        :userId,
        cp.pt,
        v.ward_id,
        v.city_id,
        0,
        NOW(),
        NOW(),
        'Complaint registered',
        'CITIZEN'
    
    FROM complaint_point cp
    JOIN validation v ON true

    RETURNING id,
    ST_AsGeoJSON(location) AS location;
""";


        Query query = entityManager.createNativeQuery(sql);

        query.setParameter("title", title);
        query.setParameter("description", description);
        query.setParameter("department", department);
        query.setParameter("priority", priority);
        query.setParameter("userId", userId);
        query.setParameter("geoJson", geoJson);
        query.setParameter("wardId", wardId);

        Object resultObj = query.getResultStream().findFirst().orElse(null);

        if (resultObj == null) {
            throw new RuntimeException("Complaint location not inside user ward");
        }

        Object[] result = (Object[]) resultObj;

        Map<String, Object> map = new HashMap<>();
        map.put("id", result[0]);
        map.put("location", result[1]);

        return map;
    }




}
