package com.crsgroup.bookingartifact.repository;

import com.crsgroup.bookingartifact.model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {}
