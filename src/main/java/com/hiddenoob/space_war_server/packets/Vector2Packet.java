package com.hiddenoob.space_war_server.packets;

import java.nio.ByteBuffer;

public class Vector2Packet extends Packet {

    public static Vector2Packet decode(ByteBuffer buffer) {
        buffer.getInt(); // bodySize
        float x = buffer.getFloat();
        float y = buffer.getFloat();
        return new Vector2Packet(x, y);
    }

    public static Vector2Packet decodeBody(ByteBuffer buffer) {
        float x = buffer.getFloat();
        float y = buffer.getFloat();
        return new Vector2Packet(x, y);
    }
    private final float x;

    // ── Decode ───────────────────────────────────────────────────────────────
    private final float y;

    protected Vector2Packet(float x, float y) {
        this.x = x;
        this.y = y;
    }

    // ── Encode ───────────────────────────────────────────────────────────────

    @Override
    public PacketType getPacketType() {
        return PacketType.VECTOR2;
    }

    @Override
    public int getBodySize() {
        return Float.BYTES * 2;
    } // 8 byte

    @Override
    public void writeToPacketBody(ByteBuffer buffer) {
        buffer.putFloat(x);
        buffer.putFloat(y);
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}