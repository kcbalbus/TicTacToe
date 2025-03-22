package com.example.pwctictactoebackend.utils;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionSubscriptionMap {
    private final Map<String, Set<String>> sessionSubscriptions = new ConcurrentHashMap<>();

    public void addSubscription(String sessionId, String destination) {
        sessionSubscriptions.computeIfAbsent(sessionId, k -> ConcurrentHashMap.newKeySet()).add(destination);
    }

    public void removeSubscription(String sessionId, String destination) {
        Set<String> subscriptions = sessionSubscriptions.getOrDefault(sessionId, Collections.emptySet());
        subscriptions.remove(destination);
        if (subscriptions.isEmpty()) {
            sessionSubscriptions.remove(sessionId);
        }
    }

    public Set<String> getSubscriptions(String sessionId) {
        return sessionSubscriptions.getOrDefault(sessionId, Collections.emptySet());
    }
}

