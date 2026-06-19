package com.NagarSetu.Backend.Config;


import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.*;

import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

import java.time.Duration;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class RedisConfig {



    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        var serializer = GenericJacksonJsonRedisSerializer.builder()
                .build();

        RedisCacheConfiguration config =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofDays(1))
                        .serializeKeysWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(new StringRedisSerializer())
                        )
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(serializer)
                        )
                        .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();



//        var serializer = GenericJacksonJsonRedisSerializer.builder()
//                .build();
//
//
//        // 1. Setup secure type validation for Jackson 3
//        PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
//                .allowIfBaseType(Object.class)
//                .build();
//
//        // 2. Pass the validator to the builder to enable type tracking in Redis
//        var serializer = GenericJacksonJsonRedisSerializer.builder()
//                .enableDefaultTyping(typeValidator)
//                .build();
//
//        // 3. Keep your existing configuration untouched
//        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
//                .entryTtl(Duration.ofDays(1))
//                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
//                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
//                .disableCachingNullValues();
//
//        return RedisCacheManager.builder(connectionFactory)
//                .cacheDefaults(config)
//                .build();




    }

}
