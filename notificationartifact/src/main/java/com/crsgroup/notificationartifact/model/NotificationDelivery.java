package com.crsgroup.notificationartifact.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "notification_deliveries", schema = "notificationschema")
public class NotificationDelivery {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String eventId;

    private String notificationType;
    private String recipient;
    private String status;
    private Integer attempts;

    private LocalDateTime lastAttempt;
    private LocalDateTime deliveredAt;

    @Column(columnDefinition = "text")
    private String errorMessage;

    @Column(updatable = false, insertable = false,
            columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime createdAt;
}

