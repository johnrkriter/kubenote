package com.example.microserviceboilerplate.controller;

import com.example.microserviceboilerplate.service.MyService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

	@Autowired
	private MyService myService;

	@GetMapping(value = "/name", produces = MediaType.APPLICATION_JSON_VALUE)
	public String getName(@RequestParam int id) {
		return myService.getName(id);
	}

}
