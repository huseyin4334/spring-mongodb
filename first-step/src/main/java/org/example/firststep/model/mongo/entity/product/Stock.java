package org.example.firststep.model.mongo.entity.product;

import org.example.firststep.model.mongo.entity.MongoEmbedded;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

public class Stock extends MongoEmbedded {
    @Field(name = "qty", targetType = FieldType.INT32, write = Field.Write.ALWAYS)
    private int stock;

    @Field(name = "status", targetType = FieldType.STRING)
    private StockStatus stockStatus;
}
