package com.pragma.onclass_technology_service.domain.usecase;

import com.pragma.onclass_technology_service.domain.spi.CapabilityTechnologyPersistencePort;
import com.pragma.onclass_technology_service.domain.spi.TechnologyPersistencePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteTechnologiesByCapabilityUseCaseTest {

    @Mock
    private CapabilityTechnologyPersistencePort capabilityTechnologyPersistencePort;

    @Mock
    private TechnologyPersistencePort technologyPersistencePort;

    @InjectMocks
    private DeleteTechnologiesByCapabilityUseCase useCase;

    private final Long capabilityId = 1L;
    private final String messageId = "msg-test-123";

    @Test
    @DisplayName("Debe eliminar la relación y la tecnología si no está asociada a otra capacidad (conteo = 0)")
    void shouldDeleteTechnologiesWhenNotUsedByOtherCapabilities() {
        // Arrange
        Long technologyId = 100L;

        when(capabilityTechnologyPersistencePort.findTechnologyIdsByCapabilityId(capabilityId))
                .thenReturn(Flux.just(technologyId));

        when(capabilityTechnologyPersistencePort.deleteByCapabilityId(capabilityId))
                .thenReturn(Mono.empty());

        when(capabilityTechnologyPersistencePort.countCapabilitiesByTechnologyId(technologyId))
                .thenReturn(Mono.just(0L));

        when(technologyPersistencePort.deleteById(technologyId))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.deleteTechnologiesByCapability(capabilityId, messageId))
                .verifyComplete();

        // Verify
        verify(capabilityTechnologyPersistencePort).findTechnologyIdsByCapabilityId(capabilityId);
        verify(capabilityTechnologyPersistencePort).deleteByCapabilityId(capabilityId);
        verify(capabilityTechnologyPersistencePort).countCapabilitiesByTechnologyId(technologyId);
        verify(technologyPersistencePort).deleteById(technologyId);
    }

    @Test
    @DisplayName("Debe eliminar la relación pero conservar la tecnología si otra capacidad la sigue usando (conteo > 0)")
    void shouldKeepTechnologyWhenUsedByOtherCapabilities() {
        // Arrange
        Long technologyId = 100L;

        when(capabilityTechnologyPersistencePort.findTechnologyIdsByCapabilityId(capabilityId))
                .thenReturn(Flux.just(technologyId));

        when(capabilityTechnologyPersistencePort.deleteByCapabilityId(capabilityId))
                .thenReturn(Mono.empty());

        when(capabilityTechnologyPersistencePort.countCapabilitiesByTechnologyId(technologyId))
                .thenReturn(Mono.just(2L));

        // Act & Assert
        StepVerifier.create(useCase.deleteTechnologiesByCapability(capabilityId, messageId))
                .verifyComplete();

        // Verify
        verify(capabilityTechnologyPersistencePort).findTechnologyIdsByCapabilityId(capabilityId);
        verify(capabilityTechnologyPersistencePort).deleteByCapabilityId(capabilityId);
        verify(capabilityTechnologyPersistencePort).countCapabilitiesByTechnologyId(technologyId);
        // Aseguramos que NUNCA se llame al borrado de la tecnología
        verify(technologyPersistencePort, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Debe finalizar sin hacer nada si la capacidad no tiene tecnologías asociadas")
    void shouldDoNothingWhenNoTechnologiesAssociated() {
        // Arrange
        when(capabilityTechnologyPersistencePort.findTechnologyIdsByCapabilityId(capabilityId))
                .thenReturn(Flux.empty());

        // Act & Assert
        StepVerifier.create(useCase.deleteTechnologiesByCapability(capabilityId, messageId))
                .verifyComplete();

        // Verify
        verify(capabilityTechnologyPersistencePort).findTechnologyIdsByCapabilityId(capabilityId);
        verify(capabilityTechnologyPersistencePort, never()).deleteByCapabilityId(anyLong());
        verify(technologyPersistencePort, never()).deleteById(anyLong());
    }
}
