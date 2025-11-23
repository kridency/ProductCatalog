package org.example.productcatalog.web.handler;

import org.example.productcatalog.web.servlet.ProductServlet;

public class ProductHandler extends AbstractHandler {
    private static ProductHandler INSTANCE;

    private ProductHandler() {
        servlet = ProductServlet.getInstance();
    }

    public static ProductHandler getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ProductHandler();
        }
        return INSTANCE;
    }
}
