package com.pragma.onclass_technology_service.domain.api;

import reactor.core.publisher.Mono;

import java.util.List;

public interface SaveCapabilityTechnologyServicePort {

    Mono<Void> saveAll(Long capabilityId, List<Long> technologyIds);
}
