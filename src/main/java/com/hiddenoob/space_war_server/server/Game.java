package com.hiddenoob.space_war_server.server;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;

import com.hiddenoob.Math.Vector2;
import com.hiddenoob.space_war_server.GameObjects.Astreoid;
import com.hiddenoob.space_war_server.mapper.PacketMapper;
import com.hiddenoob.space_war_server.websocket.GameWebSocketHandler;


@Component
public class Game implements SmartLifecycle {
    
    private static final Logger logger = LoggerFactory.getLogger(Game.class);


    private long tickRate = 1000 / 1; // 64 tick per second
    private volatile boolean isRunning = false; 
    private Map gameMap;
    private GameWebSocketHandler socketHandler;


    Game(Map map,GameWebSocketHandler socketHandler){
        this.gameMap = map;
        this.socketHandler = socketHandler;
    }


    public void startLoop() {
        while (this.isRunning && !Thread.currentThread().isInterrupted()) {
            long startTime = System.currentTimeMillis();


            // test için.
            socketHandler.getSessions().forEach((id, player) -> {
                Vector2 pos = player.getPosition();
                List<Astreoid> nearAstreoids = gameMap.queryRange(pos.x - 20,pos.x + 20,pos.y - 20,pos.y + 20);
                nearAstreoids.forEach((entity -> 
                    {
                        try {
                            player.getSession().sendMessage(
                                new BinaryMessage(PacketMapper.toPacket(entity.getShape()).toByteArray())
                            );
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                ));
            });

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