package com.pragma.onclass_technology_service.infraestructure.adapters.persistenceadapters.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("technology")
public class TechnologyEntity {

    @Id
    private Long id;
    private String name;
    private String description;
}
