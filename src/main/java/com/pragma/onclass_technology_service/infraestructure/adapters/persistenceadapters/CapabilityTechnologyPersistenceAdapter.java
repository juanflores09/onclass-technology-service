package com.pragma.onclass_technology_service.infraestructure.adapters.persistenceadapters;

import com.pragma.onclass_technology_service.domain.spi.CapabilityTechnologyPersistencePort;
import com.pragma.onclass_technology_service.infraestructure.adapters.persistenceadapters.entity.CapabilityTechnologyEntity;
import com.pragma.onclass_technology_service.infraestructure.adapters.persistenceadapters.repository.SpringDataCapabilityTechnologyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CapabilityTechnologyPersistenceAdapter implements CapabilityTechnologyPersistencePort {

    private final SpringDataCapabilityTechnologyRepository capabilityTechnologyRepository;

    @Override
    public Mono<Void> saveAll(Long capabilityId, List<Long> technologyIds) {
        return Flux.fromIterable(technologyIds)
                .map(techId -> {
                    CapabilityTechnologyEntity entity = new CapabilityTechnologyEntity();
                    entity.setCapabilityId(capabilityId);
                    entity.setTechnologyId(techId);
                    return entity;
                })
                .flatMap(capabilityTechnologyRepository::save, 5)
                .then();
    }
}
