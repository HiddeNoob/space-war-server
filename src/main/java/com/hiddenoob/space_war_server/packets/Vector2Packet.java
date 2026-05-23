package com.hiddenoob.space_war_server.packets;

import java.nio.ByteBuffer;

public class Vector2Packet extends Packet {
    private final float x;
    private final float y;

    protected Vector2Packet(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public PacketType getPacketType() { return PacketType.VECTOR2; }

    @Override
    public int getBodySize() { return (Float.SIZE / Byte.SIZE) * 2; } // 8 byte

    @Override
    public void writeToPacketBody(ByteBuffer buffer) {
        buffer.putFloat(x);
        buffer.putFloat(y);
    }

    public float getX() { return x; }
    public float getY() { return y; }
}