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
    protected int getBodySize() {
        // Düzeltme: Sadece Vector2Packet'ların body boyutlarını toplar, header'larını değil.
        return a.getBodySize() + b.getBodySize();
    }

    @Override
    public void writeToPacketBody(ByteBuffer buffer) {
        // Düzeltme: Sadece Vector2Packet'ların body'lerini yazar, header'larını değil.
        a.writeToPacketBody(buffer);
        b.writeToPacketBody(buffer);
    }

    public Vector2Packet getA() { return a; }
    public Vector2Packet getB() { return b; }
}