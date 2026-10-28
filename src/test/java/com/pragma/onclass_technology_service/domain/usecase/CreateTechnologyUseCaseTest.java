package com.pragma.onclass_technology_service.domain.usecase;

import com.pragma.onclass_technology_service.domain.emuns.TechnicalMessage;
import com.pragma.onclass_technology_service.domain.exceptions.BusinessException;
import com.pragma.onclass_technology_service.domain.model.Technology;
import com.pragma.onclass_technology_service.domain.spi.TechnologyPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateTechnologyUseCaseTest {

    @Mock
    private TechnologyPersistencePort repositoryPort;

    @InjectMocks
    private CreateTechnologyUseCase createTechnologyUseCase;

    private Technology technology;

    @BeforeEach
    void setUp() {
        technology = new Technology(1L, "Spring Boot", "Framework reactivo para Java");
    }

    @Test
    void createTechnology_Success() {
        // Given
        when(repositoryPort.existsByName(technology.getName())).thenReturn(Mono.just(false));
        when(repositoryPort.save(any(Technology.class))).thenReturn(Mono.just(technology));

        // When & Then
        Mono<Technology> result = createTechnologyUseCase.createTechnology(technology);

        StepVerifier.create(result)
                .expectNext(technology)
                .verifyComplete();

        verify(repositoryPort, times(1)).existsByName(technology.getName());
        verify(repositoryPort, times(1)).save(technology);
    }

    @Test
    void createTechnology_AlreadyExists_ThrowsBusinessException() {
        // Given
        when(repositoryPort.existsByName(technology.getName())).thenReturn(Mono.just(true));

        // When & Then
        Mono<Technology> result = createTechnologyUseCase.createTechnology(technology);

        StepVerifier.create(result)
                .expectErrorSatisfies(error -> {
                    org.assertj.core.api.Assertions.assertThat(error)
                            .isInstanceOf(BusinessException.class)
                            .hasMessage(TechnicalMessage.TECHNOLOGY_ALREADY_EXISTS.getMessage());
                })
                .verify();

        verify(repositoryPort, times(1)).existsByName(technology.getName());
        verify(repositoryPort, never()).save(any(Technology.class));
    }
}
