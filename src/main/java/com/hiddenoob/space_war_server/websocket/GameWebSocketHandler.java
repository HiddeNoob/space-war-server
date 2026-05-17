package com.hiddenoob.space_war_server.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, ConcurrentWebSocketSessionDecorator> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        ConcurrentWebSocketSessionDecorator safeSession = new ConcurrentWebSocketSessionDecorator(
            session, 1000, 64 * 1024
        );
        sessions.put(session.getId(), safeSession);
        safeSession.sendMessage(new TextMessage("Welcome!"));
        broadcastMessage(session.getId() + " joined the game");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId()); 
    }

    public void broadcastMessage(String message) {
        TextMessage textMessage = new TextMessage(message);
        for (ConcurrentWebSocketSessionDecorator session : sessions.values()) {
            try {
                session.sendMessage(textMessage);
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }
}