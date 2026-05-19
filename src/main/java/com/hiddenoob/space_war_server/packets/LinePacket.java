package com.hiddenoob.space_war_server.packets;

import java.nio.ByteBuffer;

public class LinePacket implements Packet {
    private final Vector2Packet a;
    private final Vector2Packet b;

    public LinePacket(Vector2Packet a, Vector2Packet b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public PacketType getPacketType() { return PacketType.LINE; }

    @Override
    public int getByteSize() { return a.getByteSize() + b.getByteSize(); } // 16 byte

    @Override
    public void serializeTo(ByteBuffer buffer) {
        a.serializeTo(buffer);
        b.serializeTo(buffer);
    }

    public Vector2Packet getA() { return a; }
    public Vector2Packet getB() { return b; }
}