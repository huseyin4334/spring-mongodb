package org.example.firststep.model.mongo.populated.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.firststep.model.mongo.entity.product.Product;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
@AllArgsConstructor
public class GroupedProductByStock {
    private List<Product> products;
    private int totalStock;

    // Price statistics
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BigDecimal avgPrice;
}
