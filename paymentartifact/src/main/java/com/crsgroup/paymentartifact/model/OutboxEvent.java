package com.crsgroup.paymentartifact.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "outbox_events", schema = "paymentschema")
public class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aggregateType;
    private String aggregateId;
    private String type;

    @Column(columnDefinition = "text")
    private String payload;

    @Column(updatable = false, insertable = false, columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime createdAt;
}
