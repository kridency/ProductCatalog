package org.example.productcatalog;

import org.example.productcatalog.web.listener.HttpListener;

import java.util.logging.Logger;

public class ProductCatalogApplication {

    public static void main(String[] args) {
        HttpListener.getInstance().startHTTPServer(8080);
    }
}
