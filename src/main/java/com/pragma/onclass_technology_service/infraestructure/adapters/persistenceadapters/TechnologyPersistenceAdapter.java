package com.pragma.onclass_technology_service.infraestructure.adapters.persistenceadapters;

import com.pragma.onclass_technology_service.domain.model.Technology;
import com.pragma.onclass_technology_service.domain.spi.TechnologyPersistencePort;
import com.pragma.onclass_technology_service.infraestructure.adapters.persistenceadapters.entity.TechnologyEntity;
import com.pragma.onclass_technology_service.infraestructure.adapters.persistenceadapters.repository.SpringDataTechnologyRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public class TechnologyPersistenceAdapter implements TechnologyPersistencePort {

    private final SpringDataTechnologyRepository repository;

    public TechnologyPersistenceAdapter(SpringDataTechnologyRepository repository) {
        this.repository = repository;
    }

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
}
