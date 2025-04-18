package org.example.firststep.repository.mongo.product;

import com.mongodb.lang.Nullable;
import org.example.firststep.model.mongo.entity.product.Product;
import org.example.firststep.model.mongo.entity.product.StockStatus;
import org.example.firststep.model.mongo.populated.product.GroupedProductByStock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String>, ProductRepositoryCustom, ProductRepositoryAggregation {

    //@Hint() // -> We can tell the mongodb to use specified index.
    @Query(collation = "{'locale': 'en_US'}") // If I send null. It will use default en_US. I don't have to specify this, Because default en_US define on mongoDB.
    List<Product> findProductsByNameDescription_Name(String name, Pageable pageable, @Nullable Collation collation);

    List<Product> findProductsByWarehouseAddress_Location(GeoJsonPoint warehouseAddressLocation);

    List<Product> findProductsByWarehouseAddress_LocationNear(GeoJsonPoint selectedLocation);

    GeoResults<Product> findProductsByWarehouseAddress_LocationNear(GeoJsonPoint selectedLocation, Distance distance);

    GeoResults<Product> findProductsByWarehouseAddress_LocationNear(GeoJsonPoint selectedLocation, Distance min, Distance max);

    // Text queries
    Page<Product> findProductsBy(TextCriteria criteria, Pageable pageable);


    // Projection
    /*
        * fields uses for get specific fields
        * exists uses for
     */
    @Query(
            value = "{price: {$gte: ?0}}",
            fields = "{nameDescription.name: 1, price: 1, stock: 1}",
            exists = false,
            count = false,
            delete = false, // Here uses for
            sort = "{price: 1}"
    )
    Page<Product> findProductsByPriceGreaterThanEqual(BigDecimal price, Pageable pageable);

    @Query(
            value = "{price: {$gte: ?0}}",
            fields = "{_id:  1}",
            exists = true,
            count = false,
            delete = false
    )
     boolean existsByPriceGreaterThanEqual(BigDecimal price);

    @Query(
            value = "{price: {$gte: ?0}}",
            fields = "{_id:  1}",
            exists = false,
            count = true,
            delete = false
    )
    long countAllByPriceGreaterThanEqual(BigDecimal price);


    // Update
    // pipeline uses for multiple updates.
    // Otherwise, we can write directly
    @Query(value = "{code: ?0}")
    @Update(update = "{'$inc': {'stock.qty': ?1}, '$set': { 'stock.status': ?2 }}")
    void findAndChangeStockQtyAndStockAvailability(String code, int increment, StockStatus status);
}
