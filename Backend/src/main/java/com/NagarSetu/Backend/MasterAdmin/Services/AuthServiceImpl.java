package com.NagarSetu.Backend.MasterAdmin.Services;


import com.NagarSetu.Backend.City.CityRepository;
import com.NagarSetu.Backend.Entities.City;
import com.NagarSetu.Backend.Entities.User;
import com.NagarSetu.Backend.Entities.UserRole;
import com.NagarSetu.Backend.Entities.UserStatus;
import com.NagarSetu.Backend.MasterAdmin.DTOs.RegisterCityAdminRequestDTO;
import com.NagarSetu.Backend.MasterAdmin.DTOs.RegisterCityAdminResponseDTO;
import com.NagarSetu.Backend.MasterAdmin.DTOs.RegisterMasterAdminRequest;
import com.NagarSetu.Backend.MasterAdmin.DTOs.RegisterMasterAdminResponse;

import com.NagarSetu.Backend.User.UserRepository;
import com.NagarSetu.Backend.Ward.WardRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final CityRepository cityRepository;


    @Override
    @Transactional
    public void deleteMasterAdmin(UUID userId) {

        User user = userRepository.findByIdAndRole(userId, UserRole.MASTER_ADMIN)
                .orElseThrow(() -> new RuntimeException("Master Admin not found"));




        userRepository.delete(user);


    }

        @Override
        public void deleteCityAdmin(UUID userId) {
        User user = userRepository.findByIdAndRole(userId, UserRole.CITY_ADMIN)
                .orElseThrow(() -> new RuntimeException("City Admin not found"));

            City city = user.getCity();
            if (city != null) {
                city.setCityAdmin(null);
                cityRepository.save(city);
            }


            userRepository.delete(user);


        }

    @Transactional
    @Override
    public RegisterCityAdminResponseDTO registerCityAdmin(RegisterCityAdminRequestDTO userDTO) {

        City city = cityRepository.findById(userDTO.getCityId())
                .orElseThrow(() -> new RuntimeException("City not with given id found"));

        if (city.getCityAdmin() != null) {
            throw new RuntimeException("City already has an admin");
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
        if (userRepository.existsByPhone(userDTO.getPhone())) {
            throw new RuntimeException("User already exists with this phone");
        }
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists with this email");
        }


        User user = new User();

        user.setName(userDTO.getName());
        user.setPhone(userDTO.getPhone());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());

        user.setRole(UserRole.CITY_ADMIN);
        user.setStatus(UserStatus.APPROVED);
        user.setIsVerified(true);

        user.setCity(city);
        user.setWard(null); // important

        User savedUser = userRepository.save(user);

        city.setCityAdmin(savedUser);
        cityRepository.save(city);

        return modelMapper.map(savedUser, RegisterCityAdminResponseDTO.class);
    }




    @Override
    public RegisterMasterAdminResponse registerMasterAdmin(RegisterMasterAdminRequest userDTO) {
        if(userDTO.getEmail()==null||userDTO.getEmail().isBlank()){
            throw new IllegalArgumentException("Email is required");
        }
        if(userDTO.getPassword()==null||userDTO.getPassword().isBlank()){
            throw new IllegalArgumentException("Password is required");
        }
        if(userDTO.getName()==null||userDTO.getName().isBlank()){
            throw new IllegalArgumentException("Name is required");
        }
        if(userDTO.getPhone()==null||userDTO.getPhone().isBlank()) {
            throw new IllegalArgumentException("Phone is required");
        }
        if (userRepository.existsByPhone(userDTO.getPhone())) {
            throw new RuntimeException("User already exists with this phone");
        }
        if(userRepository.findByEmail(userDTO.getEmail()).isPresent()){
            throw new RuntimeException("User already exists with this email");
        }


        User user = modelMapper.map(userDTO, User.class);
        user.setRole(UserRole.MASTER_ADMIN);
        user.setStatus(UserStatus.APPROVED);
        user.setIsVerified(true);


    User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, RegisterMasterAdminResponse.class);
    }
}
