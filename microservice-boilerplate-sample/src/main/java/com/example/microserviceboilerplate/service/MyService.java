package com.example.microserviceboilerplate.service;

import com.example.microserviceboilerplate.exception.SomeCustomException;
import com.example.microserviceboilerplate.repository.MyRepository;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class MyService {

	@Autowired
	private HttpHeaders httpHeaders;
	@Autowired
	private MyRepository myRepository;

	public String getName(int id) {
		if (httpHeaders.containsKey("userId")) {
			log.info("User: {}", httpHeaders.getFirst("userId"));
		} else {
			throw new SomeCustomException();
		}
		String name = myRepository.getName(id);
		return name != null ? name + "0" : "DEFAULT";
	}
}
