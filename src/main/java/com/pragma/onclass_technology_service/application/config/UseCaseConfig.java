package com.pragma.onclass_technology_service.application.config;

import com.pragma.onclass_technology_service.domain.api.TechnologyServicePort;
import com.pragma.onclass_technology_service.domain.spi.TechnologyPersistencePort;
import com.pragma.onclass_technology_service.domain.usecase.TechnologyUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public TechnologyServicePort technologyServicePort(TechnologyPersistencePort technologyPersistencePort){
        return new TechnologyUseCase(technologyPersistencePort);
    }
}
