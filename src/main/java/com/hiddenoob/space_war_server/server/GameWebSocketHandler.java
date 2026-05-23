package com.hiddenoob.space_war_server.server;

import com.hiddenoob.space_war_server.events.SendMessageEvent;
import com.hiddenoob.space_war_server.gameObjects.Player;
import com.hiddenoob.space_war_server.packets.PacketMapper;
import com.hiddenoob.space_war_server.utils.CompressionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameWebSocketHandler extends TextWebSocketHandler {
    private static final Logger logger =
            LoggerFactory.getLogger(GameWebSocketHandler.class);
    private final Map<String, Player> sessions = new ConcurrentHashMap<>();
    private final Game game;
    private final ApplicationEventPublisher eventPublisher;

    GameWebSocketHandler(Game game, ApplicationEventPublisher eventPublisher) {
        this.game = game;
        this.eventPublisher = eventPublisher;
    }

    public Map<String, Player> getSessions() {
        return Collections.unmodifiableMap(this.sessions);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        WebSocketSession safeSession = new ConcurrentWebSocketSessionDecorator(
                session, 1000, 64 * 1024
        );
        Player newPlayer = new Player(safeSession);

        sessions.put(session.getId(), newPlayer);
        game.addPlayer(newPlayer);


        logger.info("{} connected as {}", session.getRemoteAddress(),
                session.getId());

        eventPublisher.publishEvent(new SendMessageEvent(this,
                PacketMapper.toPacket("Server", session.getId() + " connected"
                ).toArray(),
                null //  broadcast
        ));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session,
                                      CloseStatus status) {
        game.removePlayer(sessions.get(session.getId()));
        sessions.remove(session.getId());
        logger.info("{} left from server", session.getId());
        eventPublisher.publishEvent(new SendMessageEvent(this,
                PacketMapper.toPacket("Server", session.getId() + " left").toArray(),
                null
        ));
    }

    private void broadcastMessage(byte[] message) {
        for (Player p : sessions.values()) {
            try {
                p.getSession().sendMessage(new BinaryMessage(message));
            } catch (IOException e) {
                logger.error("Error broadcasting message to player {}: {}",
                        p.getSession().getId(), e.getMessage());
            }
        }
    }

    @EventListener
    public void handleSendMessage(SendMessageEvent event) {
        byte[] flaggedMessage = CompressionUtils.compress(event.getMessage());
        Player targetPlayer = event.getTargetPlayer();

        if (targetPlayer != null) {
            // Send to a specific player
            try {
                targetPlayer.getSession().sendMessage(new BinaryMessage(flaggedMessage));
            } catch (IOException e) {
                logger.error("Error sending message to player {}: {}",
                        targetPlayer.getSession().getId(), e.getMessage());
            }
        } else {
            // Broadcast to all players if targetPlayer is null
            broadcastMessage(flaggedMessage);
        }
    }


}