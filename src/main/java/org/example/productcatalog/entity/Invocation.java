package org.example.productcatalog.entity;

import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;

@Setter
@Getter
public class Invocation {
    private long id;
    private Instant date;
    private String endpoint;
    private String email;

    public Invocation(String endpoint, String email) {
        this.date = Instant.now();
        this.endpoint = endpoint;
        this.email = email;
    }

}
