package com.pragma.onclass_technology_service.domain.api;

import reactor.core.publisher.Mono;

public interface DeleteTechnologiesServicePort {

    Mono<Void> deleteTechnologiesByCapability(Long capabilityId, String messageId);

}
