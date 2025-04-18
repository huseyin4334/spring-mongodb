package org.example.firststep.repository.mongo.user;

import org.bson.types.ObjectId;
import org.example.firststep.model.mongo.entity.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, ObjectId> {
    Optional<User> findByUserName(String username);
}
