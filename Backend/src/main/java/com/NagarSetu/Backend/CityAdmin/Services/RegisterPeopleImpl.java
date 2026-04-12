package com.NagarSetu.Backend.CityAdmin.Services;


import com.NagarSetu.Backend.City.CityRepository;
import com.NagarSetu.Backend.CityAdmin.DTOs.CityPeopleRegisterDTO;
import com.NagarSetu.Backend.CityAdmin.DTOs.CityPeopleRegisterResponseDTO;
import com.NagarSetu.Backend.Entities.*;
import com.NagarSetu.Backend.User.UserRepository;
import com.NagarSetu.Backend.Ward.WardRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterPeopleImpl implements RegisterPeople {

    private final UserRepository userRepository;
    private final CityRepository cityRepository;
    private final WardRepository wardRepository;


    @Override
    @Transactional
    public CityPeopleRegisterResponseDTO registerPeople(CityPeopleRegisterDTO userDTO) {
        City city = cityRepository.findById(userDTO.getCityId())
                .orElseThrow(() -> new RuntimeException("City not with given id found"));




        if (userDTO.getRole() == null || userDTO.getRole().isEmpty()) {
            throw new RuntimeException("Role is required");
        }

        if (userDTO.getEmail() == null || userDTO.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (userDTO.getPassword() == null || userDTO.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (userDTO.getName() == null || userDTO.getName().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (userDTO.getPhone() == null || userDTO.getPhone().isBlank()) {
            throw new IllegalArgumentException("Phone is required");
        }



        if(userRepository.findByPhone(userDTO.getPhone()).isPresent()){
            throw new IllegalArgumentException("Phone number already in use");
        }

            UserRole role = UserRole.valueOf(userDTO.getRole());


            if (role == UserRole.DEPARTMENT_HEAD) {

                if(userDTO.getDepartment() == null || userDTO.getDepartment().isBlank()) {
                    throw new IllegalArgumentException("Department is required for department head");
                }

                User user = User.builder()
                        .name(userDTO.getName())
                        .phone(userDTO.getPhone())
                        .password(userDTO.getPassword())
                        .role(role)
                        .department(Department.fromString(userDTO.getDepartment()))
                        .email(userDTO.getEmail())
                        .city(city)
                        .isVerified(true)
                        .status(UserStatus.APPROVED)
                        .build();

                userRepository.save(user);

                return CityPeopleRegisterResponseDTO.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .role(user.getRole())
                        .status(user.getStatus())
                        .phone(user.getPhone())
                        .email(user.getEmail())
                        .department(
                                user.getDepartment() != null
                                        ? user.getDepartment().getDisplayName()
                                        : null
                        )
                        .cityId(user.getCity() != null ? user.getCity().getId() : null)
                        .wardId(user.getWard() != null ? user.getWard().getId() : null)
                        .build();




            } else if (role == UserRole.WARD_HEAD) {

                if(userDTO.getWardId()==null) {
                    throw new IllegalArgumentException("Ward ID is required for ward head");
                }
                Ward ward =wardRepository.findById(userDTO.getWardId())
                        .orElseThrow(() -> new RuntimeException("Ward not with given id found"));

                if(ward.getWardHead() != null) {
                    throw new RuntimeException("Ward already has a head");
                }


                User user = User.builder()
                        .name(userDTO.getName())
                        .phone(userDTO.getPhone())
                        .password(userDTO.getPassword())
                        .role(role)
                        .email(userDTO.getEmail())
                        .city(city)
                        .ward(ward)
                        .isVerified(true)
                        .status(UserStatus.APPROVED)
                        .build();

                userRepository.save(user);

                ward.setWardHead(user);
                wardRepository.save(ward);


                return CityPeopleRegisterResponseDTO.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .role(user.getRole())
                        .phone(user.getPhone())
                        .email(user.getEmail())
                        .status(user.getStatus())
                        .department(
                                user.getDepartment() != null
                                        ? user.getDepartment().getDisplayName()
                                        : null
                        )
                        .cityId(user.getCity() != null ? user.getCity().getId() : null)
                        .wardId(user.getWard() != null ? user.getWard().getId() : null)
                        .build();





            } else if (role == UserRole.WORKER) {
                if(userDTO.getDepartment() == null || userDTO.getDepartment().isBlank()) {
                    throw new IllegalArgumentException("Department is required for Worker");
                }
                Ward ward = null;
                if(userDTO.getWardId() != null) {
                    ward = wardRepository.findById(userDTO.getWardId())
                            .orElseThrow(() -> new RuntimeException("Ward not with given id found"));
                }

                User user = User.builder()
                        .name(userDTO.getName())
                        .phone(userDTO.getPhone())
                        .password(userDTO.getPassword())
                        .role(role)
                        .department(Department.fromString(userDTO.getDepartment()))
                        .email(userDTO.getEmail())
                        .city(city)
                        .ward(ward)
                        .isVerified(true)
                        .status(UserStatus.APPROVED)
                        .build();

                userRepository.save(user);

                return CityPeopleRegisterResponseDTO.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .role(user.getRole())
                        .phone(user.getPhone())
                        .email(user.getEmail())
                        .status(user.getStatus())
                        .department(
                                user.getDepartment() != null
                                        ? user.getDepartment().getDisplayName()
                                        : null
                        )
                        .cityId(user.getCity() != null ? user.getCity().getId() : null)
                        .wardId(user.getWard() != null ? user.getWard().getId() : null)
                        .build();






            }



        throw new IllegalArgumentException("Invalid role");
        }




}
