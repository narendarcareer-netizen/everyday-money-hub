package com.narender.moneyhub.events;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/** Real publisher: sends events to the money-hub Kafka topic. */
@Component
@ConditionalOnProperty(name = "app.events.kafka-enabled", havingValue = "true")
public class KafkaMoneyHubEventPublisher implements MoneyHubEventPublisher {

    private static final String TOPIC = "money-hub-events";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaMoneyHubEventPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(MoneyHubEvent event) {
        kafkaTemplate.send(TOPIC, event.getKey(), event.toString());
    }
}
