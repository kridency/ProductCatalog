package org.example.productcatalog.terminal;

import org.example.productcatalog.entity.User;
import org.example.productcatalog.exception.ApplicationException;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.example.productcatalog.preset.ProductCatalogInit.FORCED_COMPLETION;
import static org.example.productcatalog.preset.ProductCatalogInit.UNAUTHORIZED;

public class AuthTerminal extends AbstractTerminal<User> {
    private static AuthTerminal INSTANCE;

    private AuthTerminal() {
        commandMenu = System.lineSeparator() + "\t\u001B[92mregister\u001B[0m (Регистрация пользователя)"
                + System.lineSeparator() + "\t\u001B[92mlogin\u001B[0m (Вход в систему)"
                + System.lineSeparator() + "\t\u001B[92mexit\u001B[0m (Завершение сеанса)";
        commands = new ConcurrentHashMap<>() {{
            put("register", userService::create);
            put("login", user -> {});
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
        if (command.equals("exit")) return null;

        System.out.println("===   Введите данные пользователя   === ");

        System.out.print("\t\tАдрес электронной почты :> ");
        var email = scanner.nextLine();
        System.out.print("\t\tПароль :> ");
        var password = scanner.nextLine();

        if (command.equals("login")) {
            Optional.ofNullable(userService.findByEmail(email)).filter(value -> value.getPassword().equals(password))
                    .ifPresentOrElse(AbstractTerminal::setPrincipal, ()->{throw new ApplicationException(UNAUTHORIZED);});
        } else if (command.equals("register")) {
            return userService.create(new User(email, password));
        }

        return getPrincipal();
    }

    @Override
    protected void print(User data) {}
}
