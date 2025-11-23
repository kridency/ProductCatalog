package org.example.productcatalog.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Product {
    private long id;
    private String item;
    private String brand;
    private String title;
    private String category;
    private double price;

    public Product(String item,
                   String brand,
                   String title,
                   String category,
                   double price) {
        this.item = item;
        this.brand = brand;
        this.title = title;
        this.category = category;
        this.price = price;
    }

}
