package com.example.microserviceboilerplate.repository;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Log4j2
public class MyRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public String getName(int id) {
		try {
			return jdbcTemplate.queryForObject("SELECT name FROM `sample_table` WHERE id = ?", String.class, id);
		} catch (EmptyResultDataAccessException ex) {
			log.warn("No result found");
			return null;
		}
	}
}
