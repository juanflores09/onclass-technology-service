package com.pragma.onclass_technology_service.infraestructure.entrypoints.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CapabilityIdsRequestDTO (
        @NotEmpty(message = "The capability IDs list cannot be empty")
        List<@NotNull Long> ids
){
}
