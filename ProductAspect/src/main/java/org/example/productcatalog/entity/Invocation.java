package org.example.productcatalog.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "invocation")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@RequiredArgsConstructor
public class Invocation {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    @SequenceGenerator(sequenceName = "id_sequence", allocationSize = 1)
    private Long id;
    @CreatedDate
    @Column(name = "date", updatable = false)
    private Instant date;
    @NonNull
    private String endpoint;
    @NonNull
    private String email;
}
