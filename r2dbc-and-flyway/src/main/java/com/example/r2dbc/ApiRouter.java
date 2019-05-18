package com.example.r2dbc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.status;

@Configuration
public class ApiRouter {

    @Bean
	public RouterFunction<ServerResponse> petsRouterFunction(PetHandler petHandler){
        return nest(applicationJsonOnly(), route()
				.path("/v1/demo", builder -> builder
						.GET("/pets/{petName}", petHandler::getSound)
						.GET("/hello",
								request -> status(HttpStatus.NOT_FOUND)
										.contentType(MediaType.APPLICATION_JSON)
										.body(fromObject("Sorry, API not found."))
						)
				)
				.path("/v2/demo", builder -> builder
						.GET("/pets/{petName}",
								request -> status(HttpStatus.NOT_FOUND)
										.contentType(MediaType.APPLICATION_JSON)
										.body(fromObject("Sorry, API not found."))
						)
				)
				.build()
		);
    }

    private static RequestPredicate applicationJsonOnly() {
    	return contentType(MediaType.APPLICATION_JSON).and(accept(MediaType.APPLICATION_JSON));
	}

}
