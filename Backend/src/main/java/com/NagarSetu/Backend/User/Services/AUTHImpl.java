package com.NagarSetu.Backend.User.Services;


import com.NagarSetu.Backend.City.CityRepository;
import com.NagarSetu.Backend.Entities.User;
import com.NagarSetu.Backend.Entities.UserRole;
import com.NagarSetu.Backend.Entities.UserStatus;
import com.NagarSetu.Backend.User.DTOs.RegisterRequestDTO;
import com.NagarSetu.Backend.User.DTOs.RegisterResponseDTO;
import com.NagarSetu.Backend.User.DTOs.UserLoginRequestDTO;
import com.NagarSetu.Backend.User.UserRepository;
import com.NagarSetu.Backend.Ward.WardRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AUTHImpl implements AUTH {
    private final WardRepository wardRepository;
    private final UserRepository userRepository;
    private final CityRepository cityRepository;
    private final ObjectMapper mapper;


    @Override
    public RegisterResponseDTO login(UserLoginRequestDTO request) {

        if(request.getPhone() == null || request.getPhone().isBlank()) {
            throw new IllegalArgumentException("Phone number is required");
        }
        if(request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        Map<String, Object> user = userRepository.findUserForLogin(request.getPhone());

        if (user == null || user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        // 🚫 Check status
        String statusStr = user.get("status").toString();
        if ("BLOCKED".equalsIgnoreCase(statusStr)) {
            throw new RuntimeException("User is Blocked");
        }

        String dbPassword = user.get("password").toString();
        if (!dbPassword.equals(request.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        Map<String, Object> locationMap = null;

        if (user.get("location") != null) {
            try {
                locationMap = mapper.readValue(
                        user.get("location").toString(),
                        Map.class
                );
            } catch (Exception e) {
                System.out.println("Invalid GeoJSON: " + user.get("location"));
                locationMap = null; // avoid crash
            }
        }


        return RegisterResponseDTO.builder()
                .id(UUID.fromString(user.get("id").toString()))
                .phone((String) user.get("phone"))
                .name((String) user.get("name"))
                .email((String) user.get("email"))
                .status(UserStatus.valueOf(statusStr.toUpperCase()))
                .role(UserRole.valueOf(user.get("role").toString()))
                .wardId(user.get("ward_id") != null
                        ? UUID.fromString(user.get("ward_id").toString())
                        : null)
                .cityId(user.get("city_id") != null
                        ? UUID.fromString(user.get("city_id").toString())
                        : null)
                .location(locationMap) // ✅ GeoJSON
                .build();

    }





    @Override
    public RegisterResponseDTO signUp(RegisterRequestDTO request) {

        try {

            if (request.getPassword() == null || request.getPassword().isBlank()) {
                throw new IllegalArgumentException("Password is required");
            }
            if (userRepository.findByPhone(request.getPhone()).isPresent()) {
                throw new RuntimeException("User already exists");
            }
            if (request.getLocation() == null) {
                throw new BadRequestException("Location is required");
            }
            String email = request.getEmail();


            if (email != null && email.isBlank()) {
                email = null;
            }


            String geoJson;
            try {
                geoJson = mapper.writeValueAsString(request.getLocation());
            } catch (Exception e) {
                throw new RuntimeException("Invalid GeoJSON");
            }

            Map<String, Object> result = userRepository.registerUser(
                    request.getPhone(),
                    request.getPassword(),
                    request.getName(),
                    email,
                    geoJson
            );




            return RegisterResponseDTO.builder()
                    .id((UUID) result.get("id"))
                    .phone((String) result.get("phone"))
                    .name((String) result.get("name"))
                    .email((String) result.get("email"))
                    .wardId((UUID) result.get("ward_id"))
                    .cityId((UUID) result.get("city_id"))
                    .location(result.get("location") != null
                            ? mapper.readValue((String) result.get("location"), Map.class)
                            : null)
                    .role(UserRole.CITIZEN)
                    .build();



        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }

    }

    private Map<String, Object> parsePoint(String wkt) {
        if (wkt == null) return null;

        // POINT(lng lat)
        wkt = wkt.replace("POINT(", "").replace(")", "");
        String[] parts = wkt.split(" ");

        Map<String, Object> map = new HashMap<>();
        map.put("lng", Double.parseDouble(parts[0]));
        map.put("lat", Double.parseDouble(parts[1]));

        return map;
    }



}
