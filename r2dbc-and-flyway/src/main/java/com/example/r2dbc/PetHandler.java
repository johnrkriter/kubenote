package com.example.r2dbc;

import com.example.r2dbc.repository.PetRepository;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

@Component
@Log4j2
public class PetHandler {

    @Autowired
    private PetRepository petRepository;

    public Mono<ServerResponse> getSound(ServerRequest serverRequest){
        String featureName = serverRequest.pathVariable("petName");
        return ServerResponse
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(petRepository.getSound(featureName), String.class);
    }

}


