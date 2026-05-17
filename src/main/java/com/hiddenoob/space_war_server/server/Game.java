package com.hiddenoob.space_war_server.server;

import java.time.Instant;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.hiddenoob.space_war_server.websocket.GameWebSocketHandler;

@Component
public class Game {

    private final GameWebSocketHandler gameWebSocketHandler;
    private long tickRate = 1000 / 1; // 64 tick per second
    public Game(GameWebSocketHandler gameWebSocketHandler) {
        this.gameWebSocketHandler = gameWebSocketHandler;
    }

    @Async
    public void start() {

        while (true) {
            long startTime = System.currentTimeMillis();

            gameWebSocketHandler.broadcastMessage("startTime: " + startTime);

            long elapsed = System.currentTimeMillis() - startTime;
            long sleepTime = tickRate - elapsed;

            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}