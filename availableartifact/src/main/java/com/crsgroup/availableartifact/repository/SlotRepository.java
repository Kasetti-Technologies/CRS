package com.crsgroup.availableartifact.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crsgroup.availableartifact.model.Slot;

import java.util.List;

public interface SlotRepository extends JpaRepository<Slot, Long> {
    List<Slot> findByCentreIdAndServiceTypeAndStatus(String centreId, String serviceType, String status);
}

