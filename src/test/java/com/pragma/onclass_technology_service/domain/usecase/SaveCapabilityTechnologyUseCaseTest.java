package com.pragma.onclass_technology_service.domain.usecase;

import com.pragma.onclass_technology_service.domain.emuns.TechnicalMessage;
import com.pragma.onclass_technology_service.domain.exceptions.BusinessException;
import com.pragma.onclass_technology_service.domain.spi.CapabilityTechnologyPersistencePort;
import com.pragma.onclass_technology_service.domain.spi.TechnologyPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaveCapabilityTechnologyUseCaseTest {

    @Mock
    private TechnologyPersistencePort technologyPersistencePort;

    @Mock
    private CapabilityTechnologyPersistencePort capabilityTechnologyPersistencePort;

    @InjectMocks
    private SaveCapabilityTechnologyUseCase useCase;

    @BeforeEach
    void setUp() {
        technologyPersistencePort = mock(TechnologyPersistencePort.class);
        capabilityTechnologyPersistencePort = mock(CapabilityTechnologyPersistencePort.class);
        useCase = new SaveCapabilityTechnologyUseCase(technologyPersistencePort, capabilityTechnologyPersistencePort);
    }

    @Test
    void saveAll_WhenTechnologyIdsIsNullOrEmpty_ReturnsEmpty() {
        // Given
        Long capabilityId = 1L;
        List<Long> emptyIds = Collections.emptyList();

        // When
        Mono<Void> result = useCase.saveAll(capabilityId, emptyIds);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        // Verify that no repository calls were made
        verifyNoInteractions(technologyPersistencePort);
        verifyNoInteractions(capabilityTechnologyPersistencePort);
    }

    @Test
    void saveAll_WhenTechnologyIdsIsNull_ReturnsEmpty() {
        // Given
        Long capabilityId = 1L;

        // When
        Mono<Void> result = useCase.saveAll(capabilityId, null);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        // Verify that no repository calls were made
        verifyNoInteractions(technologyPersistencePort);
        verifyNoInteractions(capabilityTechnologyPersistencePort);
    }

    @Test
    void saveAll_WhenTechnologiesExist_SavesSuccessfully() {
        // Given
        Long capabilityId = 1L;
        List<Long> technologyIds = List.of(10L, 20L);

        when(technologyPersistencePort.areAllTechnologiesExist(technologyIds))
                .thenReturn(Mono.just(true));
        when(capabilityTechnologyPersistencePort.saveAll(capabilityId, technologyIds))
                .thenReturn(Mono.empty());

        // When
        Mono<Void> result = useCase.saveAll(capabilityId, technologyIds);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(technologyPersistencePort, times(1)).areAllTechnologiesExist(technologyIds);
        verify(capabilityTechnologyPersistencePort, times(1)).saveAll(capabilityId, technologyIds);
    }

    @Test
    void saveAll_WhenTechnologiesDoNotExist_ThrowsBusinessException() {
        // Given
        Long capabilityId = 1L;
        List<Long> technologyIds = List.of(10L, 99L);

        // Simulating that technologies are not found (filter drops the element, triggering switchIfEmpty)
        when(technologyPersistencePort.areAllTechnologiesExist(technologyIds))
                .thenReturn(Mono.just(false));

        // When
        Mono<Void> result = useCase.saveAll(capabilityId, technologyIds);

        // Then
        StepVerifier.create(result)
                .expectErrorSatisfies(throwable -> {
                    org.assertj.core.api.Assertions.assertThat(throwable)
                            .isInstanceOf(BusinessException.class);
                    BusinessException ex = (BusinessException) throwable;
                    org.assertj.core.api.Assertions.assertThat(ex.getTechnicalMessage())
                            .isEqualTo(TechnicalMessage.TECHNOLOGY_NOT_FOUND);
                })
                .verify();

        verify(technologyPersistencePort, times(1)).areAllTechnologiesExist(technologyIds);
        verifyNoInteractions(capabilityTechnologyPersistencePort);
    }
}
