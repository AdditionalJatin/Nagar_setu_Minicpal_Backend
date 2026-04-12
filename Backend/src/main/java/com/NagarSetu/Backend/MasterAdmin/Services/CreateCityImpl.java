package com.NagarSetu.Backend.MasterAdmin.Services;


import com.NagarSetu.Backend.City.CityRepository;
import com.NagarSetu.Backend.Exceptions.ResourceNotFoundException;
import com.NagarSetu.Backend.MasterAdmin.DTOs.CityListTResponseDTO;
import com.NagarSetu.Backend.MasterAdmin.DTOs.RegisterCItyResponseDTO;
import com.NagarSetu.Backend.MasterAdmin.DTOs.RegisterCityDTO;

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
public class CreateCityImpl implements CreateCity {
    private final CityRepository cityRepository;
    private final ObjectMapper mapper;

    @Override
    @Transactional
    public RegisterCItyResponseDTO createCity(RegisterCityDTO dto) {

        try {

            String geoJson = mapper.writeValueAsString(dto.getGeometry());

            if(dto.getName()==null || dto.getState()==null) {
                throw new IllegalArgumentException("name of City and state are required");
            }

            if(cityRepository.existsByName(dto.getName())) {
                throw new IllegalArgumentException("City with name " + dto.getName() + " already exists");
            }



            Map<String, Object> result = cityRepository.insertCity(
                    dto.getName(),
                    dto.getState(),
                    dto.getCountry(),
                    dto.getPopulation(),
                    geoJson
            );

            return RegisterCItyResponseDTO.builder()
                    .id((UUID) result.get("id"))
                    .name((String) result.get("city_name"))
                    .state((String) result.get("state"))
                    .country((String) result.get("country"))
                    .population((Long) result.get("population"))
                    .areaSqKm((Double) result.get("area_sq_km"))
                    .geometry(mapper.readValue((String) result.get("geometry"), Map.class))
                    .center(mapper.readValue((String) result.get("center"), Map.class))
                    .isActive((Boolean) result.get("is_active"))
                    .createdAt((LocalDateTime) result.get("created_at"))
                    .updatedAt((LocalDateTime) result.get("updated_at"))
                    .build();

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();

            throw new RuntimeException(
                    "Failed to create ward: " + e.getMessage(),
                    e
            );
        }
    }

    @Transactional
    @Override
    public void deleteCity(UUID cityId) {

        if (!cityRepository.existsById(cityId)) {
            throw new ResourceNotFoundException("City not found");
        }

        cityRepository.deleteCity(cityId);
    }


    @Override
    public List<CityListTResponseDTO> getAllCities() {
        return cityRepository.findAllByOrderByNameAsc()
                .stream()
                .map(city -> CityListTResponseDTO.builder()
                        .id(city.getId())
                        .name(city.getName())
                        .areaSqKm(city.getAreaSqKm())
                        .population(city.getPopulation())
                        .state(city.getState())
                        .build()
                )
                .toList();
    }


    @Override
    public RegisterCItyResponseDTO updateCity(UUID cityId, RegisterCityDTO dto) {

        try{

            if(!cityRepository.existsById(cityId)) {
                throw new IllegalArgumentException("City with id " + cityId + " does not exists");
            }



            Map<String, Object> result;

            if (dto.getGeometry() != null) {

                String geoJson = mapper.writeValueAsString(dto.getGeometry());

                result = cityRepository.updateCityWithGeometry(
                        cityId,
                        dto.getName(),
                        dto.getState(),
                        dto.getCountry(),
                        dto.getPopulation(),
                        geoJson
                );

            } else {

                result = cityRepository.updateCityWithoutGeometry(
                        cityId,
                        dto.getName(),
                        dto.getState(),
                        dto.getCountry(),
                        dto.getPopulation()
                );
            }




        if (result == null) {
            throw new IllegalArgumentException("City not found");
        }

        return RegisterCItyResponseDTO.builder()
                .id((UUID) result.get("id"))
                .name((String) result.get("city_name"))
                .state((String) result.get("state"))
                .country((String) result.get("country"))
                .population((Long) result.get("population"))
                .areaSqKm((Double) result.get("area_sq_km"))
                .geometry(mapper.readValue((String) result.get("geometry"), Map.class))
                .center(mapper.readValue((String) result.get("center"), Map.class))
                .isActive((Boolean) result.get("is_active"))
                .createdAt((LocalDateTime) result.get("created_at"))
                .updatedAt((LocalDateTime) result.get("updated_at"))
                .build();

    } catch (IllegalArgumentException e) {
        throw e;
    } catch (Exception e) {
            e.printStackTrace(); //

            throw new RuntimeException(
                    "Failed to create ward: " + e.getMessage(),
                    e
            );
    }
}

    }
