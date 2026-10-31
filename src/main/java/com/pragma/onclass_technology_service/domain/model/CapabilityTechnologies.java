package com.pragma.onclass_technology_service.domain.model;

import java.util.List;

public record CapabilityTechnologies(
        Long capabilityId,
        List<Technology> technologies
) {
}
