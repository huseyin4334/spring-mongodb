package org.example.firststep.service;

import lombok.RequiredArgsConstructor;
import org.example.firststep.model.mongo.Person;
import org.example.firststep.repository.PersonRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PersonService {
    private final PersonRepository personRepository;
    private final MongoTemplate mongoTemplate;

    public void savePerson(Person person) {
        mongoTemplate.save(person);
        System.out.println("Person saved");
    }

    // https://docs.spring.io/spring-data/mongodb/reference/mongodb/template-api.html#mongo-template.execute-callbacks
    public void executeSomething(Person person) {
        // MongoCollection<Document> -> connection
        // MongoCollection is a part of the MongoDB Java driver. It is a representation of a collection in a MongoDB database.
        // We can low level operations with the collection using the MongoCollection interface.
        Person person1 = mongoTemplate.execute(Person.class, connection -> {
            long count = connection.countDocuments();
            person.setAge((int) count);
            return person;
        });

        System.out.println(person1);
    }

    @Transactional
    public void savePersonTransactional(Person person) {
        personRepository.save(person);
        mongoTemplate.save(person);
    }
}
