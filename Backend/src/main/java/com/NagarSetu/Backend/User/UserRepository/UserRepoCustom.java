package com.NagarSetu.Backend.User.UserRepository;

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
