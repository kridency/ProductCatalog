package org.example.productcatalog.web.handler;

import org.example.productcatalog.web.servlet.ProductServlet;

public class ProductHandler extends AbstractHandler {
    public ProductHandler() {
        servlet = new ProductServlet();
    }
}
