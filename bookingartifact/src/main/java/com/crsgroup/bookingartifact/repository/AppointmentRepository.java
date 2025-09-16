package com.crsgroup.bookingartifact.repository;

import com.crsgroup.bookingartifact.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    boolean existsBySlotIdAndStatus(Long slotId, String status);
}
