package com.crsgroup.paymentartifact.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "payments", schema = "paymentschema")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long bookingId;
    // private Double amount;

    @Column(nullable = false)
    private BigDecimal amount;
    
    private String status; // authorized, captured, failed

    @Column(updatable = false, insertable = false, columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime createdAt;
}
