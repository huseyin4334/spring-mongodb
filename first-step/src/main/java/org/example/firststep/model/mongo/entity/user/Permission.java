package org.example.firststep.model.mongo.entity.user;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.example.firststep.model.mongo.entity.MongoEntity;
import org.example.firststep.model.mongo.entity.NameDescription;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.data.mongodb.core.mapping.Unwrapped;

@Getter @Setter
@Document(collection = "permissions")
public class Permission extends MongoEntity<ObjectId, Permission> {
    @MongoId
    private ObjectId id;

    @Unwrapped(onEmpty = Unwrapped.OnEmpty.USE_NULL)
    private NameDescription nameDescription;

    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", nameDescription=" + nameDescription +
                '}';
    }
}
