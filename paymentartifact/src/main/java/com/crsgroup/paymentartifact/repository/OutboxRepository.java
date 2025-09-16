package com.crsgroup.paymentartifact.repository;

import com.crsgroup.paymentartifact.model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {}
