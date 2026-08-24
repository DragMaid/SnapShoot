package com.snapshoot.gateway.config;

import org.msgpack.jackson.dataformat.MessagePackFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean(name = "msgpackObjectMapper")
    public ObjectMapper msgpackObjectMapper() {
        return new ObjectMapper(new MessagePackFactory());
    }
}
