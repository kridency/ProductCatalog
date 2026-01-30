package org.example.productcatalog.entity;

import jakarta.persistence.*;
import lombok.Setter;

import java.time.Instant;

@Setter
@Entity
@Table(name = "invocation")
public class Invocation {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="sequence_generator")
    @SequenceGenerator(name = "sequence_generator", sequenceName = "id_sequence", allocationSize = 1)
    private Long id;
    private Instant date = Instant.now();
    private String endpoint;
    private String email;
}
