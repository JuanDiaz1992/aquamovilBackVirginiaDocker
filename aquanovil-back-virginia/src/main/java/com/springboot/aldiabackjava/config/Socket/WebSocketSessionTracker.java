package com.springboot.aldiabackjava.config.Socket;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionTracker {
    private final Map<String, Set<String>> userSessions = new ConcurrentHashMap<>();

    @EventListener
    public void handleSessionConnectEvent(SessionConnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String user = sha.getUser() != null ? sha.getUser().getName() : null;
        if (user != null) {
            userSessions.putIfAbsent(user, new HashSet<>());
        }
    }

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String user = sha.getUser() != null ? sha.getUser().getName() : null;
        if (user != null) {
            userSessions.computeIfAbsent(user, k -> new HashSet<>()).add(sha.getSessionId());
        }
    }

    @EventListener
    public void handleSessionDisconnectEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String user = sha.getUser() != null ? sha.getUser().getName() : null;
        if (user != null) {
            Set<String> sessions = userSessions.get(user);
            if (sessions != null) {
                sessions.remove(sha.getSessionId());
                if (sessions.isEmpty()) {
                    userSessions.remove(user);
                }
            }
        }
    }

    public boolean isUserConnected(String username) {
        return userSessions.containsKey(username);
    }
}
