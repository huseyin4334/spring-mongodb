package org.example.firststep.controller.api.v0;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.firststep.mapper.PersistUserToUserMapper;
import org.example.firststep.model.external.PersistUser;
import org.example.firststep.model.mongo.entity.user.User;
import org.example.firststep.repository.mongo.user.UserRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/v0/user")
@RestController
public class UserControllerApi {

    private final MongoTemplate mongoTemplate;
    private final UserRepository userRepository;
    private final PersistUserToUserMapper persistUserToUserMapper;


    @PostMapping("/import")
    public ResponseEntity<String> importUsers(@RequestBody List<PersistUser> persistUsers) {

        List<User> users = persistUserToUserMapper.mapPersistUserToUserList(persistUsers);

        for (User user : users) {
            if (user.getId() == null)
                mongoTemplate.insert(user);
            else
                mongoTemplate.save(user);
        }

        return ResponseEntity.badRequest().body("Users saved successfully");
    }


    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody PersistUser user) {
        boolean isExist = mongoTemplate
                .exists(Query.query(
                        Criteria.where("userName")
                                .is(user.getUserName())
                        ),
                        User.class
                );
        if (isExist)
            return ResponseEntity.badRequest().body("User already exists");

        User newUser = persistUserToUserMapper.mapPersistUserToUser(user);
        newUser = userRepository.save(newUser);
        log.info("User created: \n" + newUser);

        return ResponseEntity.ok("User created successfully");
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody PersistUser user) {
        boolean isExist = mongoTemplate
                .exists(Query.query(
                                Criteria.where("userName")
                                        .is(user.getUserName())
                        ),
                        User.class
                );
        if (!isExist)
            return ResponseEntity.badRequest().body("User not found");

        User replaceUser = persistUserToUserMapper.mapPersistUserToUser(user);
        replaceUser = userRepository.save(replaceUser);
        log.info("User updated: \n" + replaceUser);

        return ResponseEntity.ok("User created successfully");
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> getUser(
            @RequestParam(value = "userName", required = false) String userName,
            @RequestParam(value = "email", required = false) String email
    ) {
        Query query = new Query();

        if (userName != null)
            query.addCriteria(Criteria.where("userName").is(userName));
        if (email != null)
            query.addCriteria(Criteria.where("email").is(email));

        List<User> users = mongoTemplate.find(query, User.class);
        if (users.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(users);
    }
}
