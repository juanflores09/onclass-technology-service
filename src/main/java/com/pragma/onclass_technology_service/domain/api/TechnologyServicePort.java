package com.pragma.onclass_technology_service.domain.api;

import com.pragma.onclass_technology_service.domain.model.Technology;
import reactor.core.publisher.Mono;

public interface TechnologyServicePort {

    Mono<Technology> createTechnology(Technology technology);
}
