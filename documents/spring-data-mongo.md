# MongoDb And Spring Boot

We have some ways to interact with MongoDB in Spring Boot:
- Template API
    - Convenience methods by MongoOperations
    - Execute callback methods
    - Fluent API
        - Fluent api uses for low level operations.
    - Exception translation
        - Spring Data MongoDB translates exceptions into Spring's DataAccessException hierarchy.
        - It can do it with `PersistenceExceptionTranslator ` interface.
        - Mongo exceptions root class is `MongoException`.
- Repository API
    - Repository interface

## Template Api
One of the first tasks when using MongoDB and Spring is to create a `MongoClient`.
The MongoClient is the main entry point for interacting with MongoDB.

Once configured, MongoTemplate is thread-safe and can be reused across multiple instances.
The MongoTemplate class implements the interface MongoOperations.

We have 5 options to create a `MongoClient`:
- `MongoClients.create()`
- `MongoClientFactoryBean` creates a `MongoClient` instance and exposes it as a Spring bean. Factory method uses for create an instance.
- `application.properties` file defines the connection properties.
    - Prefix is `spring.data.mongodb`.
    - `spring.data.mongodb.uri=mongodb://localhost:27017/test`
    - `spring.data.mongodb.host=localhost`
    - `spring.data.mongodb.port=27017`
    - `spring.data.mongodb.database=test`
- `MongoDatabaseFactory` interface defines a method for creating a `MongoDatabase` instance.
    - We will use this interface for create a `MongoDatabase` definition.
    - After that, we will give this definition to `MongoTemplate` for create a `MongoTemplate` instance.
- `AbstractMongoClientConfiguration` class is an abstract class that provides a base configuration for creating a `MongoClient` instance.

```java

import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
class MongoConfig {

    // 1.way
    @Bean
    public MongoTemplate mongoTemplate() {
        return MongoClients.create("mongodb://localhost:27017/test");
    }

    // 2.way
    @Bean
    public MongoClientFactoryBean mongoClientFactoryBean() {
        MongoClientFactoryBean factoryBean = new MongoClientFactoryBean();
        factoryBean.setHost("localhost");
        factoryBean.setPort(27017);
        return factoryBean;
    }

    // 4.way
    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory() {
        return new SimpleMongoClientDatabaseFactory(MongoClients.create(), "test");
    }
}
```

```java

import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
class MongoConfig extends AbstractMongoClientConfiguration {

    @Override
    public String getDatabaseName() {
        return "test";
    }

    @Override
    protected void configureClientSettings(Builder builder) {
        builder
                .credential(
                        MongoCredential.createCredential("username", "test", "password".toCharArray())
                )
                .applyToClusterSettings(settings ->
                        settings.hosts(singletonList(new ServerAddress("localhost", 27017)))
                );
    }
}
```

---

## Write Concern
Write concern defines the level of acknowledgment requested from MongoDB for write operations.
For example, we don't care a write operation is successful or not, we set `WriteConcern.UNACKNOWLEDGED`.
After that, mongo will return success when it write it to first node. It didn't wait for write it to other nodes.

We can configure it with `WriteConcernResolver` interface.

[Write Concern](https://docs.spring.io/spring-data/mongodb/reference/mongodb/template-config.html#mongo-template.writeresultchecking)


## Index And Collection