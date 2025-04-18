package org.example.firststep.model.mongo.entity.product;

import lombok.Getter;
import lombok.Setter;
import org.example.firststep.model.mongo.entity.MongoEntity;
import org.example.firststep.model.mongo.entity.SearchableNameDescription;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.data.mongodb.core.mapping.Unwrapped;

@Getter @Setter
public class Feature extends MongoEntity<String, Feature> {

    @MongoId(targetType = FieldType.OBJECT_ID)
    private String id;

    @Unwrapped(onEmpty = Unwrapped.OnEmpty.USE_NULL)
    private SearchableNameDescription nameDescription;
}
