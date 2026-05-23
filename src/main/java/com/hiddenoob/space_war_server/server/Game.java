package com.hiddenoob.space_war_server.server;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import com.hiddenoob.space_war_server.packets.PacketMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;

import com.hiddenoob.Math.Vector2;

import com.hiddenoob.space_war_server.gameObjects.Astreoid;
import com.hiddenoob.space_war_server.gameObjects.Map;
import com.hiddenoob.space_war_server.gameObjects.Player;



@Component
public class Game implements SmartLifecycle {
    
    private static final Logger logger = LoggerFactory.getLogger(Game.class);


    private final long tickRate = 1000 / 1; // 64 tick per second
    private volatile boolean isRunning = false;

    private final Map gameMap;
    private final HashSet<Player> players = new HashSet<>();
    private final ApplicationEventPublisher eventPublisher;

    Game(Map map, ApplicationEventPublisher eventPublisher){
        this.gameMap = map;
        this.eventPublisher = eventPublisher;
    }


    public void startLoop() {
        while (this.isRunning && !Thread.currentThread().isInterrupted()) {
            long startTime = System.currentTimeMillis();

            players.forEach((player) -> {
                Vector2 pos = player.getPosition();
                List<Astreoid> nearAstreoids = gameMap.queryRange(pos.x - 20,pos.x + 20,pos.y - 20,pos.y + 20);
                var polygons = nearAstreoids.stream().map((Astreoid::getShape)).toList();
                try {
                    var buffer = PacketMapper.toPolygonPacketList(polygons).toArray();
                    logger.info(buffer.length + " byte is sending to player: " + player.getSession().getId());
                    player.getSession().sendMessage(new BinaryMessage(buffer));
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    protected Map getGameMap() {
        return gameMap;
    }

    protected void addPlayer(Player p){
        gameMap.addObject(p);
        players.add(p);
    }

    protected void removePlayer(Player p){
        gameMap.removeObject(p);
        players.remove(p);
    }
    
}