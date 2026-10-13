package com.narender.moneyhub.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** Default publisher: logs events. Flip app.events.kafka-enabled to use Kafka. */
@Component
@ConditionalOnProperty(name = "app.events.kafka-enabled", havingValue = "false", matchIfMissing = true)
public class LoggingMoneyHubEventPublisher implements MoneyHubEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(LoggingMoneyHubEventPublisher.class);

    @Override
    public void publish(MoneyHubEvent event) {
        log.info("money-hub-event {}", event);
    }
}
