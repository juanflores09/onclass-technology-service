package com.pragma.onclass_technology_service.infraestructure.entrypoints.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TechnologyDTO(
        Long id,

        @NotBlank(message = "El nombre no puede estar vacío")
        @Size(max = 50, message = "El tamaño máximo del nombre debe ser de 50 caracteres")
        String name,

        @NotBlank(message = "La descripción no puede estar vacía")
        @Size(max = 90, message = "El tamaño máximo de la descripción debe ser de 90 caracteres")
        String description
) {
}
