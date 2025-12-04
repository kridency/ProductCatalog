package org.example.productcatalog.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Setter;
import lombok.Getter;

@Setter
@Getter
@Entity
@Table(schema = "custom", name = "product")
public class Product {
    @Id
    private long id;
    private String item;
    private String brand;
    private String title;
    private String category;
    private double price;
}
