package com.crsgroup.availableartifact.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "slots", schema = "slotschema")
public class Slot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // since it's SERIAL in DB
    private Long id;

    @Column(name = "centre_id", nullable = false, length = 20)
    private String centreId;

    @Column(name = "service_type", nullable = false, length = 50)
    private String serviceType;

    @Column(name = "slot_time", nullable = false)
    private LocalDateTime slotTime;

    @Column(name = "status", length = 20)
    private String status = "available"; // default value

    @Column(name = "created_at", updatable = false)
    @org.hibernate.annotations.CreationTimestamp
    private LocalDateTime createdAt;
}

