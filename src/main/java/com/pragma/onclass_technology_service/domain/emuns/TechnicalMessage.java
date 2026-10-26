package com.pragma.onclass_technology_service.domain.emuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TechnicalMessage {

    INTERNAL_ERROR("500", "Something went wrong, please try again"),
    TECHNOLOGY_ALREADY_EXISTS("400", "Ya existe una tecnología con ese nombre"),
    INVALID_PARAMETERS("400", "Parámetros inválidos"),
    TECHNOLOGY_LIST_EMPTY("400", "La lista de tecnologías no puede estar vacía");

    private final String code;
    private final String message;

}
