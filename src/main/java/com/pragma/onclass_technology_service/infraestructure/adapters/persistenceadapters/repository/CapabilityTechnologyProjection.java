package com.pragma.onclass_technology_service.infraestructure.adapters.persistenceadapters.repository;

public record CapabilityTechnologyProjection(
        Long capabilityId,
        Long id,
        String name,
        String description
) {
}
