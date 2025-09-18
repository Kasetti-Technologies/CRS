package com.crsgroup.notificationartifact.repository;

import com.crsgroup.notificationartifact.model.NotificationDelivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NotificationDeliveryRepository extends JpaRepository<NotificationDelivery, UUID> {
    boolean existsByEventId(String eventId);
    Optional<NotificationDelivery> findByEventId(String eventId);
}