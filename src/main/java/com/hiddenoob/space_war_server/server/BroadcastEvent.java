package com.hiddenoob.space_war_server.server;

import com.hiddenoob.space_war_server.packets.Packet;

import java.time.Instant;

public class BroadcastEvent {
    private final Packet packet;
    private final Instant timestamp = Instant.now();

    public BroadcastEvent(Packet packet) {
        this.packet = packet;
    }

    public Packet getPayload() {
        return packet;
    }
}