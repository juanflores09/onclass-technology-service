package com.pragma.onclass_technology_service.infraestructure.adapters.persistenceadapters.repository;

import com.pragma.onclass_technology_service.infraestructure.adapters.persistenceadapters.entity.CapabilityTechnologyEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface SpringDataCapabilityTechnologyRepository extends ReactiveCrudRepository<CapabilityTechnologyEntity, Long> {

    Flux<CapabilityTechnologyEntity> findByCapabilityId(Long capability);

    Mono<Void> deleteByCapabilityId(Long capabilityId);

    @Query("SELECT COUNT(*) FROM capability_technology WHERE technology_id = :technologyId")
    Mono<Long> countBootcampsByCapabilityId(Long technologyId);
}
