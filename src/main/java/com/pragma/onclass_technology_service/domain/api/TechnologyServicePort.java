package com.pragma.onclass_technology_service.domain.api;

import com.pragma.onclass_technology_service.domain.model.Technology;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TechnologyServicePort {

    Mono<Technology> createTechnology(Technology technology);
    Mono<Boolean> areAllTechnologiesExist(List<Long> ids);
}
