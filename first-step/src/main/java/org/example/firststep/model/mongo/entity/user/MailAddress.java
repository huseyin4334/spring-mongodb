package org.example.firststep.model.mongo.entity.user;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.firststep.model.mongo.entity.MongoEmbedded;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter @Setter
@AllArgsConstructor
public class MailAddress extends MongoEmbedded {

    @Email
    @Field(name = "email")
    private String email;

    @Override
    public String toString() {
        return "MailAddress{" +
                "email='" + email + '\'' +
                '}';
    }
}
