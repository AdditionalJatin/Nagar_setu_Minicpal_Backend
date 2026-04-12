package com.NagarSetu.Backend.MasterAdmin.Services;

import com.NagarSetu.Backend.City.CityRepository;
import com.NagarSetu.Backend.Exceptions.ResourceNotFoundException;
import com.NagarSetu.Backend.MasterAdmin.DTOs.RegisterWardDTO;
import com.NagarSetu.Backend.MasterAdmin.DTOs.RegisterWardResponseDTO;
import com.NagarSetu.Backend.MasterAdmin.DTOs.WardListResponseDTO;
import com.NagarSetu.Backend.Ward.WardRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WardCRUDImpl implements WardCRUD {

    private final WardRepository wardRepository;
    private final ObjectMapper mapper;
    private final CityRepository cityRepository;

    // =========================
    // ✅ CREATE WARD
    // =========================
    @Override
    public List<WardListResponseDTO> getWardsByCity(UUID cityId) {

        if (!cityRepository.existsById(cityId)) {
            throw new RuntimeException("City not found");
        }

        List<Object[]> rows = wardRepository.getWardListByCity(cityId);

        return rows.stream().map(r -> WardListResponseDTO.builder()
                .id((UUID) r[0])
                .name((String) r[1])
                .wardNumber((Integer) r[2])
                .population((Integer) r[3])
                .areaSqKm((Double) r[4])
                .build()
        ).toList();
    }



    @Override
    @Transactional
    public RegisterWardResponseDTO createWard(RegisterWardDTO dto) {

        try {

            // ✅ Basic validation
            if (dto.getName() == null || dto.getCityId() == null) {
                throw new IllegalArgumentException("Ward name and cityId are required");
            }

            if (dto.getGeometry() == null) {
                throw new IllegalArgumentException("Geometry is required");
            }

            // ✅ City must exist
            if (!cityRepository.existsById(dto.getCityId())) {
                throw new ResourceNotFoundException("City does not exist");
            }

            // ✅ Ward name unique in city
            if (wardRepository.existsByCity_IdAndName(dto.getCityId(), dto.getName())) {
                throw new IllegalArgumentException("Ward name already exists in this city");
            }

            // =========================
            // ✅ Ward number logic
            // =========================
            Integer wardNumber;

            if (dto.getWardNumber() != null) {

                if (wardRepository.existsByCity_IdAndWardNumber(
                        dto.getCityId(),
                        dto.getWardNumber()
                )) {
                    throw new IllegalArgumentException("Ward number already exists in this city");
                }

                wardNumber = dto.getWardNumber();

            } else {
                wardNumber = wardRepository.getNextWardNumber(dto.getCityId());
            }

            // =========================
            // ✅ Convert GeoJSON
            // =========================
            String geoJson;
            try {
                geoJson = mapper.writeValueAsString(dto.getGeometry());
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid GeoJSON format");
            }

            // =========================
            // ✅ DB Insert
            // =========================
            Map<String, Object> result = wardRepository.insertWard(
                    dto.getName(),
                    dto.getCityId(),
                    wardNumber,
                    dto.getPopulation(),
                    geoJson
            );

            if (result == null) {
                throw new IllegalArgumentException(
                        "Ward creation failed: outside city OR overlapping OR invalid geometry"
                );
            }

            return mapToResponse(result);

        } catch (IllegalArgumentException e) {
            throw e;

        } catch (Exception e) {
            e.printStackTrace(); // 🔥 CRITICAL FOR DEBUG

            throw new RuntimeException(
                    "Failed to create ward: " + e.getMessage(),
                    e
            );
        }
    }

    // =========================
    // ✅ UPDATE WARD
    // =========================
    @Override
    @Transactional
    public RegisterWardResponseDTO updateWard(UUID wardId, RegisterWardDTO dto) {

        try {

            if (wardId == null) {
                throw new IllegalArgumentException("Ward ID is required");
            }

            var ward = wardRepository.findById(wardId)
                    .orElseThrow(() -> new ResourceNotFoundException("Ward not found"));

            UUID cityId = ward.getCity().getId();

            // ✅ Name validation
            if (dto.getName() != null) {
                if (wardRepository.existsByCity_IdAndName(cityId, dto.getName())) {
                    throw new IllegalArgumentException("Ward name already exists in this city");
                }
            }

            // ✅ Ward number validation
            if (dto.getWardNumber() != null) {
                if (wardRepository.existsByCity_IdAndWardNumberAndIdNot(
                        cityId,
                        dto.getWardNumber(),
                        wardId
                )) {
                    throw new IllegalArgumentException("Ward number already exists in this city");
                }
            }

            Map<String, Object> result;

            if (dto.getGeometry() != null) {

                String geoJson = mapper.writeValueAsString(dto.getGeometry());

                result = wardRepository.updateWardWithGeometry(
                        wardId,
                        dto.getName(),
                        dto.getWardNumber(),
                        dto.getPopulation(),
                        geoJson
                );

            } else {

                result = wardRepository.updateWardWithoutGeometry(
                        wardId,
                        dto.getName(),
                        dto.getWardNumber(),
                        dto.getPopulation()
                );
            }

            if (result == null) {
                throw new IllegalArgumentException(
                        "Update failed: invalid geometry OR overlap OR outside city"
                );
            }

            return mapToResponse(result);

        } catch (IllegalArgumentException e) {
            throw e;

        } catch (Exception e) {
            e.printStackTrace();

            throw new RuntimeException(
                    "Failed to update ward: " + e.getMessage(),
                    e
            );
        }
    }
    @Override
    @Transactional
    public void deleteWard(UUID wardId) {

        if (!wardRepository.existsById(wardId)) {
            throw new ResourceNotFoundException("Ward not found");
        }

        wardRepository.deleteWard(wardId);
    }

    // =========================
    // ✅ MAPPING
    // =========================
    private RegisterWardResponseDTO mapToResponse(Map<String, Object> result) throws Exception {

        return RegisterWardResponseDTO.builder()
                .id((UUID) result.get("id"))
                .name((String) result.get("name"))
                .cityId((UUID) result.get("city_id"))
                .wardNumber((Integer) result.get("ward_number"))

                .population(
                        result.get("population") != null
                                ? ((Number) result.get("population")).longValue()
                                : null
                )

                .areaSqKm((Double) result.get("area_sq_km"))

                .geometry(
                        result.get("geometry") != null
                                ? mapper.readValue((String) result.get("geometry"), Map.class)
                                : null
                )
                .center(
                        result.get("center") != null
                                ? mapper.readValue((String) result.get("center"), Map.class)
                                : null
                )

                .createdAt((LocalDateTime) result.get("created_at"))
                .updatedAt((LocalDateTime) result.get("updated_at"))
                .build();
    }
}