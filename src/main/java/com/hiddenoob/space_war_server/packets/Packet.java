package com.hiddenoob.space_war_server.packets;

import java.nio.ByteBuffer;

public abstract class Packet {

    public abstract PacketType getPacketType();

    protected abstract int getBodySize();

    protected abstract void writeToPacketBody(ByteBuffer buffer);

    protected int getHeaderSize() {
        return 5; // 1 byte PacketType + 4 byte BodySize
    }

    public int getPacketSize() {
        return getHeaderSize() + getBodySize();
    }

    public byte[] toArray() {
        ByteBuffer buffer = ByteBuffer.allocate(getPacketSize());
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