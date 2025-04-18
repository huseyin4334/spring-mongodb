package org.example.firststep.model.mongo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.annotation.Collation;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

@Collation(value = "en_US") // localization. We can change it when we search.
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class NameDescription extends MongoEmbedded {

    @Indexed
    @Field(name = "name")
    private String name;

    @Field(name = "description")
    private String description;

    @Override
    public String toString() {
        return "NameDescription{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
