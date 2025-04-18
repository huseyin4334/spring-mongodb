package org.example.firststep.model.mongo.entity.product;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.example.firststep.model.mongo.entity.MongoEntity;
import org.example.firststep.model.mongo.entity.SearchableNameDescription;
import org.example.firststep.model.mongo.entity.address.Address;
import org.springframework.data.mongodb.core.mapping.*;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
@Document(collection = "products")
public class Product extends MongoEntity<ObjectId, Product> {

    @MongoId
    private ObjectId id;

    @Field(name = "code")
    private String code;

    @Unwrapped(onEmpty = Unwrapped.OnEmpty.USE_NULL)
    SearchableNameDescription nameDescription;

    @Field(name = "price", targetType = FieldType.DECIMAL128)
    private BigDecimal price;

    @Field(name = "stock")
    private Stock stock;

    @Field(name = "features", targetType = FieldType.ARRAY)
    @DocumentReference(collection = "features", lazy = true)
    private List<Feature> features;

    @Field(name = "technologies", targetType = FieldType.ARRAY)
    @DocumentReference(collection = "technologies", lazy = true)
    private List<Technology> technologies;

    @Field(name = "warehouseAddress", targetType = FieldType.ARRAY)
    @DocumentReference(collection = "addresses", lazy = true)
    private Address warehouseAddress;
}
