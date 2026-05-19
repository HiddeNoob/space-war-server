package com.hiddenoob.space_war_server.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.hiddenoob.space_war_server.GameObjects.Player;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameWebSocketHandler extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(GameWebSocketHandler.class);
    private final Map<String, Player> sessions = new ConcurrentHashMap<>();
    private final com.hiddenoob.space_war_server.server.Map gameMap;

    GameWebSocketHandler(com.hiddenoob.space_war_server.server.Map gameMap){
        this.gameMap = gameMap;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        WebSocketSession safeSession = new ConcurrentWebSocketSessionDecorator(
            session, 1000, 64 * 1024
        );
        Player newPlayer = new Player(safeSession);
        sessions.put(session.getId(), newPlayer);
        gameMap.addObject(newPlayer);
        safeSession.sendMessage(new TextMessage("Welcome!"));
        logger.info("{} connected as {}",session.getRemoteAddress(),session.getId());
        broadcastMessage(session.getId() + " joined the game");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId()); 
        logger.info("{} left from server",session.getId());
    }

    @Async
    public void broadcastMessage(String message) {
        TextMessage textMessage = new TextMessage(message);
        for (Player p : sessions.values()) {
            try {
                p.getSession().sendMessage(textMessage);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }

    public Map<String, Player> getSessions() {
        return Collections.unmodifiableMap(this.sessions);
    }

}