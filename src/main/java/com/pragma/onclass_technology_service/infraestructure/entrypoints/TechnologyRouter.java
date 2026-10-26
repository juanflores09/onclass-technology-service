package com.pragma.onclass_technology_service.infraestructure.entrypoints;

import com.pragma.onclass_technology_service.infraestructure.entrypoints.handler.TechnologyHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class TechnologyRouter {

    @Bean
    public RouterFunction<ServerResponse> technologyRoutes(TechnologyHandler handler) {
        return route(POST("/api/v1/technologies"), handler::createTechnology);
    }
}
