package com.hiddenoob.space_war_server.packets;

import java.nio.ByteBuffer;

public class LinePacket extends Packet {

    private final Vector2Packet a;
    // ── Decode ───────────────────────────────────────────────────────────────
    private final Vector2Packet b;

    protected LinePacket(Vector2Packet a, Vector2Packet b) {
        this.a = a;
        this.b = b;
    }

    public static LinePacket decode(ByteBuffer buffer) {
        buffer.getInt(); // bodySize
        Vector2Packet a = Vector2Packet.decodeBody(buffer);
        Vector2Packet b = Vector2Packet.decodeBody(buffer);
        return new LinePacket(a, b);
    }

    public static LinePacket decodeBody(ByteBuffer buffer) {
        Vector2Packet a = Vector2Packet.decodeBody(buffer);
        Vector2Packet b = Vector2Packet.decodeBody(buffer);
        return new LinePacket(a, b);
    }

    // ── Encode ───────────────────────────────────────────────────────────────

    @Override
    public PacketType getPacketType() {
        return PacketType.LINE;
    }

    @Override
    protected int getBodySize() {
        return a.getBodySize() + b.getBodySize(); // 16 byte
    }

    @Override
    public void writeToPacketBody(ByteBuffer buffer) {
        // Sadece body yazılır — Vector2 header'ları dahil değil
        a.writeToPacketBody(buffer);
        b.writeToPacketBody(buffer);
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public Vector2Packet getA() {
        return a;
    }

    public Vector2Packet getB() {
        return b;
    }
}