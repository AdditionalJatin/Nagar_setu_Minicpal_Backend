package com.NagarSetu.Backend.Config;

import org.modelmapper.ModelMapper;
import com.fasterxml.jackson.databind.ObjectMapper; // ✅ CORRECT
import org.n52.jackson.datatype.jts.JtsModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ProjectConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }





}