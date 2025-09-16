package com.crsgroup.bookingartifact.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "appointments", schema = "bookingschema")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long slotId;
    private String patientId;
    private String status = "booked";

    @Column(updatable = false, insertable = false, columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime createdAt;
}
