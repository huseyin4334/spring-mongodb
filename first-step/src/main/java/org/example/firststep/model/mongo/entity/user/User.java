package org.example.firststep.model.mongo.entity.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.example.firststep.converters.PasswordConverter;
import org.example.firststep.model.mongo.entity.MongoEntity;
import org.example.firststep.model.mongo.entity.address.Address;
import org.springframework.data.convert.ValueConverter;
import org.springframework.data.mongodb.core.mapping.*;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@Document(collection = "users")
@Getter @Setter
public class User extends MongoEntity<ObjectId, User> {
    // https://stackoverflow.com/questions/59466606/what-is-use-of-mongoid-in-spring-data-mongodb-over-id
    @MongoId(targetType = FieldType.OBJECT_ID)
    private ObjectId id;

    @Field(name = "userName")
    private String userName;

    // ExplicitEncrypted is used to encrypt the field value.
    // Our custom PasswordConverter is used to encrypt and decrypt the password to like ExplicitEncrypted.
    //@ExplicitEncrypted(algorithm = "AES/ECB/PKCS5Padding", keyAltName = "userKey")
    @Field(name = "password", targetType = FieldType.STRING)
    @ValueConverter(PasswordConverter.class)
    private String password;

    /*
        Unwrap uses the unwrap values where the embedded object.
        { _id: ..., username: ..., password: ..., email_email: ..., contact_email: ... }
     */
    @Unwrapped(onEmpty = Unwrapped.OnEmpty.USE_NULL, prefix = "email_")
    private MailAddress mailAddress;

    @Unwrapped(onEmpty = Unwrapped.OnEmpty.USE_NULL, prefix = "contact_")
    private MailAddress conntactMailAddress;

    @Field(name = "roles", targetType = FieldType.ARRAY)
    // DbRef and DocumentReference are not the same.
    @DocumentReference(collection = "roles")
    private List<Role> roles;

    @Field(name = "userType", targetType = FieldType.STRING)
    private UserType userType;

    @Field(name = "address", targetType = FieldType.OBJECT_ID)
    @DocumentReference(collection = "addresses")
    private Address address;

    @Field(name = "lastLogin", targetType = FieldType.DATE_TIME)
    private Date lastLogin;

    @Field(name = "createdOn", targetType = FieldType.DATE_TIME)
    private Date createdOn;

    @Field(name = "updatedOn", targetType = FieldType.DATE_TIME)
    private Date updatedOn;

    public User(String username, String password, String email, String contactEmail) {
        this.userName = username;
        this.password = password;
        this.mailAddress = new MailAddress(email);
        this.conntactMailAddress = new MailAddress(contactEmail);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", mailAddress=" + mailAddress +
                ", conntactMailAddress=" + conntactMailAddress +
                ", roles=" + roles +
                ", userType=" + userType +
                ", address=" + address +
                ", lastLogin=" + lastLogin +
                ", createdOn=" + createdOn +
                ", updatedOn=" + updatedOn +
                '}';
    }
}
