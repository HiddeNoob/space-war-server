package com.hiddenoob.space_war_server.packets;

import java.nio.ByteBuffer;

public class ListPacket extends Packet {
    
    private final Packet[] data;

    protected ListPacket(Packet[] data) {
        this.data = data;
    }

    @Override
    protected int getBodySize() {
        int totalSize = 0;
        for (Packet item : data) {
            totalSize += item.getPacketSize();
        }
        return totalSize;
    }

    @Override
    protected void writeToPacketBody(ByteBuffer buffer) {
        for (Packet item : data) {
            item.exportPacketToBuffer(buffer);
        }
    }

    public Packet[] getData() {
        return data;
    }

    @Override
    public PacketType getPacketType() { return PacketType.ARRAY; }
    
}
