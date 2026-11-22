package org.example.productcatalog.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product", uniqueConstraints = { @UniqueConstraint(name = "uc_product", columnNames = { "item" }) })
public class Product {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    @SequenceGenerator(sequenceName = "id_sequence", allocationSize = 1)
    private Long id;
    private String item;
    private String brand;
    private String title;
    private String category;
    private Double price;
}
