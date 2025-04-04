package org.example.firststep.repository;

import org.example.firststep.model.mongo.Person;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PersonRepository extends MongoRepository<Person, String> {
}
