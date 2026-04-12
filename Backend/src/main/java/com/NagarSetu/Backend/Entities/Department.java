package com.NagarSetu.Backend.Entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Department {

    SANITATION(
            "Sanitation",
            "Handles garbage collection, waste management",
            24, // SLA in hours
            Priority.LOW
    ),

    WATER_SUPPLY(
            "Water Supply",
            "Water leakage, supply issues",
            12,
            Priority.HIGH
    ),

    ELECTRICITY(
            "Electricity",
            "Street lights, power failures",
            6,
            Priority.HIGH
    ),

    ROADS(
            "Roads",
            "Potholes, road damage",
            48,
            Priority.MEDIUM
    ),

    DRAINAGE(
            "Drainage",
            "Sewage blockage, drainage overflow",
            24,
            Priority.HIGH
    ),

    PUBLIC_HEALTH(
            "Public Health",
            "Mosquito, hygiene, disease control",
            24,
            Priority.HIGH
    ),

    PARKS_AND_GARDENS(
            "Parks & Gardens",
            "Park maintenance, greenery",
            72,
            Priority.LOW
    ),

    TRAFFIC(
            "Traffic",
            "Signals, congestion issues",
            12,
            Priority.MEDIUM
    ),

    ENCROACHMENT(
            "Encroachment",
            "Illegal constructions",
            72,
            Priority.LOW
    ),

    FIRE(
            "Fire Department",
            "Fire hazards and emergencies",
            2,
            Priority.CRITICAL
    ),

    OTHER(
            "Other",
            "Miscellaneous complaints",
            48,
            Priority.LOW
    );

    private final String displayName;
    private final String description;
    private final int slaHours;
    private final Priority priority;


    public static Department fromString(String value) {

        if (value == null || value.isBlank()) {
            return null; // or throw exception if you want strict validation
        }

        for (Department d : Department.values()) {
            if (d.name().equalsIgnoreCase(value) ||
                    d.getDisplayName().equalsIgnoreCase(value)) {
                return d;
            }
        }

        throw new IllegalArgumentException("Invalid Department: " + value);
    }

}
