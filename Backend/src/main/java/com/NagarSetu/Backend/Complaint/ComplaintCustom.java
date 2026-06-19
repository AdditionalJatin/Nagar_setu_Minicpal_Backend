package com.NagarSetu.Backend.Complaint;

import com.NagarSetu.Backend.Entities.ComplaintStatus;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public interface ComplaintCustom {
    public Map<String, Object> registerComplaint(
            String title,
            String description,
            String department,
            String priority,
            UUID userId,
            String geoJson,
            UUID wardId
    );






}
