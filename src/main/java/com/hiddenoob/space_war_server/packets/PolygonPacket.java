package com.hiddenoob.space_war_server.packets;

import java.nio.ByteBuffer;

public class PolygonPacket extends Packet {
    private final ListPacket lines;

    protected PolygonPacket(ListPacket lines) {
        this.lines = lines;
    }

    @Override
    public PacketType getPacketType() { return PacketType.POLYGON; }

    @Override
    public int getBodySize() {

        return lines.getPacketSize();
    }

    @Override
    protected void writeToPacketBody(ByteBuffer buffer) {
        lines.exportPacketToBuffer(buffer);
    }

    public ListPacket getLines() { return lines; }
}