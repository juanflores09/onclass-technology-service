package com.pragma.onclass_technology_service.domain.spi;

import com.pragma.onclass_technology_service.domain.model.Technology;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TechnologyPersistencePort {

    Mono<Technology> save(Technology technology);
    Mono<Boolean> existsByName(String name);
    Mono<Boolean> areAllTechnologiesExist(List<Long> ids);
}
