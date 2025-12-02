package org.example.productcatalog.web.handler;

import org.example.productcatalog.web.controller.UserServlet;

public class UserHandler extends AbstractHandler {
    public UserHandler() {
        servlet = new UserServlet();
    }
}
