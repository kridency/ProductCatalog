package org.example.productcatalog.terminal;

import org.example.productcatalog.audit.AuditProxyFactory;
import org.example.productcatalog.entity.User;
import org.example.productcatalog.exception.ApplicationException;
import org.example.productcatalog.service.CrudService;
import org.example.productcatalog.service.UserService;

import static org.example.productcatalog.preset.ProductCatalogInit.RETURN;

import java.util.concurrent.ConcurrentHashMap;

public class UserTerminal extends AbstractTerminal<User> {
    private static UserTerminal INSTANCE;

    private UserTerminal() {
        service = AuditProxyFactory.<CrudService<User>>createAuditedProxy(
                UserService.getInstance(),
                CrudService.class,
                auditor);
        commandMenu = System.lineSeparator() + "\t\u001B[92mupdate\u001B[0m (Редактирование профиля пользователя)"
                + System.lineSeparator() + "\t\u001B[92mdelete\u001B[0m (Удаление пользователя)"
                + System.lineSeparator() + "\t\u001B[92mreturn\u001B[0m (Возврат в главное меню)";
        commands = new ConcurrentHashMap<>() {{
            put("update", service::update);
            put("delete", service::remove);
            put("return", user -> {});
        }};
    }

    public static UserTerminal getInstance() {
        if(INSTANCE == null) INSTANCE = new UserTerminal();
        return INSTANCE;
    }

    @Override
    public User processCommand(String command) {
        User user = getPrincipal();

        if (command.equals("delete")) {
            return user;
        } else if (command.equals("return")) {
            throw new ApplicationException(RETURN);
        }

        System.out.println("===   Введите данные пользователя   === ");

        System.out.print("\t\tАдрес электронной почты :> ");
        user.setEmail(scanner.nextLine());
        System.out.print("\t\tПароль :> ");
        user.setPassword(scanner.nextLine());

        return user;
    }

    @Override
    public void print(User ignore) {
        System.out.println("\n\t\t\tИдентификатор\t\t\t|\t\tЭлектронная почта\t|\t\tПароль");
        System.out.println("-".repeat(50));
        service.findAll().forEach(value ->
                System.out.println(value.getId() + "\t\t|\t\t" + value.getEmail() + "\t\t|\t\t" + value.getPassword())
        );
    }
}
