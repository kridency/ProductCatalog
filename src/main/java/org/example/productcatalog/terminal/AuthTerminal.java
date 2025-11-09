package org.example.productcatalog.terminal;

import org.example.productcatalog.entity.User;
import org.example.productcatalog.exception.ApplicationException;
import org.example.productcatalog.service.UserService;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.example.productcatalog.preset.ProductCatalogInit.FORCED_COMPLETION;
import static org.example.productcatalog.preset.ProductCatalogInit.UNAUTHORIZED;

public class AuthTerminal extends AbstractTerminal<User> {
    private static AuthTerminal INSTANCE;

    private AuthTerminal() {
        service = UserService.getInstance();
        commandMenu = System.lineSeparator() + "\t\u001B[92mregister\u001B[0m (Регистрация пользователя)"
                + System.lineSeparator() + "\t\u001B[92mlogin\u001B[0m (Вход в систему)"
                + System.lineSeparator() + "\t\u001B[92mexit\u001B[0m (Завершение сеанса)";
        commands = new ConcurrentHashMap<>() {{
            put("register", service::create);
            put("login", user -> {
                Optional.ofNullable(((UserService) service).findByEmail(user.getEmail()))
                        .filter(value -> value.getPassword().equals(user.getPassword()))
                        .ifPresentOrElse(AbstractTerminal::setPrincipal, ()->{throw new ApplicationException(UNAUTHORIZED);});

                auditor.audit(Instant.now() + " User: " + user.getEmail() + "; Successfully signed in");
            });
            put("exit", user -> { throw new RuntimeException(FORCED_COMPLETION); });
        }};
    }

    public static AuthTerminal getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new AuthTerminal();
        }
        return INSTANCE;
    }

    @Override
    protected User processCommand(String command) {
        User user = new User(null, null);

        if (command.equals("exit")) return user;

        System.out.println("===   Введите данные пользователя   === ");

        System.out.print("\t\tАдрес электронной почты :> ");
        user.setEmail(scanner.nextLine());
        System.out.print("\t\tПароль :> ");
        user.setPassword(scanner.nextLine());

        return user;
    }

    @Override
    protected void print(User data) {}
}
