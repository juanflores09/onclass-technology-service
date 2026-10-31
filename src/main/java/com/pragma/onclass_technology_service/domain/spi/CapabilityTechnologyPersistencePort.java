package com.pragma.onclass_technology_service.domain.spi;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CapabilityTechnologyPersistencePort {

    Mono<Void> saveAll(Long capabilityId, List<Long> technologyIds);

    Flux<Long> findTechnologyIdsByCapabilityId(Long capabilityId);

    Mono<Void> deleteByCapabilityId(Long capabilityId);

    Mono<Long> countCapabilitiesByTechnologyId(Long technologyId);
}
