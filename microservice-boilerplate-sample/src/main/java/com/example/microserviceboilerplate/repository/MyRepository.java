package com.example.microserviceboilerplate.repository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
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
