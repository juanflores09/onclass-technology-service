package com.pragma.onclass_technology_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Technology {

    private Long id;
    private String name;
    private String description;
}
