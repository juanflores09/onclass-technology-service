package com.pragma.onclass_technology_service.infraestructure.entrypoints.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CapabilityTechnologiesRequestDTO (
        @NotNull(message = "Capability ID cannot be null")
        Long capabilityId,

        @NotEmpty(message = "Technology IDs list cannot be empty")
        List<Long> technologyIds
){
}
