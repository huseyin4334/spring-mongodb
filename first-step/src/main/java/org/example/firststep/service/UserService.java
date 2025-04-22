package org.example.firststep.service;

import com.mongodb.ClientSessionOptions;
import com.mongodb.client.ClientSession;
import lombok.RequiredArgsConstructor;
import org.example.firststep.model.mongo.entity.user.Permission;
import org.example.firststep.model.mongo.entity.user.Role;
import org.example.firststep.model.mongo.entity.user.User;
import org.example.firststep.repository.mongo.user.UserRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    private final MongoTemplate mongoTemplate;

    public User updateLastLoginDate(String username) {
        /*
        mongoTemplate.updateMulti(
                Query.query(Criteria.where("userName").is(username)),
                Update.update("lastLoginDate", new Date()),
                User.class,
                "users"
        );
         */

        ClientSessionOptions sessionOptions = ClientSessionOptions.builder()
                .causallyConsistent(true)
                .build();

        ClientSession clientSession = mongoTemplate.getMongoDatabaseFactory().getSession(sessionOptions);

        return mongoTemplate.withSession(() -> clientSession)
                .execute(action -> {
                    clientSession.startTransaction();

                    try {
                        Query query = new Query();
                        query.addCriteria(Criteria.where("userName").is(username));

                        Update update = new Update();
                        update.set("lastLoginDate", new Date());

                        mongoTemplate.updateFirst(query, update, User.class);

                        clientSession.commitTransaction();
                    } catch (Exception e) {
                        clientSession.abortTransaction();
                    }

                    return mongoTemplate.findOne(Query.query(Criteria.where("userName").is(username)), User.class);
                }, ClientSession::close);
    }

    /*
        Transactional uses MongoTransactionManager
        If any exception occurs, it will rollback the transaction.

        The default prefix is "mongo:" for label namespace.
        MongoTransactionOptionsResolver is used to resolve the transaction options.

        https://docs.spring.io/spring-data/mongodb/reference/mongodb/client-session-transactions.html
     */
    @Transactional(label = {
            "mongo:readConcern=available", // available means that the read operation will not wait for the write operation to be acknowledged.
            "mongo:readPreference=primary", // primary means that the read operation will be directed to the primary node of the replica set.
            "mongo:writeConcern=majority", // majority means that the write operation must be acknowledged by the majority of the nodes in the replica set.
    })
    public User loginUser(String username) throws IllegalAccessException {
        mongoTemplate.updateFirst(
                Query.query(Criteria.where("userName").is(username)),
                Update.update("lastLoginDate", new Date()),
                User.class,
                "users"
        );

        loginProcesses(mongoTemplate.findOne(
                Query.query(Criteria.where("userName").is(username)),
                User.class
        ));

        return mongoTemplate.findOne(Query.query(Criteria.where("userName").is(username)), User.class);
    }

    private void loginProcesses(User userName) throws IllegalAccessException {
        throw new IllegalAccessException("You are not allowed to access this method");
    }

}
