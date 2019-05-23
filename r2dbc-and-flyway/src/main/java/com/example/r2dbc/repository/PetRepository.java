package com.example.r2dbc.repository;

import com.example.r2dbc.model.Pet;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;

@Repository
public class PetRepository {

	@Autowired
	private DatabaseClient databaseClient;
	@Autowired
	private TransactionalOperator transactionalOperator;

	/*public Mono<String> getSound(String name) {
		return databaseClient
				.execute().sql("SELECT name, sound FROM pets WHERE name = :name")
				.bind("name", featureName)
				.as(Pet.class).fetch().one()
				.map(Pet::getSound)
				.switchIfEmpty(Mono.error(new NotFoundException("4000", "Pet not found!")));
	}*/

	public Mono<String> getSound(String name) {
		return databaseClient
				.execute().sql("SELECT sound FROM pets WHERE name = :name")
				.bind("name", name)
				.as(String.class).fetch().one()
				.switchIfEmpty(Mono.error(new RuntimeException("Pet not found!")));
	}

	public Mono<Pet> getOne(String name) {
		return databaseClient
				.execute().sql("SELECT name, sound FROM pets WHERE name = :name")
				.bind("name", name)
				.map((row, rowMetadata) -> new Pet(
						row.get("name", String.class),
						row.get("sound", String.class)
				)).one()
				.switchIfEmpty(Mono.error(new RuntimeException("Pet not found!")));
	}

	/*public Mono<Pet> getOne(String name) {
		return databaseClient
				.select().from("pets")
				.matching(Criteria.where("pets").is(name))
				.as(Pet.class).fetch().one()
				.switchIfEmpty(Mono.error(new RuntimeException("Pet not found!")));
	}*/

	public Mono<Void> transactionDemo(String name, String sound) {
		return databaseClient
				.insert().into("pets")
				.value("name", name)
				.value("sound", sound)
				.fetch()
				.rowsUpdated()
				.then(this.addDog())
				.then()
				.as(transactionalOperator::transactional);
	}

	private Mono<Integer> addDog() {
		return databaseClient
				.insert().into("pets")
				.value("name", "dog")
				.value("sound", "bark")
				.fetch()
				.rowsUpdated();
	}
}
