package com.pragma.onclass_technology_service.infraestructure.entrypoints.handler;

import com.pragma.onclass_technology_service.domain.api.CreateTechnologyServicePort;
import com.pragma.onclass_technology_service.domain.api.FindTechnologiesServicePort;
import com.pragma.onclass_technology_service.domain.emuns.TechnicalMessage;
import com.pragma.onclass_technology_service.domain.exceptions.BusinessException;
import com.pragma.onclass_technology_service.infraestructure.entrypoints.dto.ApiResponseDTO;
import com.pragma.onclass_technology_service.infraestructure.entrypoints.dto.TechnologyDTO;
import com.pragma.onclass_technology_service.infraestructure.entrypoints.dto.TechnologyIdsRequestDTO;
import com.pragma.onclass_technology_service.infraestructure.entrypoints.util.ErrorDTO;
import com.pragma.onclass_technology_service.infraestructure.entrypoints.mapper.TechnologyMapper;
import com.pragma.onclass_technology_service.infraestructure.entrypoints.util.Constants;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class TechnologyHandler {

    private final CreateTechnologyServicePort createTechnologyServicePort;
    private final FindTechnologiesServicePort findTechnologiesServicePort;
    private final TechnologyMapper technologyMapper;
    private final Validator validator;

    public Mono<ServerResponse> createTechnology(ServerRequest request) {

        String messageId = request.headers().firstHeader(Constants.X_MESSAGE_ID);

        return request.bodyToMono(TechnologyDTO.class)
                .flatMap(dto -> {

                    Set<ConstraintViolation<TechnologyDTO>> violations = validator.validate(dto);

                    if (!violations.isEmpty()) {
                        String errorMessage = violations.iterator().next().getMessage();
                        return Mono.error(new BusinessException(errorMessage, TechnicalMessage.INVALID_PARAMETERS));
                    }
                    return Mono.just(dto);
                })
                .map(technologyMapper::technologyDtoToTechnology)
                .flatMap(createTechnologyServicePort::createTechnology)
                .doOnSuccess(saved ->log.info("Technology successfully created  with messageId: {}", messageId))
                .flatMap(saved -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(saved))
                .contextWrite(Context.of(Constants.X_MESSAGE_ID, Objects.requireNonNull(messageId)))
                .onErrorResume(BusinessException.class, ex -> buildErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        messageId,
                        ex.getTechnicalMessage(),
                        List.of(ErrorDTO.builder()
                                    .code(ex.getTechnicalMessage().getCode())
                                    .message(ex.getMessage())
                                .build())
                ))
                .onErrorResume(ex -> {
                    log.error("[createTechnology] Unexpected error occurred for messageId: {}", messageId, ex);
                    return buildErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            messageId,
                            TechnicalMessage.INTERNAL_ERROR,
                            List.of(ErrorDTO.builder()
                                        .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                                        .message(TechnicalMessage.INTERNAL_ERROR.getMessage())
                                    .build())
                    );
                });
    }

    public Mono<ServerResponse> validateTechnologies(ServerRequest request) {

        String messageId = request.headers().firstHeader(Constants.X_MESSAGE_ID);

        return request.bodyToMono(TechnologyIdsRequestDTO.class)
                .flatMap(dto -> {

                    Set<ConstraintViolation<TechnologyIdsRequestDTO>> violations = validator.validate(dto);
                    if (!violations.isEmpty()) {
                        String errorMessage = violations.iterator().next().getMessage();
                        return Mono.error(new BusinessException(errorMessage, TechnicalMessage.INVALID_PARAMETERS));
                    }
                    return Mono.just(dto);
                })
                .flatMap(dto -> findTechnologiesServicePort.areAllTechnologiesExist(dto.ids())
                )
                .flatMap(exists -> ServerResponse
                        .status(HttpStatus.OK)
                        .bodyValue(exists))
                .contextWrite(Context.of(Constants.X_MESSAGE_ID, Objects.requireNonNull(messageId)))
                .onErrorResume(BusinessException.class, ex -> buildErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        messageId,
                        ex.getTechnicalMessage(),
                        List.of(ErrorDTO.builder()
                                .code(ex.getTechnicalMessage().getCode())
                                .message(ex.getMessage())
                                .build())
                ))
                .onErrorResume(ex -> {
                    log.error("Unexpected error occurred for messageId: {}", messageId, ex);
                    return buildErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            messageId,
                            TechnicalMessage.INTERNAL_ERROR,
                            List.of(ErrorDTO.builder()
                                    .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                                    .message(TechnicalMessage.INTERNAL_ERROR.getMessage())
                                    .build())
                    );
                });
    }
    public Mono<ServerResponse> findTechnologiesByIds(ServerRequest request) {

        String messageId = request.headers().firstHeader(Constants.X_MESSAGE_ID);

        return request.bodyToMono(TechnologyIdsRequestDTO.class)
                .flatMap(dto -> {

                    Set<ConstraintViolation<TechnologyIdsRequestDTO>> violations = validator.validate(dto);
                    if (!violations.isEmpty()) {
                        String errorMessage = violations.iterator().next().getMessage();
                        return Mono.error(new BusinessException(errorMessage, TechnicalMessage.INVALID_PARAMETERS));
                    }
                    return Mono.just(dto);
                })
                .flatMap(dto -> findTechnologiesServicePort.findByIdIn(dto.ids())
                        .collectList()
                )
                .flatMap(technologies -> ServerResponse
                        .status(HttpStatus.OK)
                        .bodyValue(technologies))
                .contextWrite(Context.of(Constants.X_MESSAGE_ID, Objects.requireNonNull(messageId)))
                .onErrorResume(BusinessException.class, ex -> buildErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        messageId,
                        ex.getTechnicalMessage(),
                        List.of(ErrorDTO.builder()
                                .code(ex.getTechnicalMessage().getCode())
                                .message(ex.getMessage())
                                .build())
                ))
                .onErrorResume(ex -> {
                    log.error("[findTechnologiesByIds] Unexpected error occurred for messageId: {}", messageId, ex);
                    return buildErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            messageId,
                            TechnicalMessage.INTERNAL_ERROR,
                            List.of(ErrorDTO.builder()
                                    .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                                    .message(TechnicalMessage.INTERNAL_ERROR.getMessage())
                                    .build())
                    );
                });
    }

    private Mono<ServerResponse> buildErrorResponse(HttpStatus httpStatus, String identifier,
                                                    TechnicalMessage error, List<ErrorDTO> errors) {
        return Mono.defer(() -> {
            ApiResponseDTO apiErrorResponse = ApiResponseDTO.builder()
                    .code(error.getCode())
                    .message(error.getMessage())
                    .identifier(identifier)
                    .date(Instant.now().toString())
                    .errors(errors)
                    .build();
            return ServerResponse.status(httpStatus).bodyValue(apiErrorResponse);
        });
    }
}
