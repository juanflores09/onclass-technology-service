package com.pragma.onclass_technology_service.infraestructure.entrypoints.dto;

import java.util.List;

public record CapabilityTechnologiesResponseDTO (
        Long capabilityId,
        List<TechnologyDTO> technologies
) {
}
