package com.crsgroup.availableartifact.service;

import com.crsgroup.availableartifact.model.Slot;
import com.crsgroup.availableartifact.repository.SlotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SlotServiceTest {

    @Mock
    private SlotRepository slotRepository;

    @InjectMocks
    private SlotService slotService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetSlots_found() {
        Slot slot = new Slot();
        slot.setId(1L);
        slot.setCentreId("DC800");
        slot.setServiceType("MRI");
        slot.setSlotTime(LocalDateTime.now());
        slot.setStatus("available");

        when(slotRepository.findByCentreIdAndServiceTypeAndStatus("DC800", "MRI", "available"))
                .thenReturn(List.of(slot));

        List<Slot> result = slotService.getSlots("DC800", "MRI", "available");

        assertEquals(1, result.size());
        assertEquals("MRI", result.get(0).getServiceType());
    }

    @Test
    void testGetSlots_notFound() {
        when(slotRepository.findByCentreIdAndServiceTypeAndStatus("DC900", "CT", "available"))
                .thenReturn(Collections.emptyList());

        List<Slot> result = slotService.getSlots("DC900", "CT", "available");

        assertTrue(result.isEmpty());
    }

    @Test
    void testAddSlot() {
        Slot slot = new Slot();
        slot.setCentreId("DC777");
        slot.setServiceType("XRAY");
        slot.setSlotTime(LocalDateTime.now());

        when(slotRepository.save(any(Slot.class))).thenAnswer(i -> {
            Slot saved = i.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        Slot savedSlot = slotService.addSlot(slot);

        assertNotNull(savedSlot.getId());
        assertEquals("DC777", savedSlot.getCentreId());
    }

    @Test
    void testUpdateSlot() {
        Slot slot = new Slot();
        slot.setId(5L);
        slot.setCentreId("DC888");
        slot.setServiceType("MRI");
        slot.setStatus("booked");

        when(slotRepository.save(slot)).thenReturn(slot);

        Slot updated = slotService.updateSlot(slot);

        assertEquals("booked", updated.getStatus());
        verify(slotRepository, times(1)).save(slot);
    }
}
