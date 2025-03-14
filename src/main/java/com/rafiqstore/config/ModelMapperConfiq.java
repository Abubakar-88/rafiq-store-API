package com.rafiqstore.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfiq {
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
}
