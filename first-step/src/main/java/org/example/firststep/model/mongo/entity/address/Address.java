package org.example.firststep.model.mongo.entity.address;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.example.firststep.model.mongo.entity.MongoEntity;
import org.example.firststep.model.mongo.entity.SearchableNameDescription;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.data.mongodb.core.mapping.Unwrapped;

@Getter @Setter
@Document(collection = "addresses")
public class Address extends MongoEntity<ObjectId, Address> {

    @MongoId
    private ObjectId id;

    @Unwrapped(onEmpty = Unwrapped.OnEmpty.USE_NULL)
    private SearchableNameDescription nameDescription;

    @GeoSpatialIndexed(name = "location", type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", nameDescription=" + nameDescription +
                ", location=" + location +
                '}';
    }
}
