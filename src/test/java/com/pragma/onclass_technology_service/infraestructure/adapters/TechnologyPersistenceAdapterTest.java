package com.pragma.onclass_technology_service.infraestructure.adapters;

import com.pragma.onclass_technology_service.domain.model.Technology;
import com.pragma.onclass_technology_service.infraestructure.adapters.persistenceadapters.TechnologyPersistenceAdapter;
import com.pragma.onclass_technology_service.infraestructure.adapters.persistenceadapters.entity.TechnologyEntity;
import com.pragma.onclass_technology_service.infraestructure.adapters.persistenceadapters.repository.SpringDataTechnologyRepository;
import com.pragma.onclass_technology_service.infraestructure.entrypoints.mapper.TechnologyMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TechnologyPersistenceAdapterTest {

    @Mock
    private SpringDataTechnologyRepository repository;

    @Mock
    private TechnologyMapper technologyMapper;

    @InjectMocks
    private TechnologyPersistenceAdapter persistenceAdapter;

    private Technology technology;
    private TechnologyEntity technologyEntity;

    @BeforeEach
    void setUp() {
        technology = new Technology(1L, "Spring Boot", "Framework reactivo");
        technologyEntity = new TechnologyEntity();
        technologyEntity.setId(1L);
        technologyEntity.setName("Spring Boot");
        technologyEntity.setDescription("Framework reactivo");
    }

    @Test
    void save_Success() {
        // Given
        when(repository.save(any(TechnologyEntity.class))).thenReturn(Mono.just(technologyEntity));

        // When
        Mono<Technology> result = persistenceAdapter.save(technology);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(saved -> saved.getId().equals(1L)
                        && saved.getName().equals("Spring Boot")
                        && saved.getDescription().equals("Framework reactivo"))
                .verifyComplete();

        verify(repository, times(1)).save(any(TechnologyEntity.class));
    }

    @Test
    void existsByName_ReturnsTrue() {
        // Given
        when(repository.existsByName("Spring Boot")).thenReturn(Mono.just(true));

        // When
        Mono<Boolean> result = persistenceAdapter.existsByName("Spring Boot");

        // Then
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        verify(repository, times(1)).existsByName("Spring Boot");
    }

    @Test
    void areAllTechnologiesExist_MatchingCount_ReturnsTrue() {
        // Given
        List<Long> ids = List.of(1L, 2L);
        when(repository.countByIdIn(ids)).thenReturn(Mono.just(2L));

        // When
        Mono<Boolean> result = persistenceAdapter.areAllTechnologiesExist(ids);

        // Then
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        verify(repository, times(1)).countByIdIn(ids);
    }

    @Test
    void areAllTechnologiesExist_MismatchedCount_ReturnsFalse() {
        // Given
        List<Long> ids = List.of(1L, 2L, 3L);
        when(repository.countByIdIn(ids)).thenReturn(Mono.just(2L));

        // When
        Mono<Boolean> result = persistenceAdapter.areAllTechnologiesExist(ids);

        // Then
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();

        verify(repository, times(1)).countByIdIn(ids);
    }

    @Test
    void findByIdIn_ReturnsMappedTechnologies() {
        // Given
        List<Long> ids = List.of(1L);
        when(repository.findByIdIn(ids)).thenReturn(Flux.just(technologyEntity));
        when(technologyMapper.entityToDomain(technologyEntity)).thenReturn(technology);

        // When
        Flux<Technology> result = persistenceAdapter.findByIdIn(ids);

        // Then
        StepVerifier.create(result)
                .expectNext(technology)
                .verifyComplete();

        verify(repository, times(1)).findByIdIn(ids);
        verify(technologyMapper, times(1)).entityToDomain(technologyEntity);
    }
}
