package org.example.productcatalog.terminal;

import org.example.productcatalog.audit.AuditProxyFactory;
import org.example.productcatalog.entity.Product;
import org.example.productcatalog.exception.ApplicationException;
import org.example.productcatalog.service.CrudService;
import org.example.productcatalog.service.ProductService;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import static org.example.productcatalog.preset.ProductCatalogInit.RETURN;

public class ProductTerminal extends AbstractTerminal<Product> {
    private static ProductTerminal INSTANCE;

    private ProductTerminal() {
        service = AuditProxyFactory.<CrudService<Product>>createAuditedProxy(
                ProductService.getInstance(),
                CrudService.class,
                auditor);
        commandMenu = System.lineSeparator() + "\t\u001B[92madd\u001B[0m (Добавление товара)"
                + System.lineSeparator() + "\t\u001B[92mupdate\u001B[0m (Изменение товара)"
                + System.lineSeparator() + "\t\u001B[92mdelete\u001B[0m (Удаление товара)"
                + System.lineSeparator() + "\t\u001B[92mlist\u001B[0m (Список товаров)"
                + System.lineSeparator() + "\t\u001B[92mreturn\u001B[0m (Возврат в главное меню)";
        commands = new ConcurrentHashMap<>() {{
            put("add", service::create);
            put("update", service::update);
            put("delete", service::remove);
            put("list", product -> print(product));
            put("return", product -> {});
        }};
    }

    public static ProductTerminal getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ProductTerminal();
        }
        return INSTANCE;
    }

    @Override
    public Product processCommand(String command) {
        Product product = new Product(null, null, null, null, null);

        if (command.equals("return")) { throw new ApplicationException(RETURN); }

        System.out.println("===   Введите описание товара / значение критериев поиска  === ");

        System.out.print("\t\tАртикул [] :> ");

        if (command.equals("list")) {
            product.setItem(Optional.of(scanner.nextLine()).filter(Predicate.not(String::isEmpty)).orElse(null));
        } else product.setItem(Optional.of(scanner.nextLine()).filter(Predicate.not(String::isEmpty))
                .orElseThrow(() -> new ApplicationException("Указан пустой артикул!")));

        if (command.equals("delete")) return product;

        System.out.print("\t\tТорговая марка товара [] :> ");
        product.setBrand(Optional.of(scanner.nextLine()).filter(Predicate.not(String::isEmpty)).orElse(null));
        System.out.print("\t\tНаименование товара [] :> ");
        product.setTitle(Optional.of(scanner.nextLine()).filter(Predicate.not(String::isEmpty)).orElse(null));
        System.out.print("\t\tКатегория товара [] :> ");
        product.setCategory(Optional.of(scanner.nextLine()).filter(Predicate.not(String::isEmpty)).orElse(null));
        System.out.print("\t\tЦена товара [] :> ");
        Optional.of(scanner.nextLine()).filter(Predicate.not(String::isEmpty))
                .ifPresentOrElse(string -> product.setPrice(Double.parseDouble(string)), () -> {});

        return product;
    }

    @Override
    public void print(Product product) {
        System.out.println("\n\t\t\tАртикул\t\t\t|\t\tТорговая марка\t|\t\tНаименование\t|\t\tКатегория\t|\t\tЦена");
        service.findFiltered(product).forEach(value -> System.out.println("-".repeat(120) + "\n"
                        + "\t" + (value.getItem().length() <= 24 ?
                value.getItem() + " ".repeat(24 - value.getItem().length()) : value.getItem().substring(0, 24))
                        + "|\t" + (value.getBrand().length() <= 20 ?
                value.getBrand() + " ".repeat(20 - value.getBrand().length()) : value.getBrand().substring(0, 20))
                        + "|\t" + (value.getTitle().length() <= 20 ?
                value.getTitle() + " ".repeat(20 - value.getTitle().length()) : value.getTitle().substring(0, 20))
                        + "|\t" + (value.getCategory().length() <= 16 ?
                value.getCategory() + " ".repeat(16 - value.getCategory().length()) : value.getCategory().substring(0, 16))
                        + "|\t" + value.getPrice())
        );
    }
}
