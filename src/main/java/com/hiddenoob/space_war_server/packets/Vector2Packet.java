package com.hiddenoob.space_war_server.packets;

import java.nio.ByteBuffer;

public class Vector2Packet implements Packet {
    private final float x;
    private final float y;

    public Vector2Packet(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public PacketType getPacketType() { return PacketType.VECTOR2; }

    @Override
    public int getByteSize() { return (Float.SIZE / Byte.SIZE) * 2; } // 8 byte

    @Override
    public void serializeTo(ByteBuffer buffer) {
        buffer.putFloat(x);
        buffer.putFloat(y);
    }

    // Karşı tarafta paketi okurken içindeki veriye erişmek için
    public float getX() { return x; }
    public float getY() { return y; }
}