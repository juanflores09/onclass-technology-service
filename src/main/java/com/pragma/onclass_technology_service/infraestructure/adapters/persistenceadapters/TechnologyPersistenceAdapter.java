package com.pragma.onclass_technology_service.infraestructure.adapters.persistenceadapters;

import com.pragma.onclass_technology_service.domain.model.Technology;
import com.pragma.onclass_technology_service.domain.spi.TechnologyPersistencePort;
import com.pragma.onclass_technology_service.infraestructure.adapters.persistenceadapters.entity.TechnologyEntity;
import com.pragma.onclass_technology_service.infraestructure.adapters.persistenceadapters.repository.SpringDataTechnologyRepository;
import com.pragma.onclass_technology_service.infraestructure.entrypoints.mapper.TechnologyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class TechnologyPersistenceAdapter implements TechnologyPersistencePort {

    private final SpringDataTechnologyRepository repository;
    private final TechnologyMapper technologyMapper;


    @Override
    public Mono<Technology> save(Technology technology) {
        TechnologyEntity entity = new TechnologyEntity();
        entity.setName(technology.getName());
        entity.setDescription(technology.getDescription());

        return repository.save(entity)
                .map(saved -> new Technology(
                        saved.getId(), saved.getName(), saved.getDescription()));
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return repository.existsByName(name);
    }

    @Override
    public Mono<Boolean> areAllTechnologiesExist(List<Long> ids) {
        return repository.countByIdIn(ids)
                .map(count -> count == ids.size());
    }

    @Override
    public Flux<Technology> findByIdIn(List<Long> ids) {
        return repository.findByIdIn(ids)
                .map(technologyMapper::entityToDomain);
    }
}
