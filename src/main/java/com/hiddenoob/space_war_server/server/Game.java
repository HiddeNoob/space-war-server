package com.hiddenoob.space_war_server.server;

import com.hiddenoob.Math.Lines.BreakableLine;
import com.hiddenoob.Math.Polygons.Polygon;
import com.hiddenoob.Math.Vector2;
import com.hiddenoob.space_war_server.events.SendMessageEvent;
import com.hiddenoob.space_war_server.gameObjects.Map;
import com.hiddenoob.space_war_server.gameObjects.Player;
import com.hiddenoob.space_war_server.packets.PacketMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random; // Import Random


@Component
public class Game implements SmartLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(Game.class);

    private final long tickRate = 1000 / 1; // 64 tick per second
    private final Map gameMap;

    // TODO concurrent collision olur ConcurrentHashMap yap
    private final HashSet<Player> players = new HashSet<>();
    private final ApplicationEventPublisher eventPublisher;
    private final Random random = new Random(); // Random nesnesi ekle
    private volatile boolean isRunning = false;

    Game(Map map, ApplicationEventPublisher eventPublisher) {
        this.gameMap = map;
        this.eventPublisher = eventPublisher;
    }


    public void startLoop() {
        while (this.isRunning && !Thread.currentThread().isInterrupted()) {
            long startTime = System.currentTimeMillis();

            players.forEach((player) -> {
                Vector2 playerPos = player.getPosition();
                double playerRotation = player.getPhysics().getRotation();
                final int range = 20;
                List<Polygon<BreakableLine>> nearAstreoids = new ArrayList<>();
                gameMap.forEachInRange(playerPos.x - range,
                        playerPos.x + range, playerPos.y - range,
                        playerPos.y + range,
                        asteroid -> {
                            nearAstreoids.add(asteroid.getActualPolygon());
                        });


                eventPublisher.publishEvent(new SendMessageEvent(this,
                        PacketMapper.toPolygonPacketList(nearAstreoids).toArray(),
                        player
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

    protected Map getGameMap() {
        return gameMap;
    }

    protected void addPlayer(Player p) {

        float deltaX = (random.nextFloat() * 100) - 50;
        float deltaY = (random.nextFloat() * 100) - 50;


        p.getPhysics().setPosition(new Vector2(deltaX, deltaY));

        gameMap.addObject(p);
        players.add(p);
    }

    protected void removePlayer(Player p) {
        gameMap.removeObject(p);
        players.remove(p);
    }

}