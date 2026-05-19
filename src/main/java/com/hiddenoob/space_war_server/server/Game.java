package com.hiddenoob.space_war_server.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import com.hiddenoob.space_war_server.websocket.GameWebSocketHandler;


@Component
public class Game implements SmartLifecycle {
    
    private static final Logger logger = LoggerFactory.getLogger(Game.class);
    private final GameWebSocketHandler gameWebSocketHandler;
    private long tickRate = 1000 / 1; // 64 tick per second
    private volatile boolean isRunning = false; 
    public Game(GameWebSocketHandler gameWebSocketHandler) {
        this.gameWebSocketHandler = gameWebSocketHandler;
    }

    public void startLoop() {
        while (this.isRunning && !Thread.currentThread().isInterrupted()) {
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

    @Override
    public void start() {
        logger.info("Starting game loop");
        this.isRunning = true;
        
        Thread gameThread = new Thread(this::startLoop, "game-loop-thread");
        gameThread.start();
    }

    @Override
    public void stop() {
        logger.info("Stopping game loop");
        this.isRunning = false;
    }

    @Override
    public boolean isRunning() {
        return this.isRunning;
    }

    @Override
    public int getPhase() {
        return 0; // lowest priority
    }
}