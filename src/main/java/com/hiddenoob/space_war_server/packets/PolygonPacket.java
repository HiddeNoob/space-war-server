package com.hiddenoob.space_war_server.packets;

import java.nio.ByteBuffer;
import java.util.List;

public class PolygonPacket implements Packet {
    private final List<LinePacket> lines;

    public PolygonPacket(List<LinePacket> lines) {
        this.lines = lines;
    }

    @Override
    public PacketType getPacketType() { return PacketType.POLYGON; }

    @Override
    public int getByteSize() {
        int linesTotalSize = lines.stream()
                                  .mapToInt(LinePacket::getByteSize)
                                  .sum();
        
        return 4 + linesTotalSize; 
    }

    @Override
    public void serializeTo(ByteBuffer buffer) {
        buffer.putInt(lines.size());
        
        for (LinePacket line : lines) {
            line.serializeTo(buffer);
        }
    }

    public List<LinePacket> getLines() { return lines; }
}