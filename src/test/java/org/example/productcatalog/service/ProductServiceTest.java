package org.example.productcatalog.service;


import org.example.productcatalog.AbstractTest;
import org.example.productcatalog.entity.Product;
import org.example.productcatalog.terminal.AbstractTerminal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ProductServiceTest extends AbstractTest {
    private static final ProductService productService = Mockito.spy(ProductService.getInstance());

    @Test
    void givenCurrentUserAndProduct_whenTryToAdd_thenReturnCorrectResult() {
        var user = userService.findByEmail("name@hostname");
        AbstractTerminal.setPrincipal(user);

        var newProduct = new Product("I11","Adidas", "Sneakers", "Shoes", 100.0);
        newProduct = productService.create(newProduct);

        Assertions.assertEquals("Sneakers", newProduct.getTitle());
        Assertions.assertEquals(100.0, newProduct.getPrice());
    }
}
