package com.hiddenoob.space_war_server.server;

import com.hiddenoob.Math.Lines.BreakableLine;
import com.hiddenoob.Math.Polygons.Polygon;
import com.hiddenoob.Math.Vector2;
import com.hiddenoob.space_war_server.events.SendMessageEvent;
import com.hiddenoob.space_war_server.gameObjects.Map;
import com.hiddenoob.space_war_server.gameObjects.Player;
import com.hiddenoob.space_war_server.packets.PacketMapper;
import com.hiddenoob.space_war_server.packets.PlayerStatePacket;
import com.hiddenoob.space_war_server.packets.WorldStatePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;


@Component
public class Game implements SmartLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(Game.class);

    private final Map gameMap;
    private final HashSet<Player> players = new HashSet<>();
    private final ApplicationEventPublisher eventPublisher;
    private final Random random = new Random();

    @Value("${app.search_range}")
    private int SEARCH_RANGE = 100;

    @Value("${app.tick_rate}")
    private long TICK_RATE = 64;

    private volatile boolean isRunning = false;

    Game(Map map, ApplicationEventPublisher eventPublisher) {
        this.gameMap = map;
        this.eventPublisher = eventPublisher;
    }


    public void startLoop() {
        while (this.isRunning && !Thread.currentThread().isInterrupted()) {
            long startTime = System.currentTimeMillis();

            players.forEach((player) -> {
                double oldX = player.getPosition().x;
                double oldY = player.getPosition().y;
                player.getPhysics().update(TICK_RATE / 1000.0);
                gameMap.updateObjectPosition(player, oldX, oldY);

                Vector2 playerPos = player.getPosition();
                List<Polygon<BreakableLine>> nearAstreoids = new ArrayList<>();
                gameMap.forEachInRange(playerPos.x - SEARCH_RANGE,
                        playerPos.x + SEARCH_RANGE, playerPos.y - SEARCH_RANGE,
                        playerPos.y + SEARCH_RANGE,
                        asteroid -> {
                            if (asteroid != player) {
                                nearAstreoids.add(asteroid.getActualPolygon());
                            }
                        });

                eventPublisher.publishEvent(new SendMessageEvent(this,
                        new WorldStatePacket(
                                new PlayerStatePacket(
                                        PacketMapper.toPacket(player.getPosition()),
                                        (float) player.getPhysics().getRotation(),
                                        PacketMapper.toPacket(player.getActualPolygon())
                                ),
                                PacketMapper.toPolygonPacketList(nearAstreoids)
                        ).toArray(),
                        player
                ));
            });

            long elapsed = System.currentTimeMillis() - startTime;
            long sleepTime = 1000 / TICK_RATE - elapsed;

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

        p.getPhysics().setPosition(new Vector2(deltaX + 500, deltaY + 500));

        gameMap.addObject(p);
        players.add(p);
    }

    protected void removePlayer(Player p) {
        gameMap.removeObject(p);
        players.remove(p);
    }
}
