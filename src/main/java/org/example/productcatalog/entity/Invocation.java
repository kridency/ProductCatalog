package org.example.productcatalog.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
public class Invocation {
    private long id;
    private Instant date;
    private String endpoint;
    private User user;

    public Invocation(String endpoint, User user) {
        this.date = Instant.now();
        this.endpoint = endpoint;
        this.user = user;
    }

}
