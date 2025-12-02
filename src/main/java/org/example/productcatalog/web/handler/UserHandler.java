package org.example.productcatalog.web.handler;

import org.example.productcatalog.mapper.UserMapper;
import org.example.productcatalog.repository.UserRepository;
import org.example.productcatalog.service.UserService;
import org.example.productcatalog.web.controller.UserServlet;

public class UserHandler extends AbstractHandler {
    public UserHandler() {
        servlet = new UserServlet(
                new UserService(new UserRepository(), UserMapper.getInstance())
        );
    }
}
