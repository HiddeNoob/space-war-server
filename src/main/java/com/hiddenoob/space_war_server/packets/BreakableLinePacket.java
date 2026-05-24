package com.hiddenoob.space_war_server.packets;

import java.nio.ByteBuffer;

public class BreakableLinePacket extends LinePacket {

    public static BreakableLinePacket decode(ByteBuffer buffer) {
        buffer.getInt(); // bodySize
        int health = buffer.getInt();
        Vector2Packet a = Vector2Packet.decodeBody(buffer);
        Vector2Packet b = Vector2Packet.decodeBody(buffer);
        return new BreakableLinePacket(health, a, b);
    }

    public static BreakableLinePacket decodeBody(ByteBuffer buffer) {
        int health = buffer.getInt();
        Vector2Packet a = Vector2Packet.decodeBody(buffer);
        Vector2Packet b = Vector2Packet.decodeBody(buffer);
        return new BreakableLinePacket(health, a, b);
    }

    // ── Decode ───────────────────────────────────────────────────────────────
    private final int health;

    public BreakableLinePacket(int health, Vector2Packet a, Vector2Packet b) {
        super(a, b);
        this.health = health;
    }

    // ── Encode ───────────────────────────────────────────────────────────────

    @Override
    public PacketType getPacketType() {
        return PacketType.BREAKABLE_LINE;
    }

    @Override
    protected int getBodySize() {
        return Integer.BYTES + super.getBodySize(); // 4 + 16 = 20 byte
    }

    @Override
    public void writeToPacketBody(ByteBuffer buffer) {
        buffer.putInt(health);
        super.writeToPacketBody(buffer);
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public int getHealth() {
        return health;
    }
}