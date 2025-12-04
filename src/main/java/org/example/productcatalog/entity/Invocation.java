package org.example.productcatalog.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;

@Setter
@Getter
@Entity
@Table(schema = "custom", name = "invocation")
public class Invocation {
    @Id
    private long id;
    private Instant date = Instant.now();
    private String endpoint;
    private String email;
}
