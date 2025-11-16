package org.example.productcatalog.entity;

public class Product {
    private long id;
    private String item;
    private String brand;
    private String title;
    private String category;
    private Double price;

    public Product(String item,
                   String brand,
                   String title,
                   String category,
                   Double price) {
        this.item = item;
        this.brand = brand;
        this.title = title;
        this.category = category;
        this.price = price;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) { this.id = id; }

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

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
