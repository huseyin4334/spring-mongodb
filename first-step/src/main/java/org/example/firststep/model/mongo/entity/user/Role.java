package org.example.firststep.model.mongo.entity.user;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.example.firststep.model.mongo.entity.MongoEntity;
import org.example.firststep.model.mongo.entity.NameDescription;
import org.springframework.data.mongodb.core.mapping.*;

import java.util.List;

@Getter @Setter
@Document(collection = "roles")
public class Role extends MongoEntity<ObjectId, Role> {

    @MongoId
    private ObjectId id;

    @Unwrapped(onEmpty = Unwrapped.OnEmpty.USE_NULL)
    private NameDescription nameDescription;

    @Field(name = "permissions")
    @DocumentReference(collection = "permissions")
    private List<Permission> permissions;

    private void addPermission(Permission permission) {
        this.permissions.add(permission);
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", nameDescription=" + nameDescription +
                ", permissions=" + permissions +
                '}';
    }
}
