package org.example.productcatalog.entity;

import jakarta.persistence.*;
import lombok.Setter;
import lombok.Getter;

@Setter
@Getter
@Entity
@Table(name = "product", schema = "custom")
public class Product {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="sequence_generator")
    @SequenceGenerator(name = "sequence_generator", sequenceName = "id_sequence", allocationSize = 1)
    private Long id;
    private String item;
    private String brand;
    private String title;
    private String category;
    private Double price;
}
