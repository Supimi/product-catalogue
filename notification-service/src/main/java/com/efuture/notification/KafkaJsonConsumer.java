package com.efuture.notification;

import com.efuture.notification.dto.ProductCreationEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class KafkaJsonConsumer {
    @KafkaListener(topics = "product-topic", groupId = "group_id")
    public void listen(ConsumerRecord<String, ProductCreationEvent> record) {
        //consume product creation notifications and send notifications to relevant parties
        log.info("Received Card Event: {}", record.value());
    }
}
