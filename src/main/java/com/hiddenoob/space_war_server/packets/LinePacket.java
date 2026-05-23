package com.hiddenoob.space_war_server.packets;

import java.nio.ByteBuffer;

public class LinePacket extends Packet {
    private final Vector2Packet a;
    private final Vector2Packet b;

    protected LinePacket(Vector2Packet a, Vector2Packet b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public PacketType getPacketType() { return PacketType.LINE; }

    @Override
    protected int getBodySize() { return a.getPacketSize() + b.getPacketSize(); }

    @Override
    public void writeToPacketBody(ByteBuffer buffer) {
        a.exportPacketToBuffer(buffer);
        b.exportPacketToBuffer(buffer);
    }

    public Vector2Packet getA() { return a; }
    public Vector2Packet getB() { return b; }
}