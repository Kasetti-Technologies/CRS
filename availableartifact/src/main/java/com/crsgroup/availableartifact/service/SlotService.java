package com.crsgroup.availableartifact.service;

import com.crsgroup.availableartifact.model.Slot;
import com.crsgroup.availableartifact.repository.SlotRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SlotService {

    @Autowired
    private SlotRepository slotRepository;

    /*
     * Fetch slots by centre, service type, and status.
     */
    public List<Slot> getSlots(String centreId, String serviceType, String status) {
        log.info("Fetching slots for centreId={}, serviceType={}, status={}", centreId, serviceType, status);

        List<Slot> slots = slotRepository.findByCentreIdAndServiceTypeAndStatus(centreId, serviceType, status);

        if (CollectionUtils.isEmpty(slots)) {
            log.info("No slots found for centreId={}, serviceType={}, status={}", centreId, serviceType, status);
            return new ArrayList<>();
        }

        log.info("Found {} slots for centreId={}, serviceType={}, status={}", slots.size(), centreId, serviceType, status);
        return slots;
    }

    /**
     * Add a new slot into the database.
     */
    public Slot addSlot(Slot slot) {
        log.info("Adding slot for centreId={}, serviceType={}, slotTime={}", slot.getCentreId(), slot.getServiceType(), slot.getSlotTime());
        Slot savedSlot = slotRepository.save(slot);
        log.info("Slot with id={} added successfully", savedSlot.getId());
        return savedSlot;
    }

    /**
     * Update an existing slot (e.g., change status).
     */
    public Slot updateSlot(Slot slot) {
        log.info("Updating slot with id={}", slot.getId());
        Slot updatedSlot = slotRepository.save(slot);
        log.info("Slot with id={} updated successfully", updatedSlot.getId());
        return updatedSlot;
    }
   
}

