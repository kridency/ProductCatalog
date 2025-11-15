package org.example.productcatalog;

import org.example.productcatalog.entity.RoleType;
import org.example.productcatalog.exception.ApplicationException;
import org.example.productcatalog.exception.ExitException;
import org.example.productcatalog.terminal.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.example.productcatalog.preset.ProductCatalogInit.*;

public class ProductCatalogApplication {
    private static final String COMMAND_MENU;
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final Map<String, ? extends AbstractTerminal<?>> terminals;

    static {
        terminals = new HashMap<>() {{
            put("authentication", AuthTerminal.getInstance());
            put("identity", UserTerminal.getInstance());
            put("product", ProductTerminal.getInstance());
        }};

        COMMAND_MENU = System.lineSeparator() + "\t\u001B[92midentity\u001B[0m (Управление пользователями)"
                + System.lineSeparator() + "\t\u001B[92mproduct\u001B[0m (Управление товарами)"
                + System.lineSeparator() + "\t\u001B[92mlogout\u001B[0m (Выход из системы)";
    }

    private static final Logger LOGGER = Logger.getLogger(ProductCatalogApplication.class.getName());

    public static void main(String[] args) {
        try {
            while (true) {
                Optional.ofNullable(AbstractTerminal.getPrincipal()).ifPresentOrElse(user -> {
                    var isAdmin = user.getRole().equals(RoleType.ROLE_ADMIN);

                    System.out.println((isAdmin ? System.lineSeparator() + "\tadministration (Администрирование)" : "")
                            + COMMAND_MENU);
                    System.out.print(COMMAND_PROMPT);
                    var command = SCANNER.nextLine();
                    if(!isAdmin && command.equals("administration")) {
                        throw new ApplicationException(INPUT_ERROR);
                    }

                    try {
                        Optional.ofNullable(terminals.get(command))
                                .ifPresentOrElse(AbstractTerminal::runCommands,
                                        () -> {
                                            if (command.equals("logout")) {
                                                AbstractTerminal.getAuditor()
                                                        .audit(Instant.now() + " User: " + user.getEmail() + "; Successfully signed out");
                                                AbstractTerminal.setPrincipal(null);
                                            } else {
                                                throw new ApplicationException(INPUT_ERROR);
                                            }
                                        });
                    } catch (ApplicationException e) {
                        if(!e.getMessage().equals("return"))
                            System.out.println(e.getMessage());
                    }
                }, () -> terminals.get("authentication").runCommands());
            }
        } catch (ExitException e) {
            LOGGER.log(Level.INFO, e.getMessage());
            System.exit(0);
        }
    }
}
