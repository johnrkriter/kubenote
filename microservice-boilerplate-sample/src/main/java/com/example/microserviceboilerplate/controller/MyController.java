package com.example.microserviceboilerplate.controller;

import com.example.microserviceboilerplate.service.MyService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@Slf4j
public class MyController {

	@Autowired
	private MyService myService;

	@GetMapping(value = "/name", produces = MediaType.APPLICATION_JSON_VALUE)
	public String getName(@RequestParam int id) {
		return myService.getName(id);
	}

	@GetMapping
	public String test(@RequestHeader String correlationId, @RequestHeader HttpHeaders httpHeaders) {
		log.info("logging with Log4j2 - hello! {}", correlationId);
		return new RestTemplate().exchange("http://localhost:8080/hello", HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class).getBody();
	}
	@GetMapping("hello")
	public String test2(@RequestHeader HttpHeaders httpHeaders) {
		log.info("logging with Log4j2 - hello again!");
		return new RestTemplate().exchange("http://localhost:8080/hello3", HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class).getBody();
	}
	@GetMapping("hello3")
	public String test3() {
		log.info("logging with Log4j2 - hello again3!");
		return "OK!";
	}

}
