package com.example.r2dbc;

import io.r2dbc.spi.ConnectionFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.connectionfactory.R2dbcTransactionManager;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

@SpringBootApplication
@EnableR2dbcRepositories
public class R2dbcApplication {

	public static void main(String[] args) {
		SpringApplication.run(R2dbcApplication.class, args);
	}

}

@Configuration
class MyConfig {

	@Autowired
	public TransactionalOperator transactionalOperator(ConnectionFactory connectionFactory) {
		ReactiveTransactionManager tm = new R2dbcTransactionManager(connectionFactory);
		return TransactionalOperator.create(tm);
	}

}