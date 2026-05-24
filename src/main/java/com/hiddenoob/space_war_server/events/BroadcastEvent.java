package com.hiddenoob.space_war_server.events;

import java.time.Instant;

public class BroadcastEvent {
    private final byte[] packet;
    private final Instant timestamp = Instant.now();

    public BroadcastEvent(byte[] packet) {
        this.packet = packet;
    }

    public byte[] getPayload() {
        return packet;
    }
}