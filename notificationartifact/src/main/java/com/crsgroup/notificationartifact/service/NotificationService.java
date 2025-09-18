package com.crsgroup.notificationartifact.service;

import com.crsgroup.notificationartifact.model.NotificationDelivery;
import com.crsgroup.notificationartifact.repository.NotificationDeliveryRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationDeliveryRepository deliveryRepo;
    private final ObjectMapper mapper;

    // Use a WebClient built once. (you can inject WebClient.Builder if preferred)
    private final WebClient webClient = WebClient.create("https://webhook.site");

    @Transactional
    public void processEvent(String eventData) throws Exception {
        log.info("🔍 processEvent raw payload: {}", eventData == null ? "<null>" : (eventData.length() > 1000 ? eventData.substring(0,1000) + "..." : eventData));

        if (eventData == null || eventData.trim().isEmpty()) {
            log.warn("Empty event data - ignoring");
            return;
        }

        JsonNode root;
        try {
            root = mapper.readTree(eventData);
        } catch (Exception ex) {
            log.error("Failed to parse incoming event JSON", ex);
            throw ex;
        }

        JsonNode after = root.path("after");
        if (after.isMissingNode() || after.isNull()) {
            log.warn("No 'after' node found - ignoring event");
            return;
        }

        // robust extraction with fallbacks
        String eventId = after.path("id").asText(null);
        if (eventId == null || eventId.isEmpty()) {
            // fallback to aggregate_id or a generated id (useful if outbox id missing)
            eventId = after.path("aggregate_id").asText(null);
            if (eventId == null || eventId.isEmpty()) {
                // fallback to a composite id
                String lsn = root.path("source").path("lsn").asText("");
                String ts = root.path("ts_ms").asText("");
                eventId = (lsn.isEmpty() && ts.isEmpty()) ? UUID.randomUUID().toString() : ts + "-" + lsn;
            }
        }

        String type = after.path("type").asText("");
        String payload = after.path("payload").asText("");

        log.info("➡️ eventId='{}' type='{}' payload length={}", eventId, type, payload == null ? 0 : payload.length());

        // idempotency check
        if (deliveryRepo.existsByEventId(eventId)) {
            log.info("⚠️ Event {} already processed, skipping", eventId);
            return;
        }

        String recipient = "demo-user"; // substitute your real recipient logic

        try {
            boolean shouldSend = false;

            // decide what to do for event types
            switch (type.toLowerCase()) {
                case "payment.completed", "payment.failed", "booking.created", "booking.cancelled" -> shouldSend = true;
                case "payment.authorized" -> {
                    log.info("🔔 payment.authorized → logged only, no webhook");
                    shouldSend = false;
                }
                default -> {
                    log.info("ℹ️ Unknown/ignored type='{}' (eventId={})", type, eventId);
                }
            }

            if (shouldSend) {
                // send webhook (synchronous for reliability in demo)
                sendWebhook(type, payload);

                // persist delivery record
                NotificationDelivery delivery = new NotificationDelivery();
                delivery.setEventId(eventId);
                delivery.setNotificationType(type);
                delivery.setRecipient(recipient);
                delivery.setStatus("SENT");
                delivery.setAttempts(1);
                delivery.setLastAttempt(LocalDateTime.now());
                delivery.setDeliveredAt(LocalDateTime.now());
                NotificationDelivery saved = deliveryRepo.save(delivery);
                log.info("✅ NotificationDelivery saved id={} eventId={}", saved.getId(), eventId);
            }
        } catch (Exception ex) {
            log.error("❌ Error processing event {}: {}", eventId, ex.getMessage(), ex);
            NotificationDelivery failed = new NotificationDelivery();
            failed.setEventId(eventId);
            failed.setNotificationType(type);
            failed.setRecipient(recipient);
            failed.setStatus("FAILED");
            failed.setAttempts(1);
            failed.setLastAttempt(LocalDateTime.now());
            failed.setErrorMessage(ex.getMessage());
            deliveryRepo.save(failed);
            throw ex; // rethrow so consumer error handling can act (offsets, retries)
        }
    }

    private void sendWebhook(String type, String payload) {
        // POST to webhook.site path
        webClient.post()
                .uri("/d234e68a-5f25-4847-9913-291243e88546")
                .header("Content-Type", "application/json")
                .bodyValue("{\"event\":\"" + type + "\",\"payload\":" + (payload == null ? "\"\"" : payload) + "}")
                .retrieve()
                .toBodilessEntity()
                .block(); // synchronous to see result in logs
        
        log.info("✅ Webhook sent for type={}", type);
    }
}