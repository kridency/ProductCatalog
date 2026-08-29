package org.example.productcatalog.entity;

import jakarta.persistence.*;
import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    @SequenceGenerator(sequenceName = "id_sequence", allocationSize = 1)
    private Long id;
    @Column(name = "item", unique = true)
    private String item;
    private String brand;
    private String title;
    private String category;
    private Double price;
}
