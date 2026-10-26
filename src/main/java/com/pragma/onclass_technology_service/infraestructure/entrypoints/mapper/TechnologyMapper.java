package com.pragma.onclass_technology_service.infraestructure.entrypoints.mapper;

import com.pragma.onclass_technology_service.domain.model.Technology;
import com.pragma.onclass_technology_service.infraestructure.entrypoints.dto.TechnologyDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TechnologyMapper {

    Technology technologyDtoToTechnology(TechnologyDTO dto);
}
