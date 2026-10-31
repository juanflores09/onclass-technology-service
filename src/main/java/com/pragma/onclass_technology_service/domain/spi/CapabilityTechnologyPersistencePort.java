package com.pragma.onclass_technology_service.domain.spi;

import reactor.core.publisher.Mono;

import java.util.List;

public interface CapabilityTechnologyPersistencePort {

    Mono<Void> saveAll(Long capabilityId, List<Long> technologyIds);
}
