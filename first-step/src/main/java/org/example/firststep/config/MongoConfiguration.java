package org.example.firststep.config;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

@RequiredArgsConstructor
@Configuration
public class MongoConfiguration {

    private final Environment environment;

    // We don't have to do this configuration like this. spring boot will autoconfigure it for us.
    @Bean
    MongoClient mongoClient() {
        return MongoClients.create(
            new ConnectionString(
                environment.getRequiredProperty("spring.data.mongodb.uri") // mongodb://localhost:27017
            )
        );
    }

    @Bean
    MongoOperations mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, environment.getRequiredProperty("spring.data.mongodb.database"));
    }

}
