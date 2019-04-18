package com.example.jdbcflyway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	private JdbcTemplate jdbcTemplate;

	@GetMapping
	public String getName(@RequestParam int id) {
		return jdbcTemplate.queryForObject("SELECT name FROM `sample_table` WHERE id = ?", String.class, id);
	}
}