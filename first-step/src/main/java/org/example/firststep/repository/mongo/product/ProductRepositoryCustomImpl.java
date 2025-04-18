package org.example.firststep.repository.mongo.product;

import lombok.RequiredArgsConstructor;
import org.example.firststep.model.mongo.entity.product.Product;
import org.example.firststep.model.mongo.entity.product.StockStatus;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationUpdate;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.query.Criteria;

import static org.springframework.data.mongodb.core.aggregation.ComparisonOperators.valueOf;
import static org.springframework.data.mongodb.core.aggregation.ConditionalOperators.Switch.CaseOperator.when;

@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    // Custom methods can be implemented here using mongoTemplate

    public void updateStockStatuses(String productCode) {
        // Aggregation pipeline update method.
        AggregationUpdate update = Aggregation.newUpdate()
                .set("stock.status").toValue(
                        ConditionalOperators.switchCases(
                                when(
                                        valueOf("stock.qty")
                                                .greaterThanEqualToValue(0)
                                        ).then(StockStatus.IN_STOCK),
                                when(
                                        valueOf("stock.qty")
                                                .equalToValue(0)
                                ).then(StockStatus.OUT_OF_STOCK),
                                when(
                                        valueOf("stock.qty")
                                                .lessThanEqualToValue(0)
                                ).then(StockStatus.UNKNOWN)
                        )
                );

        mongoTemplate.update(Product.class)
                .matching(Criteria.where("productCode").is(productCode))
                .apply(update)
                .all();
    }

    public void updateStockStatuses() {
        // Aggregation pipeline update method.
        AggregationUpdate update = Aggregation.newUpdate()
                .set("stock.status").toValue(
                        ConditionalOperators.switchCases(
                                when(
                                        valueOf("stock.qty")
                                                .greaterThanEqualToValue(0)
                                ).then(StockStatus.IN_STOCK),
                                when(
                                        valueOf("stock.qty")
                                                .equalToValue(0)
                                ).then(StockStatus.OUT_OF_STOCK),
                                when(
                                        valueOf("stock.qty")
                                                .lessThanEqualToValue(0)
                                ).then(StockStatus.UNKNOWN)
                        )
                );

        mongoTemplate.update(Product.class)
                .apply(update)
                .all();
    }
}
