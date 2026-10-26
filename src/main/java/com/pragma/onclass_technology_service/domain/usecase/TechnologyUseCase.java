package com.pragma.onclass_technology_service.domain.usecase;

import com.pragma.onclass_technology_service.domain.api.TechnologyServicePort;
import com.pragma.onclass_technology_service.domain.emuns.TechnicalMessage;
import com.pragma.onclass_technology_service.domain.exceptions.BusinessException;
import com.pragma.onclass_technology_service.domain.spi.TechnologyPersistencePort;
import com.pragma.onclass_technology_service.domain.model.Technology;
import reactor.core.publisher.Mono;

import java.util.List;

public class TechnologyUseCase implements TechnologyServicePort {

    private final TechnologyPersistencePort repositoryPort;

    public TechnologyUseCase(TechnologyPersistencePort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public Mono<Technology> createTechnology(Technology technology) {
        return repositoryPort.existsByName(technology.getName())
                .filter(exists -> !exists)
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.TECHNOLOGY_ALREADY_EXISTS)))
                .flatMap(exists ->repositoryPort.save(technology));
    }

    @Override
    public Mono<Boolean> areAllTechnologiesExist(List<Long> ids) {
        return repositoryPort.areAllTechnologiesExist(ids);
    }
}
