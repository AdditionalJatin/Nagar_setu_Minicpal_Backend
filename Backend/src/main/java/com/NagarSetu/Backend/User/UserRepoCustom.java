package com.NagarSetu.Backend.User;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Map;

public interface UserRepoCustom {

    Map<String, Object> registerUser(
            String phone,
            String password,
            String name,
            String email,
            String geoJson
    );






}
