package com.hiddenoob.space_war_server.packets;

import java.nio.ByteBuffer;

public class PolygonPacket extends Packet {

    public static PolygonPacket decode(ByteBuffer buffer) {
        buffer.getInt(); // bodySize
        ListPacket lines = (ListPacket) PacketMapper.fromBuffer(buffer);
        return new PolygonPacket(lines);
    }
    private final ListPacket lines;

    // ── Decode ───────────────────────────────────────────────────────────────

    protected PolygonPacket(ListPacket lines) {
        this.lines = lines;
    }

    // ── Encode ───────────────────────────────────────────────────────────────

    @Override
    public PacketType getPacketType() {
        return PacketType.POLYGON;
    }

    @Override
    public int getBodySize() {
        return lines.getPacketSize();
    }

    @Override
    protected void writeToPacketBody(ByteBuffer buffer) {
        lines.exportPacketToBuffer(buffer);
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public ListPacket getLines() {
        return lines;
    }
}