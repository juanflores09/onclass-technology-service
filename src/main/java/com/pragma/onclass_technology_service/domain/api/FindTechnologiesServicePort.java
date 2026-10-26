package com.pragma.onclass_technology_service.domain.api;

import com.pragma.onclass_technology_service.domain.model.Technology;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface FindTechnologiesServicePort {
    Mono<Boolean> areAllTechnologiesExist(List<Long> ids);
    Flux<Technology> findByIdIn(List<Long> ids);
}
