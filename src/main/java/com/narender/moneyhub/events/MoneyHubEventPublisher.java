package com.narender.moneyhub.events;

public interface MoneyHubEventPublisher {
    void publish(MoneyHubEvent event);
}
