package org.example.firststep.mapper;

import lombok.RequiredArgsConstructor;
import org.example.firststep.exception.ConversationException;
import org.example.firststep.model.external.PersistUser;
import org.example.firststep.model.mongo.entity.SearchableNameDescription;
import org.example.firststep.model.mongo.entity.address.Address;
import org.example.firststep.model.mongo.entity.user.MailAddress;
import org.example.firststep.model.mongo.entity.user.Role;
import org.example.firststep.model.mongo.entity.user.User;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Component
public class PersistUserToUserMapper {

    private final MongoTemplate mongoTemplate;

    public List<User> mapPersistUserToUserList(List<PersistUser> persistUser) throws ConversationException {
        return persistUser.stream()
                .map(this::mapPersistUserToUser)
                .toList();
    }

    public User mapPersistUserToUser(PersistUser persistUser) throws ConversationException {
        // Start with inner objects
        User user = mongoTemplate.findOne(
                Query.query(Criteria.where("userName")
                        .is(persistUser.getUserName())),
                User.class
        );

        if (user == null)
            user = createUser(persistUser);
        else
            updateUser(persistUser, user);

        return user;
    }

    private void updateUser(PersistUser persistUser, User user) {
        // User mail, password, username can not update
        user.setConntactMailAddress(new MailAddress(persistUser.getContactMail()));
        user.setUpdatedOn(new Date());
        user.setUserType(persistUser.getUserType());

        // Update the address
        Address address = user.getAddress();
        address.getNameDescription().setName(persistUser.getAddress().getName());
        address.getNameDescription().setDescription(persistUser.getAddress().getDescription());
        address.setLocation(
                new GeoJsonPoint(persistUser.getAddress().getCoordinates()[0], persistUser.getAddress().getCoordinates()[1])
        );

        // Update the roles
        List<Role> roles = user.getRoles();
        List<String> newRoles = persistUser.getRoles()
                .stream()
                .filter(r -> roles.stream().noneMatch(rr -> rr.getNameDescription().getName().equals(r)))
                .toList();

        roles.removeIf(r -> !persistUser.getRoles().contains(r.getNameDescription().getName()));

        List<Role> foundedRoles = mongoTemplate.find(
                Query.query(Criteria.where("name").in(newRoles)),
                Role.class
        );

        roles.addAll(foundedRoles);
    }

    private User createUser(PersistUser user) {
        // Create a new User object and set its properties
        User newUser = new User(
                user.getUserName(),
                user.getPassword(),
                user.getEmail(),
                user.getContactMail()
        );

        newUser.setCreatedOn(new Date());
        newUser.setUpdatedOn(new Date());
        newUser.setUserType(user.getUserType());

        // Set the address
        Address address = new Address();

        SearchableNameDescription searchableNameDescription = new SearchableNameDescription();
        searchableNameDescription.setName(user.getAddress().getName());
        searchableNameDescription.setDescription(user.getAddress().getDescription());
        address.setNameDescription(searchableNameDescription);

        address.setLocation(
                new GeoJsonPoint(user.getAddress().getCoordinates()[0], user.getAddress().getCoordinates()[1])
        );

        newUser.setAddress(address);

        // Get already exist roles
        List<Role> roles = mongoTemplate.find(
                Query.query(Criteria.where("name").in(user.getRoles())),
                Role.class
        );

        newUser.setRoles(roles);

        return newUser;
    }
}
