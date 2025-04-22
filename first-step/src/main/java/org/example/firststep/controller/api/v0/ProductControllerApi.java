package org.example.firststep.controller.api.v0;

import lombok.RequiredArgsConstructor;
import org.example.firststep.repository.mongo.product.ProductRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v0/products")
@RestController
public class ProductControllerApi {

    private final ProductRepository productRepository;


}
