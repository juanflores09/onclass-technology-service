package com.pragma.onclass_technology_service.domain.usecase;

import com.pragma.onclass_technology_service.domain.api.SaveCapabilityTechnologyServicePort;
import com.pragma.onclass_technology_service.domain.emuns.TechnicalMessage;
import com.pragma.onclass_technology_service.domain.exceptions.BusinessException;
import com.pragma.onclass_technology_service.domain.spi.CapabilityTechnologyPersistencePort;
import com.pragma.onclass_technology_service.domain.spi.TechnologyPersistencePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class SaveCapabilityTechnologyUseCase implements SaveCapabilityTechnologyServicePort {

    private final TechnologyPersistencePort technologyPersistencePort;
    private final CapabilityTechnologyPersistencePort capabilityTechnologyPersistencePort;

    @Override
    public Mono<Void> saveAll(Long capabilityId, List<Long> technologyIds) {
        if (technologyIds == null || technologyIds.isEmpty()) {
            return Mono.empty();
        }

        return technologyPersistencePort.areAllTechnologiesExist(technologyIds)
                .filter(exists -> exists)
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.TECHNOLOGY_NOT_FOUND)))
                .flatMap(count ->
                        capabilityTechnologyPersistencePort.saveAll(capabilityId, technologyIds)
                );
    }

}
