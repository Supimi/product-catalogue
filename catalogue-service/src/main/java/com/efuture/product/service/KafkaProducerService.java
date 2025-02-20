package com.efuture.product.service;

import com.efuture.product.dto.ProductCreationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

import static com.efuture.product.util.Constants.CARD_EVENT_TOPIC;

@Slf4j
@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, ProductCreationEvent> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, ProductCreationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(ProductCreationEvent event) {
        log.info("send product event: {}", event);
        CompletableFuture<SendResult<String, ProductCreationEvent>> future = kafkaTemplate.send(CARD_EVENT_TOPIC, event);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent with offset: {}", result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send message", ex);
            }
        });
    }
}
