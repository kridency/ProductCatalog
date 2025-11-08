package org.example.productcatalog.entity;

import java.util.UUID;

public class Product {
    private UUID id;
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

        this.id = UUID.randomUUID();
        this.item = item;
        this.brand = brand;
        this.title = title;
        this.category = category;
        this.price = price;

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getCategory() {
        return category;
    }

    public void setCategory(String description) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
