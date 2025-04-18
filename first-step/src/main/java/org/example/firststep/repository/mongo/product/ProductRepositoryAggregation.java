package org.example.firststep.repository.mongo.product;

import org.example.firststep.model.mongo.populated.product.GroupedProductByStock;
import org.springframework.data.mongodb.repository.Aggregation;

import java.util.List;

public interface ProductRepositoryAggregation {

    @Aggregation(
            pipeline = {
                    "{$match: { price: {$gt: 0.0}}}",
                    "{$bucket: {$groupBy: '$stockQty', boundaries: ?0, default: 'other', " +
                            "output: {" +
                            "products: {$push: $$ROOT}, " +
                            "totalStock: {$sum: $stock.qty}, " +
                            "minPrice: {$min: $price}, " +
                            "maxPrice: {$max: $price}, " +
                            "avgPrice: {$avg:  $price}}}}"
            }
    )
    List<GroupedProductByStock> groupByStockQty(int[] boundaries);
}
