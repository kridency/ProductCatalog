package org.example.productcatalog.terminal;

import org.example.productcatalog.audit.Auditor;
import org.example.productcatalog.entity.User;
import org.example.productcatalog.exception.ApplicationException;
import org.example.productcatalog.service.CrudService;
import org.example.productcatalog.service.UserService;

import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Consumer;

import static org.example.productcatalog.preset.ProductCatalogInit.*;

public abstract class AbstractTerminal<T> {
    protected static final Scanner scanner = new Scanner(System.in);
    protected static final Auditor auditor = Auditor.getInstance();
    protected static User principal;
    protected String commandMenu;
    protected Map<String, Consumer<T>> commands;
    protected CrudService<T, String> service;

    protected abstract T processCommand(String command);

    protected abstract void print(T data);

    public void runCommands() {
        var goBack = false;
        while(!goBack) {
            System.out.println(commandMenu);
            System.out.print(COMMAND_PROMPT);
            var command = scanner.nextLine();
            try {
                User principal;
                Optional.ofNullable(commands.get(command))
                        .ifPresentOrElse(consumer -> consumer.accept(processCommand(command)),
                                () -> { throw new ApplicationException(INPUT_ERROR); });
                principal = getPrincipal();
                goBack = command.equals("login") && principal != null;
                if (principal != null) {
                    try {
                        UserService.getInstance().findByEmail(principal.getEmail());
                    } catch (ApplicationException e) {
                        if (e.getMessage().equals(USER_NOT_FOUND)) {
                            setPrincipal(null);
                            goBack = true;
                        }
                    }
                }
            } catch (ApplicationException e) {
                if(e.getMessage().equals(RETURN) || e.getMessage().equals(UNAUTHORIZED))
                    goBack = true;
                else
                    System.out.println(e.getMessage());
            }
        }
    }

    public static void setPrincipal(User user) {
        principal = user;
    }

    public static User getPrincipal() {
        return principal;
    }

    public static Auditor getAuditor() { return auditor; }
}
