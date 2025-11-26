package org.example.productcatalog.service;


import org.example.productcatalog.AbstractTest;
import org.example.productcatalog.entity.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ProductServiceTest extends AbstractTest {
    private static final ProductService productService = Mockito.mock(ProductService.class);

    @Test
    void givenCurrentUserAndProduct_whenTryToAdd_thenReturnCorrectResult() {
        var newProduct = new Product("I12","", "t-shirt", "Clothes", 20.0);
        newProduct = productService.create(newProduct);

        Assertions.assertEquals("t-shirt", newProduct.getTitle());
        Assertions.assertEquals(20.0, newProduct.getPrice());
    }
}
