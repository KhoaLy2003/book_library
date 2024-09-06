package com.khoaly.book_library.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper
                .getConfiguration()
                .setFieldMatchingEnabled(true)
                .setAmbiguityIgnored(true)
                .setSkipNullEnabled(true)
                .setMatchingStrategy(MatchingStrategies.STANDARD);
        return modelMapper;
    }
}
