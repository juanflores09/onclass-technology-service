package com.pragma.onclass_technology_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Technology {

    private Long id;
    private String name;
    private String description;
}
