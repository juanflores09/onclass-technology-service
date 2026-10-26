package com.pragma.onclass_technology_service.infraestructure.adapters.persistenceadapters.repository;

import com.pragma.onclass_technology_service.infraestructure.adapters.persistenceadapters.entity.TechnologyEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface SpringDataTechnologyRepository extends ReactiveCrudRepository<TechnologyEntity, Long> {

    Mono<Boolean> existsByName(String name);

    Mono<Long> countByIdIn(List<Long> ids);

}
