package com.pragma.onclass_technology_service.domain.usecase;

import com.pragma.onclass_technology_service.domain.api.DeleteTechnologiesServicePort;
import com.pragma.onclass_technology_service.domain.spi.CapabilityTechnologyPersistencePort;
import com.pragma.onclass_technology_service.domain.spi.TechnologyPersistencePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class DeleteTechnologiesByCapabilityUseCase implements DeleteTechnologiesServicePort {

    private final CapabilityTechnologyPersistencePort capabilityTechnologyPersistencePort;
    private final TechnologyPersistencePort technologyPersistencePort;

    @Override
    public Mono<Void> deleteTechnologiesByCapability(Long capabilityId, String messageId) {

        // Retrieve the technologies associated with this capability from our database
        return capabilityTechnologyPersistencePort.findTechnologyIdsByCapabilityId(capabilityId)
                .collectList()
                .flatMap(technologyIds -> {
                    if (technologyIds.isEmpty()) {
                        return Mono.empty();
                    }

                    // Remove the association between the capability and its technologies in the intermediate table
                    return capabilityTechnologyPersistencePort.deleteByCapabilityId(capabilityId)
                            .then(
                                    Flux.fromIterable(technologyIds)
                                            .flatMap(technologyId ->
                                                    // Check how many capabilities currently use this technology
                                                    capabilityTechnologyPersistencePort.countCapabilitiesByTechnologyId(technologyId)
                                                            .filter(count -> count == 0) // If the count is 0 (no other capability uses it)
                                                            .flatMap(count ->
                                                                    // Remove technology
                                                                    technologyPersistencePort.deleteById(technologyId)
                                                            )
                                            )
                                            .then()
                            );
                });
    }
}
