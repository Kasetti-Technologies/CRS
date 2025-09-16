package com.crsgroup.availableartifact.controller;

import com.crsgroup.availableartifact.service.SlotService;
import com.crsgroup.availableartifact.model.Slot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/slots")
@Slf4j
public class SlotController {

    @Autowired
    private SlotService slotService;

    /**
     * Create a new slot
     */
    @PostMapping("/save")
    public Slot createSlot(@RequestBody Slot slot) {
        log.info("Adding new slot for centreId={}, serviceType={}, slotTime={}",
                slot.getCentreId(), slot.getServiceType(), slot.getSlotTime());
        return slotService.addSlot(slot);
    }

    /**
     * Get available slots for a centre and service type
     */
    @GetMapping("/availability")
    public List<Slot> getAvailability(
            @RequestParam String centreId,
            @RequestParam String serviceType) {
        log.info("Fetching available slots for centreId={}, serviceType={}", centreId, serviceType);
        List<Slot> availableSlots = slotService.getSlots(centreId, serviceType, "available");
        log.info("Found {} available slots for centreId={}, serviceType={}", availableSlots.size(), centreId, serviceType);
        return availableSlots;
    }

    /**
     * Update a slot (e.g., mark as booked or cancelled)
     */
    @PutMapping("/update")
    public Slot updateSlot(@RequestBody Slot slot) {
        log.info("Updating slot with id={}, new status={}", slot.getId(), slot.getStatus());
        return slotService.updateSlot(slot);
    }
}

