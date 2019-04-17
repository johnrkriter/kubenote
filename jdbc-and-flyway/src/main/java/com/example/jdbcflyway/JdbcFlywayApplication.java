package com.example.jdbcflyway;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class JdbcFlywayApplication {

	public static void main(String[] args) {
		SpringApplication.run(JdbcFlywayApplication.class, args);
	}

}

@RestController
class MyController {

	@Autowired
	private DataSource dataSource;

	@GetMapping
	public String hello() {
		return "hello";
	}
}