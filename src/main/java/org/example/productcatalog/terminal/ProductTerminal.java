package org.example.productcatalog.terminal;

import org.example.productcatalog.entity.Product;
import org.example.productcatalog.exception.ApplicationException;
import org.example.productcatalog.service.ProductService;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import static org.example.productcatalog.preset.ProductCatalogInit.RETURN;

public class ProductTerminal extends AbstractTerminal<Product> {
    private static ProductTerminal INSTANCE;
    private final ProductService productService = ProductService.getInstance();

    private ProductTerminal() {
        commandMenu = System.lineSeparator() + "\t\u001B[92madd\u001B[0m (Добавление товара)"
                + System.lineSeparator() + "\t\u001B[92mupdate\u001B[0m (Изменение товара)"
                + System.lineSeparator() + "\t\u001B[92mdelete\u001B[0m (Удаление товара)"
                + System.lineSeparator() + "\t\u001B[92mlist\u001B[0m (Список товаров)"
                + System.lineSeparator() + "\t\u001B[92mreturn\u001B[0m (Возврат в главное меню)";
        commands = new ConcurrentHashMap<>() {{
            put("add", productService::create);
            put("update", productService::update);
            put("delete", productService::remove);
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
        Product product;

        if (command.equals("return")) { throw new ApplicationException(RETURN); }
        else if (command.equals("list")) { return null; }

        System.out.println("===   Введите описание товара   === ");

        System.out.print("\t\tАртикул :> ");
        var item = scanner.nextLine();

        try {
            product = productService.findByItem(item);
        } catch (ApplicationException e) {
            product = null;
        }

        if (command.equals("delete") && product != null) {
            return product;
        }

        System.out.print("\t\tТорговая марка товара :> ");
        var brand = scanner.nextLine();
        System.out.print("\t\tНаименование товара :> ");
        var title = scanner.nextLine();
        System.out.print("\t\tКатегория товара :> ");
        var category = scanner.nextLine();
        System.out.print("\t\tЦена товара :> ");
        var price = Double.parseDouble(scanner.nextLine());

        var newProduct = new Product(item, brand, title, category, price);
        newProduct.setId(product == null ? newProduct.getId() : product.getId());

        return newProduct;
    }

    @Override
    public void print(Product ignore) {
        System.out.println();
        System.out.println("\t\t\tАртикул\t\t\t|\t\tТорговая марка\t|\t\tНаименование\t|\t\tКатегория\t|\t\tЦена");
        productService.findAll().forEach(value -> System.out.println("-".repeat(120) + "\n"
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
