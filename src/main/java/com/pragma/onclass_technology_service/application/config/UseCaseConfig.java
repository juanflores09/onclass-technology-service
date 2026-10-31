package com.pragma.onclass_technology_service.application.config;

import com.pragma.onclass_technology_service.domain.api.CreateTechnologyServicePort;
import com.pragma.onclass_technology_service.domain.api.DeleteTechnologiesServicePort;
import com.pragma.onclass_technology_service.domain.api.FindTechnologiesServicePort;
import com.pragma.onclass_technology_service.domain.api.SaveCapabilityTechnologyServicePort;
import com.pragma.onclass_technology_service.domain.spi.CapabilityTechnologyPersistencePort;
import com.pragma.onclass_technology_service.domain.spi.TechnologyPersistencePort;
import com.pragma.onclass_technology_service.domain.usecase.CreateTechnologyUseCase;
import com.pragma.onclass_technology_service.domain.usecase.DeleteTechnologiesByCapabilityUseCase;
import com.pragma.onclass_technology_service.domain.usecase.FindTechnologiesUseCase;
import com.pragma.onclass_technology_service.domain.usecase.SaveCapabilityTechnologyUseCase;
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

    @Bean
    public SaveCapabilityTechnologyServicePort saveCapabilityTechnologyServicePort(
            TechnologyPersistencePort technologyPersistencePort,
            CapabilityTechnologyPersistencePort capabilityTechnologyPersistencePort){
        return new SaveCapabilityTechnologyUseCase(technologyPersistencePort, capabilityTechnologyPersistencePort);
    }

    @Bean
    public DeleteTechnologiesServicePort deleteTechnologiesServicePort(
            CapabilityTechnologyPersistencePort capabilityTechnologyPersistencePort,
            TechnologyPersistencePort technologyPersistencePort){
        return new DeleteTechnologiesByCapabilityUseCase(capabilityTechnologyPersistencePort, technologyPersistencePort);
    }

}
