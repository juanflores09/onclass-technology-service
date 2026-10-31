package com.pragma.onclass_technology_service.infraestructure.adapters.persistenceadapters.repository;

import com.pragma.onclass_technology_service.infraestructure.adapters.persistenceadapters.entity.TechnologyEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface SpringDataTechnologyRepository extends ReactiveCrudRepository<TechnologyEntity, Long> {

    Mono<Boolean> existsByName(String name);

    Mono<Long> countByIdIn(List<Long> ids);

    Flux<TechnologyEntity> findByIdIn(List<Long> ids);

    @Query("""
        SELECT ct.capability_id,
                t.id,
                t.name,
                t.description FROM technology t
        JOIN capability_technology ct ON t.id = ct.technology_id
        WHERE ct.capability_id IN (:capabilityIdS)
    """)
    Flux<CapabilityTechnologyProjection> findTechnologiesByCapabilityId(List<Long> capabilityIds);

}
