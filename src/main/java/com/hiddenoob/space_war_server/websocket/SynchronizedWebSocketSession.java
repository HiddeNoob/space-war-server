package com.hiddenoob.space_war_server.websocket;


import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

import org.springframework.web.socket.TextMessage;

public class SynchronizedWebSocketSession {

    private final WebSocketSession session;

    public SynchronizedWebSocketSession(WebSocketSession session) {
        this.session = session;
    }

    public synchronized void send(String message) {
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public String getId() { return session.getId(); }
    public boolean isOpen() { return session.isOpen(); }
}