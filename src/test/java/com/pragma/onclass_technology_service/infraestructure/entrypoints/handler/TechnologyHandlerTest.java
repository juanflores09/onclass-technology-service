package com.pragma.onclass_technology_service.infraestructure.entrypoints.handler;

import com.pragma.onclass_technology_service.domain.api.CreateTechnologyServicePort;
import com.pragma.onclass_technology_service.domain.api.DeleteTechnologiesServicePort;
import com.pragma.onclass_technology_service.domain.api.FindTechnologiesServicePort;
import com.pragma.onclass_technology_service.domain.api.SaveCapabilityTechnologyServicePort;
import com.pragma.onclass_technology_service.domain.emuns.TechnicalMessage;
import com.pragma.onclass_technology_service.domain.exceptions.BusinessException;
import com.pragma.onclass_technology_service.domain.model.CapabilityTechnologies;
import com.pragma.onclass_technology_service.domain.model.Technology;
import com.pragma.onclass_technology_service.infraestructure.entrypoints.dto.CapabilityIdsRequestDTO;
import com.pragma.onclass_technology_service.infraestructure.entrypoints.dto.CapabilityTechnologiesRequestDTO;
import com.pragma.onclass_technology_service.infraestructure.entrypoints.dto.CapabilityTechnologiesResponseDTO;
import com.pragma.onclass_technology_service.infraestructure.entrypoints.dto.TechnologyDTO;
import com.pragma.onclass_technology_service.infraestructure.entrypoints.mapper.TechnologyMapper;
import com.pragma.onclass_technology_service.infraestructure.entrypoints.util.Constants;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TechnologyHandlerTest {

    @Mock
    private CreateTechnologyServicePort createTechnologyServicePort;

    @Mock
    private SaveCapabilityTechnologyServicePort saveCapabilityTechnologyServicePort;

    @Mock
    private FindTechnologiesServicePort findTechnologiesServicePort;

    @Mock
    private DeleteTechnologiesServicePort deleteTechnologiesServicePort;

    @Mock
    private TechnologyMapper technologyMapper;

    @Mock
    private Validator validator;

    @Mock
    private ServerRequest serverRequest;

    @Mock
    private ServerRequest.Headers headers;

    @InjectMocks
    private TechnologyHandler technologyHandler;

    @BeforeEach
    void setUp() {
        lenient().when(serverRequest.headers()).thenReturn(headers);
        lenient().when(headers.firstHeader(Constants.X_MESSAGE_ID)).thenReturn("msg-12345");
    }

    @Test
    void createTechnology_Success() {
        TechnologyDTO dto = new TechnologyDTO(1L,"Spring Boot", "Framework reactivo");
        Technology technology = new Technology(1L, "Spring Boot", "Framework reactivo");

        when(serverRequest.bodyToMono(TechnologyDTO.class)).thenReturn(Mono.just(dto));
        when(validator.validate(dto)).thenReturn(Collections.emptySet());
        when(technologyMapper.technologyDtoToTechnology(dto)).thenReturn(technology);
        when(createTechnologyServicePort.createTechnology(technology)).thenReturn(Mono.just(technology));

        Mono<ServerResponse> responseMono = technologyHandler.createTechnology(serverRequest);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.CREATED))
                .verifyComplete();

        verify(createTechnologyServicePort, times(1)).createTechnology(technology);
    }

    @Test
    void createTechnology_ValidationFailed_ReturnsBadRequest() {
        TechnologyDTO dto = new TechnologyDTO(1L, "", "");
        ConstraintViolation<TechnologyDTO> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("El nombre no puede estar vacío");

        when(serverRequest.bodyToMono(TechnologyDTO.class)).thenReturn(Mono.just(dto));
        when(validator.validate(dto)).thenReturn(Set.of(violation));

        Mono<ServerResponse> responseMono = technologyHandler.createTechnology(serverRequest);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.BAD_REQUEST))
                .verifyComplete();

        verify(createTechnologyServicePort, never()).createTechnology(any());
    }

    @Test
    void findTechnologiesByCapabilityId_Success() {
        // Given
        List<Long> ids = List.of(1L);
        CapabilityIdsRequestDTO requestDTO = new CapabilityIdsRequestDTO(ids);

        CapabilityTechnologies capabilityTechnologies = new CapabilityTechnologies(
                1L, List.of(new Technology(24L, "Java", "Lenguaje de programación"))
        );

        when(serverRequest.bodyToMono(CapabilityIdsRequestDTO.class)).thenReturn(Mono.just(requestDTO));
        when(serverRequest.headers()).thenReturn(headers);
        when(headers.firstHeader(Constants.X_MESSAGE_ID)).thenReturn("test-message-id");

        when(findTechnologiesServicePort.findTechnologiesByCapabilityId(ids)).thenReturn(Flux.just(capabilityTechnologies));

        CapabilityTechnologiesResponseDTO responseDTO = new CapabilityTechnologiesResponseDTO(1L, List.of());
        when(technologyMapper.toResponseDTO(List.of(capabilityTechnologies))).thenReturn(List.of(responseDTO));

        // When
        Mono<ServerResponse> responseMono = technologyHandler.findTechnologiesByCapabilityId(serverRequest);

        // Then
        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();

        verify(findTechnologiesServicePort, times(1)).findTechnologiesByCapabilityId(ids);
    }

    @Test
    void createTechnology_BusinessException_ReturnsBadRequest() {
        TechnologyDTO dto = new TechnologyDTO(1L, "Spring Boot", "Framework");
        Technology technology = new Technology(1L, "Spring Boot", "Framework");

        when(serverRequest.bodyToMono(TechnologyDTO.class)).thenReturn(Mono.just(dto));
        when(validator.validate(any(TechnologyDTO.class))).thenReturn(Set.of());
        when(technologyMapper.technologyDtoToTechnology(dto)).thenReturn(technology);
        when(createTechnologyServicePort.createTechnology(technology))
                .thenReturn(Mono.error(new BusinessException(TechnicalMessage.TECHNOLOGY_ALREADY_EXISTS)));

        Mono<ServerResponse> responseMono = technologyHandler.createTechnology(serverRequest);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.BAD_REQUEST))
                .verifyComplete();
    }

    @Test
    void createTechnology_UnexpectedException_ReturnsInternalServerError() {
        TechnologyDTO dto = new TechnologyDTO(1L, "Spring Boot", "Framework");
        Technology technology = new Technology(1L, "Spring Boot", "Framework");

        when(serverRequest.bodyToMono(TechnologyDTO.class)).thenReturn(Mono.just(dto));
        when(validator.validate(any(TechnologyDTO.class))).thenReturn(Set.of());
        when(technologyMapper.technologyDtoToTechnology(dto)).thenReturn(technology);
        when(createTechnologyServicePort.createTechnology(technology))
                .thenReturn(Mono.error(new RuntimeException("Unexpected DB error")));

        Mono<ServerResponse> responseMono = technologyHandler.createTechnology(serverRequest);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR))
                .verifyComplete();
    }

    @Test
    void findTechnologiesByCapabilityId_UnexpectedException_ReturnsInternalServerError() {
        // Given
        List<Long> ids = List.of(1L);
        CapabilityIdsRequestDTO requestDTO = new CapabilityIdsRequestDTO(ids);

        when(serverRequest.bodyToMono(CapabilityIdsRequestDTO.class)).thenReturn(Mono.just(requestDTO));
        when(serverRequest.headers()).thenReturn(headers);
        when(headers.firstHeader(Constants.X_MESSAGE_ID)).thenReturn("test-message-id");

        when(findTechnologiesServicePort.findTechnologiesByCapabilityId(ids))
                .thenReturn(Flux.error(new RuntimeException("Database timeout")));

        // When
        Mono<ServerResponse> responseMono = technologyHandler.findTechnologiesByCapabilityId(serverRequest);

        // Then
        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR))
                .verifyComplete();
    }

    @Test
    void saveCapabilityTechnologies_Success() {
        // Given
        Long capabilityId = 1L;
        List<Long> technologyIds = List.of(10L, 20L);
        CapabilityTechnologiesRequestDTO requestDTO = new CapabilityTechnologiesRequestDTO(capabilityId, technologyIds);

        // Mock del request y headers
        when(serverRequest.bodyToMono(CapabilityTechnologiesRequestDTO.class)).thenReturn(Mono.just(requestDTO));
        when(serverRequest.headers()).thenReturn(headers);
        when(headers.firstHeader(Constants.X_MESSAGE_ID)).thenReturn("test-message-id");

        when(validator.validate(requestDTO)).thenReturn(Collections.emptySet());

        when(saveCapabilityTechnologyServicePort.saveAll(capabilityId, technologyIds)).thenReturn(Mono.empty());

        // When
        Mono<ServerResponse> responseMono = technologyHandler.saveCapabilityTechnologies(serverRequest);

        // Then
        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.CREATED))
                .verifyComplete();

        verify(saveCapabilityTechnologyServicePort, times(1)).saveAll(capabilityId, technologyIds);
    }

    @Test
    void saveCapabilityTechnologies_ValidationException_ReturnsBadRequest() {
        // Given
        Long capabilityId = 1L;
        List<Long> technologyIds = Collections.emptyList();
        CapabilityTechnologiesRequestDTO requestDTO = new CapabilityTechnologiesRequestDTO(capabilityId, technologyIds);

        ConstraintViolation<CapabilityTechnologiesRequestDTO> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("The technology list cannot be empty");

        when(serverRequest.bodyToMono(CapabilityTechnologiesRequestDTO.class)).thenReturn(Mono.just(requestDTO));
        when(serverRequest.headers()).thenReturn(headers);
        when(headers.firstHeader(Constants.X_MESSAGE_ID)).thenReturn("test-message-id");

        when(validator.validate(requestDTO)).thenReturn(Set.of(violation));

        // When
        Mono<ServerResponse> responseMono = technologyHandler.saveCapabilityTechnologies(serverRequest);

        // Then
        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.BAD_REQUEST))
                .verifyComplete();

        verifyNoInteractions(saveCapabilityTechnologyServicePort);
    }

    @Test
    void saveCapabilityTechnologies_UnexpectedException_ReturnsInternalServerError() {
        // Given
        Long capabilityId = 1L;
        List<Long> technologyIds = List.of(10L, 20L);
        CapabilityTechnologiesRequestDTO requestDTO = new CapabilityTechnologiesRequestDTO(capabilityId, technologyIds);

        when(serverRequest.bodyToMono(CapabilityTechnologiesRequestDTO.class)).thenReturn(Mono.just(requestDTO));
        when(serverRequest.headers()).thenReturn(headers);
        when(headers.firstHeader(Constants.X_MESSAGE_ID)).thenReturn("test-message-id");

        when(validator.validate(requestDTO)).thenReturn(Collections.emptySet());

        when(saveCapabilityTechnologyServicePort.saveAll(capabilityId, technologyIds))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        // When
        Mono<ServerResponse> responseMono = technologyHandler.saveCapabilityTechnologies(serverRequest);

        // Then
        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR))
                .verifyComplete();

        verify(saveCapabilityTechnologyServicePort, times(1)).saveAll(capabilityId, technologyIds);
    }

    @Test
    void deleteTechnologiesByCapability_Success() {
        // Given
        Long capabilityId = 1L;
        String messageId = "msg-12345";

        when(serverRequest.pathVariable("id")).thenReturn("1");
        when(deleteTechnologiesServicePort.deleteTechnologiesByCapability(capabilityId, messageId))
                .thenReturn(Mono.empty());

        // When
        Mono<ServerResponse> responseMono = technologyHandler.deleteTechnologiesByCapability(serverRequest);

        // Then
        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.NO_CONTENT))
                .verifyComplete();

        verify(deleteTechnologiesServicePort, times(1)).deleteTechnologiesByCapability(capabilityId, messageId);
    }

    @Test
    void deleteTechnologiesByCapability_InvalidIdFormat_ReturnsBadRequest() {
        // Given
        when(serverRequest.pathVariable("id")).thenReturn("invalid-id");

        // When
        Mono<ServerResponse> responseMono = technologyHandler.deleteTechnologiesByCapability(serverRequest);

        // Then
        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.BAD_REQUEST))
                .verifyComplete();

        verifyNoInteractions(deleteTechnologiesServicePort);
    }

    @Test
    void deleteTechnologiesByCapability_BusinessException_ReturnsBadRequest() {
        // Given
        Long capabilityId = 1L;
        String messageId = "msg-12345";

        when(serverRequest.pathVariable("id")).thenReturn("1");
        when(deleteTechnologiesServicePort.deleteTechnologiesByCapability(capabilityId, messageId))
                .thenReturn(Mono.error(new BusinessException(TechnicalMessage.INVALID_PARAMETERS)));

        // When
        Mono<ServerResponse> responseMono = technologyHandler.deleteTechnologiesByCapability(serverRequest);

        // Then
        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.BAD_REQUEST))
                .verifyComplete();

        verify(deleteTechnologiesServicePort, times(1)).deleteTechnologiesByCapability(capabilityId, messageId);
    }

    @Test
    void deleteTechnologiesByCapability_UnexpectedException_ReturnsInternalServerError() {
        // Given
        Long capabilityId = 1L;
        String messageId = "msg-12345";

        when(serverRequest.pathVariable("id")).thenReturn("1");
        when(deleteTechnologiesServicePort.deleteTechnologiesByCapability(capabilityId, messageId))
                .thenReturn(Mono.error(new RuntimeException("Database connectivity failure")));

        // When
        Mono<ServerResponse> responseMono = technologyHandler.deleteTechnologiesByCapability(serverRequest);

        // Then
        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR))
                .verifyComplete();

        verify(deleteTechnologiesServicePort, times(1)).deleteTechnologiesByCapability(capabilityId, messageId);
    }
}
