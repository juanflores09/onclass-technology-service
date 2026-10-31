package com.pragma.onclass_technology_service.domain.usecase;

import com.pragma.onclass_technology_service.domain.api.FindTechnologiesServicePort;
import com.pragma.onclass_technology_service.domain.model.CapabilityTechnologies;
import com.pragma.onclass_technology_service.domain.spi.TechnologyPersistencePort;
import reactor.core.publisher.Flux;

import java.util.List;

public class FindTechnologiesUseCase implements FindTechnologiesServicePort {

    private final TechnologyPersistencePort repositoryPort;

    public FindTechnologiesUseCase(TechnologyPersistencePort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public Flux<CapabilityTechnologies> findTechnologiesByCapabilityId(List<Long> capabilityIds) {
        return repositoryPort.findTechnologiesByCapabilityIds(capabilityIds);
    }
}
