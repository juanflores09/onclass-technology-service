package com.pragma.onclass_technology_service.domain.usecase;

import com.pragma.onclass_technology_service.domain.api.CreateTechnologyServicePort;
import com.pragma.onclass_technology_service.domain.emuns.TechnicalMessage;
import com.pragma.onclass_technology_service.domain.exceptions.BusinessException;
import com.pragma.onclass_technology_service.domain.spi.TechnologyPersistencePort;
import com.pragma.onclass_technology_service.domain.model.Technology;
import reactor.core.publisher.Mono;

public class CreateTechnologyUseCase implements CreateTechnologyServicePort {

    private final TechnologyPersistencePort repositoryPort;

    public CreateTechnologyUseCase(TechnologyPersistencePort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public Mono<Technology> createTechnology(Technology technology) {
        return repositoryPort.existsByName(technology.getName())
                .filter(exists -> !exists)
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.TECHNOLOGY_ALREADY_EXISTS)))
                .flatMap(exists ->repositoryPort.save(technology));
    }
}
