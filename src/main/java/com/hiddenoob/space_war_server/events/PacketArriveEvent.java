package com.hiddenoob.space_war_server.events;

import com.hiddenoob.space_war_server.gameObjects.Player;
import com.hiddenoob.space_war_server.packets.Packet;

import java.time.Instant;

public class PacketArriveEvent {
    private final Packet packet;
    private final Instant timestamp = Instant.now();

    public PacketArriveEvent(Player from, Packet packet) {
        this.packet = packet;
    }

    public Packet getPayload() {
        return packet;
    }
}