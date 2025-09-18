package com.crsgroup.notificationartifact.consumer;

import com.crsgroup.notificationartifact.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;

    // listen to both topics in one listener (or create two methods)
    @KafkaListener(topics = {"crsdbserver.paymentschema.outbox_events", "crsdbserver.bookingschema.outbox_events"},
                   groupId = "notification-service")
    public void consume(ConsumerRecord<Object, Object> record) {
        try {
            Object value = record.value();
            if (value == null) {
                log.warn("Received null payload on topic={} offset={}", record.topic(), record.offset());
                return;
            }

            // If value is Avro GenericRecord, toString() yields a JSON-like text which we can parse.
            final String raw = value.toString();

            log.info("📩 Raw Event Received (topic={}, partition={}, offset={}): {}", record.topic(), record.partition(), record.offset(), raw);

            // pass raw JSON text to service
            notificationService.processEvent(raw);

        } catch (Exception e) {
            log.error("❌ Error while handling incoming record: ", e);
            // Let the container error handling decide what to do (retries etc.)
            throw new RuntimeException(e);
        }
    }
}
