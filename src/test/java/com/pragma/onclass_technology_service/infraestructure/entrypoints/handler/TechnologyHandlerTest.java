package com.pragma.onclass_technology_service.infraestructure.entrypoints.handler;

import com.pragma.onclass_technology_service.domain.api.CreateTechnologyServicePort;
import com.pragma.onclass_technology_service.domain.api.FindTechnologiesServicePort;
import com.pragma.onclass_technology_service.domain.emuns.TechnicalMessage;
import com.pragma.onclass_technology_service.domain.exceptions.BusinessException;
import com.pragma.onclass_technology_service.domain.model.Technology;
import com.pragma.onclass_technology_service.infraestructure.entrypoints.dto.TechnologyDTO;
import com.pragma.onclass_technology_service.infraestructure.entrypoints.dto.TechnologyIdsRequestDTO;
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
    private FindTechnologiesServicePort findTechnologiesServicePort;

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
    void validateTechnologies_Success() {
        TechnologyIdsRequestDTO dto = new TechnologyIdsRequestDTO(List.of(1L, 2L));

        when(serverRequest.bodyToMono(TechnologyIdsRequestDTO.class)).thenReturn(Mono.just(dto));
        when(validator.validate(dto)).thenReturn(Collections.emptySet());
        when(findTechnologiesServicePort.areAllTechnologiesExist(dto.ids())).thenReturn(Mono.just(true));

        Mono<ServerResponse> responseMono = technologyHandler.validateTechnologies(serverRequest);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();

        verify(findTechnologiesServicePort, times(1)).areAllTechnologiesExist(dto.ids());
    }

    @Test
    void findTechnologiesByIds_Success() {
        TechnologyIdsRequestDTO dto = new TechnologyIdsRequestDTO(List.of(1L));
        Technology technology = new Technology(1L, "Spring Boot", "Framework reactivo");

        when(serverRequest.bodyToMono(TechnologyIdsRequestDTO.class)).thenReturn(Mono.just(dto));
        when(validator.validate(dto)).thenReturn(Collections.emptySet());
        when(findTechnologiesServicePort.findByIdIn(dto.ids())).thenReturn(Flux.just(technology));

        Mono<ServerResponse> responseMono = technologyHandler.findTechnologiesByIds(serverRequest);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();

        verify(findTechnologiesServicePort, times(1)).findByIdIn(dto.ids());
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
    void validateTechnologies_BusinessException_ReturnsBadRequest() {
        TechnologyIdsRequestDTO dto = new TechnologyIdsRequestDTO(List.of(1L));
        when(serverRequest.bodyToMono(TechnologyIdsRequestDTO.class)).thenReturn(Mono.just(dto));
        when(validator.validate(any(TechnologyIdsRequestDTO.class))).thenReturn(Set.of());
        when(findTechnologiesServicePort.areAllTechnologiesExist(dto.ids()))
                .thenReturn(Mono.error(new BusinessException(TechnicalMessage.INVALID_PARAMETERS)));

        Mono<ServerResponse> responseMono = technologyHandler.validateTechnologies(serverRequest);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.BAD_REQUEST))
                .verifyComplete();
    }

    @Test
    void findTechnologiesByIds_UnexpectedException_ReturnsInternalServerError() {
        TechnologyIdsRequestDTO dto = new TechnologyIdsRequestDTO(List.of(1L));
        when(serverRequest.bodyToMono(TechnologyIdsRequestDTO.class)).thenReturn(Mono.just(dto));
        when(validator.validate(any(TechnologyIdsRequestDTO.class))).thenReturn(Set.of());
        when(findTechnologiesServicePort.findByIdIn(dto.ids()))
                .thenReturn(Flux.error(new RuntimeException("Database timeout")));

        Mono<ServerResponse> responseMono = technologyHandler.findTechnologiesByIds(serverRequest);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR))
                .verifyComplete();
    }
}
