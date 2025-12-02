package org.example.productcatalog.web.handler;

import org.example.productcatalog.web.controller.ProductServlet;

public class ProductHandler extends AbstractHandler {
    public ProductHandler() {
        servlet = new ProductServlet();
    }
}
