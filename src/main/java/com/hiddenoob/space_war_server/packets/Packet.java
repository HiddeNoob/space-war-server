package com.hiddenoob.space_war_server.packets;

import java.nio.ByteBuffer;

public interface Packet {
    PacketType getPacketType();
    int getByteSize();
    void serializeTo(ByteBuffer buffer);
    
    default byte[] toByteArray() {
        int totalSize = 1 + 4 + getByteSize();
        ByteBuffer buffer = ByteBuffer.allocate(totalSize);
        
        buffer.put(getPacketType().getId()); // 1 byte
        buffer.putInt(getByteSize());        // 4 byte
        serializeTo(buffer);                 // ? byte
        
        return buffer.array();
    }
}