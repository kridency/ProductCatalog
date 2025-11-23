package org.example.productcatalog.web.listener;

import com.sun.net.httpserver.HttpServer;
import org.example.productcatalog.exception.ApplicationException;
import org.example.productcatalog.web.handler.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpListener {
    private static HttpListener INSTANCE;
    private HttpServer httpServer;
    private final UserHandler userHandler;
    private final ProductHandler productHandler;

    private HttpListener() {
        userHandler = UserHandler.getInstance();
        productHandler = ProductHandler.getInstance();
    }

    public static HttpListener getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new HttpListener();
        }
        return INSTANCE;
    }

    public void stop() {
        if (httpServer != null) {
            httpServer.stop(0);
            httpServer = null;
        }
    }

    public void startHTTPServer(int port) {
        try {
            httpServer = HttpServer.create(new InetSocketAddress(port), 100);
            httpServer.createContext("/api/v1/auth/", userHandler);
            httpServer.createContext("/api/v1/identity/", userHandler);
            httpServer.createContext("/api/v1/administration/", userHandler);
            httpServer.createContext("/api/v1/product/", productHandler);
            httpServer.setExecutor(null);
            httpServer.start();
        } catch (IOException e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    public int getServerPort() {
        return httpServer.getAddress().getPort();
    }
}
