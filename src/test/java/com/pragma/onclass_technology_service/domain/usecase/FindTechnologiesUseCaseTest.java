package com.pragma.onclass_technology_service.domain.usecase;

import com.pragma.onclass_technology_service.domain.model.CapabilityTechnologies;
import com.pragma.onclass_technology_service.domain.model.Technology;
import com.pragma.onclass_technology_service.domain.spi.TechnologyPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class FindTechnologiesUseCaseTest {

    @Mock
    private TechnologyPersistencePort repositoryPort;

    @InjectMocks
    private FindTechnologiesUseCase findTechnologiesUseCase;

    @BeforeEach
    void setUp() {
        repositoryPort = mock(TechnologyPersistencePort.class);
        findTechnologiesUseCase = new FindTechnologiesUseCase(repositoryPort);
    }

    @Test
    void findTechnologiesByCapabilityId_ReturnsCapabilityTechnologiesFlux() {
        // Given
        List<Long> ids = List.of(1L, 2L);
        CapabilityTechnologies capabilityTechnologies = new CapabilityTechnologies(
                1L, List.of(new Technology(24L, "Java", "Lenguaje de programación"))
        );

        when(repositoryPort.findTechnologiesByCapabilityIds(ids))
                .thenReturn(Flux.just(capabilityTechnologies));

        // When
        Flux<CapabilityTechnologies> result = findTechnologiesUseCase.findTechnologiesByCapabilityId(ids);

        // Then
        StepVerifier.create(result)
                .expectNext(capabilityTechnologies)
                .verifyComplete();

        verify(repositoryPort, times(1)).findTechnologiesByCapabilityIds(ids);
    }

    @Test
    void findTechnologiesByCapabilityId_WhenRepositoryReturnsEmpty_CompletesEmpty() {
        // Given
        List<Long> ids = List.of(99L);

        when(repositoryPort.findTechnologiesByCapabilityIds(ids))
                .thenReturn(Flux.empty());

        // When
        Flux<CapabilityTechnologies> result = findTechnologiesUseCase.findTechnologiesByCapabilityId(ids);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(repositoryPort, times(1)).findTechnologiesByCapabilityIds(ids);
    }
}
