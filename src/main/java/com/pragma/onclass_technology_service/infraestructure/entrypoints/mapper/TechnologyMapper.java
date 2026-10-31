package com.pragma.onclass_technology_service.infraestructure.entrypoints.mapper;

import com.pragma.onclass_technology_service.domain.model.CapabilityTechnologies;
import com.pragma.onclass_technology_service.domain.model.Technology;
import com.pragma.onclass_technology_service.infraestructure.adapters.persistenceadapters.entity.TechnologyEntity;
import com.pragma.onclass_technology_service.infraestructure.entrypoints.dto.CapabilityTechnologiesResponseDTO;
import com.pragma.onclass_technology_service.infraestructure.entrypoints.dto.TechnologyDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TechnologyMapper {

    Technology technologyDtoToTechnology(TechnologyDTO dto);

    Technology entityToDomain(TechnologyEntity entity);

    List<CapabilityTechnologiesResponseDTO> toResponseDTO(List<CapabilityTechnologies> capabilityTechnologies);

}
