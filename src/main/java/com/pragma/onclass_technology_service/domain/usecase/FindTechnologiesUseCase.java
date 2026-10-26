package com.pragma.onclass_technology_service.domain.usecase;

import com.pragma.onclass_technology_service.domain.api.FindTechnologiesServicePort;
import com.pragma.onclass_technology_service.domain.model.Technology;
import com.pragma.onclass_technology_service.domain.spi.TechnologyPersistencePort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class FindTechnologiesUseCase implements FindTechnologiesServicePort {

    private final TechnologyPersistencePort repositoryPort;

    public FindTechnologiesUseCase(TechnologyPersistencePort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public Mono<Boolean> areAllTechnologiesExist(List<Long> ids) {
        return repositoryPort.areAllTechnologiesExist(ids);
    }

    @Override
    public Flux<Technology> findByIdIn(List<Long> ids) {
        return repositoryPort.findByIdIn(ids);
    }
}
