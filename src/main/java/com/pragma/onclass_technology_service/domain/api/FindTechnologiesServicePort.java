package com.pragma.onclass_technology_service.domain.api;

import com.pragma.onclass_technology_service.domain.model.CapabilityTechnologies;
import reactor.core.publisher.Flux;

import java.util.List;

public interface FindTechnologiesServicePort {

    Flux<CapabilityTechnologies> findTechnologiesByCapabilityId(List<Long> capabilityIds);

}
