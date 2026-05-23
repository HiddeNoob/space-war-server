package com.hiddenoob.space_war_server.server;

import com.hiddenoob.space_war_server.packets.PacketMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.hiddenoob.space_war_server.gameObjects.Player;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameWebSocketHandler extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(GameWebSocketHandler.class);
    private final Map<String, Player> sessions = new ConcurrentHashMap<>();
    private final Game game;

    GameWebSocketHandler(Game game){
        this.game = game;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        WebSocketSession safeSession = new ConcurrentWebSocketSessionDecorator(
            session, 1000, 64 * 1024
        );
        Player newPlayer = new Player(safeSession);
        
        sessions.put(session.getId(), newPlayer);
        game.addPlayer(newPlayer);
        


        logger.info("{} connected as {}",session.getRemoteAddress(),session.getId());

        broadcastMessage(
                PacketMapper.toPacket("Server",session.getId() + " connected").toArray()
        );
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        game.removePlayer(sessions.get(session.getId()));
        sessions.remove(session.getId());
        logger.info("{} left from server",session.getId());
        broadcastMessage(
                PacketMapper.toPacket("Server",session.getId() + " left").toArray()
        );
    }

    @EventListener
    public void handleGameBroadcast(BroadcastEvent event) {
        broadcastMessage(event.getPayload().toArray());
    }

    public void broadcastMessage(byte[] message) {
        for (Player p : sessions.values()) {
            try {
                p.getSession().sendMessage(new BinaryMessage(message));
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }

    public Map<String, Player> getSessions() {
        return Collections.unmodifiableMap(this.sessions);
    }

}