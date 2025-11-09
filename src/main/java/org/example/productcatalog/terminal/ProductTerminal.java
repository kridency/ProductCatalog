package org.example.productcatalog.terminal;

import org.example.productcatalog.audit.AuditProxyFactory;
import org.example.productcatalog.entity.Product;
import org.example.productcatalog.exception.ApplicationException;
import org.example.productcatalog.service.CrudService;
import org.example.productcatalog.service.ProductService;

import java.util.concurrent.ConcurrentHashMap;

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
            put("return", user -> {});
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
        Product product = new Product(null, null, null, null, 0.0);

        if (command.equals("return")) { throw new ApplicationException(RETURN); }
        else if (command.equals("list")) { return product; }

        System.out.println("===   Введите описание товара   === ");

        System.out.print("\t\tАртикул :> ");
        product.setItem(scanner.nextLine());

        if (command.equals("delete")) return product;

        System.out.print("\t\tТорговая марка товара :> ");
        product.setBrand(scanner.nextLine());
        System.out.print("\t\tНаименование товара :> ");
        product.setTitle(scanner.nextLine());
        System.out.print("\t\tКатегория товара :> ");
        product.setCategory(scanner.nextLine());
        System.out.print("\t\tЦена товара :> ");
        product.setPrice(Double.parseDouble(scanner.nextLine()));

        return product;
    }

    @Override
    public void print(Product ignore) {
        System.out.println("\n\t\t\tАртикул\t\t\t|\t\tТорговая марка\t|\t\tНаименование\t|\t\tКатегория\t|\t\tЦена");
        service.findAll().forEach(value -> System.out.println("-".repeat(120) + "\n"
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
