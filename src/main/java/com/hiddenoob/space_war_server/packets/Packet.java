package com.hiddenoob.space_war_server.packets;

import java.nio.ByteBuffer;

public abstract class Packet {
    public abstract PacketType getPacketType();
    protected abstract int getBodySize();

    // Verilen buffer'i body'ye yazar 
    protected abstract void writeToPacketBody(ByteBuffer buffer);

    protected  int getHeaderSize() {
        return 5; // 1 byte for packet type, 4 bytes for size
    }
    public int getPacketSize() {
        return getHeaderSize() + getBodySize(); // 1 byte for packet type, 4 bytes for size, rest for data
    }

    public byte[] toArray() {
        int totalSize = getHeaderSize() + getBodySize();
        ByteBuffer buffer = ByteBuffer.allocate(totalSize);
        
        exportPacketToBuffer(buffer);
        
        return buffer.array();
    }

    protected void writeToPacketHeader(ByteBuffer buffer) {
        buffer.put(getPacketType().getId()); // 1 byte
        buffer.putInt(getBodySize());        // 4 byte
    }

    protected void exportPacketToBuffer(ByteBuffer buffer) {
        writeToPacketHeader(buffer);
        writeToPacketBody(buffer);
    }
}