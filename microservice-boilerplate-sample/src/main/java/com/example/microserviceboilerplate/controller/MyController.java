package com.example.microserviceboilerplate.controller;

import com.example.microserviceboilerplate.service.MyService;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@Log4j2
public class MyController {

	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private MyService myService;

	@GetMapping(value = "/name", produces = MediaType.APPLICATION_JSON_VALUE)
	public String getName(@RequestParam int id) {
		return myService.getName(id);
	}

	@GetMapping
	public String test(@RequestHeader String correlationId) {
		log.info("logging with Log4j2 - hello! {}", correlationId);
		return restTemplate.getForObject("http://localhost:8080/hello", String.class);
	}
	@GetMapping("hello")
	public String test2(@RequestHeader HttpHeaders httpHeaders) {
		log.info("logging with Log4j2 - hello again!");
		return "OK!";
	}

}
