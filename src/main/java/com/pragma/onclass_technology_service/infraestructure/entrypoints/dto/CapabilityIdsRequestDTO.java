package com.pragma.onclass_technology_service.infraestructure.entrypoints.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CapabilityIdsRequestDTO (
        @NotEmpty(message = "The capability IDs list cannot be empty")
        @Size(max = 3, message = "You can query a maximum of 3 capabilities at a time")
        List<@NotNull Long> ids
){
}
