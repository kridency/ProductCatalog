package org.example.productcatalog.entity;

import jakarta.persistence.*;

import java.time.Instant;

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

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
