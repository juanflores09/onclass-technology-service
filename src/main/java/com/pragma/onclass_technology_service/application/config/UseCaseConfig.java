package com.pragma.onclass_technology_service.application.config;

import com.pragma.onclass_technology_service.domain.api.CreateTechnologyServicePort;
import com.pragma.onclass_technology_service.domain.api.FindTechnologiesServicePort;
import com.pragma.onclass_technology_service.domain.spi.TechnologyPersistencePort;
import com.pragma.onclass_technology_service.domain.usecase.CreateTechnologyUseCase;
import com.pragma.onclass_technology_service.domain.usecase.FindTechnologiesUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreateTechnologyServicePort technologyServicePort(TechnologyPersistencePort technologyPersistencePort){
        return new CreateTechnologyUseCase(technologyPersistencePort);
    }

    @Bean
    public FindTechnologiesServicePort findTechnologiesServicePort(TechnologyPersistencePort technologyPersistencePort){
        return new FindTechnologiesUseCase(technologyPersistencePort);
    }
}
