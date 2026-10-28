package com.pragma.onclass_technology_service.domain.usecase;

import com.pragma.onclass_technology_service.domain.model.Technology;
import com.pragma.onclass_technology_service.domain.spi.TechnologyPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class FindTechnologiesUseCaseTest {

    @Mock
    private TechnologyPersistencePort repositoryPort;

    @InjectMocks
    private FindTechnologiesUseCase findTechnologiesUseCase;

    private List<Long> ids;
    private Technology tech1;
    private Technology tech2;

    @BeforeEach
    void setUp() {
        ids = List.of(1L, 2L);
        tech1 = new Technology(1L, "Spring Boot", "Framework reactivo");
        tech2 = new Technology(2L, "Java", "Lenguaje de programación");
    }

    @Test
    void areAllTechnologiesExist_ReturnsTrue() {
        // Given
        when(repositoryPort.areAllTechnologiesExist(ids)).thenReturn(Mono.just(true));

        // When & Then
        Mono<Boolean> result = findTechnologiesUseCase.areAllTechnologiesExist(ids);

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        verify(repositoryPort, times(1)).areAllTechnologiesExist(ids);
    }

    @Test
    void areAllTechnologiesExist_ReturnsFalse() {
        // Given
        when(repositoryPort.areAllTechnologiesExist(ids)).thenReturn(Mono.just(false));

        // When & Then
        Mono<Boolean> result = findTechnologiesUseCase.areAllTechnologiesExist(ids);

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();

        verify(repositoryPort, times(1)).areAllTechnologiesExist(ids);
    }

    @Test
    void findByIdIn_ReturnsTechnologiesFlux() {
        // Given
        when(repositoryPort.findByIdIn(ids)).thenReturn(Flux.just(tech1, tech2));

        // When & Then
        Flux<Technology> result = findTechnologiesUseCase.findByIdIn(ids);

        StepVerifier.create(result)
                .expectNext(tech1)
                .expectNext(tech2)
                .verifyComplete();

        verify(repositoryPort, times(1)).findByIdIn(ids);
    }
}
