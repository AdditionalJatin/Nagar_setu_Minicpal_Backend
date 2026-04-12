package com.NagarSetu.Backend.Config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.n52.jackson.datatype.jts.JtsModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class JacksonConfig {
//    @Bean
//    public ObjectMapper objectMapper() {
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.registerModule(new JtsModule());
//        return mapper;
//    }
@Bean
public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.findAndRegisterModules(); // 🔥 important
    mapper.registerModule(new JtsModule());
    return mapper;
}

    @Bean
    public Module jtsModule() {
        return new JtsModule();  // ✅ auto-added to existing ObjectMapper
    }
}
