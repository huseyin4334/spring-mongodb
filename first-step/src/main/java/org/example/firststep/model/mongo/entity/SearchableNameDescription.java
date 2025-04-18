package org.example.firststep.model.mongo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.annotation.Collation;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.TextScore;

@Collation(value = "en_US") // localization. We can change it when we search.
@CompoundIndex(name = "name_description_index", def = "{'name': 1, 'description': 1}")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class SearchableNameDescription extends MongoEmbedded {

    // @TextIndexed
    @Field(name = "name")
    private String name;

    @Field(name = "description")
    private String description;

    @TextScore
    Float score;

    @Override
    public String toString() {
        return "SearchableNameDescription{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", score=" + score +
                '}';
    }
}

/*
    * @TextIndexed: This annotation is used to indicate that the field should be indexed for text search.
    * But we want to search by name and description, so we use @CompoundIndex instead.
 */
