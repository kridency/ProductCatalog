package org.example.productcatalog.web.handler;

import org.example.productcatalog.mapper.ProductMapper;
import org.example.productcatalog.mapper.UserMapper;
import org.example.productcatalog.repository.ProductRepository;
import org.example.productcatalog.repository.UserRepository;
import org.example.productcatalog.service.ProductService;
import org.example.productcatalog.service.UserService;
import org.example.productcatalog.util.cache.ProductCacheManager;
import org.example.productcatalog.web.controller.ProductServlet;

public class ProductHandler extends AbstractHandler {
    public ProductHandler() {
        servlet = new ProductServlet(
                new UserService(new UserRepository(), UserMapper.getInstance()),
                new ProductService(new ProductRepository(), ProductMapper.getInstance(), new ProductCacheManager())
        );
    }
}
