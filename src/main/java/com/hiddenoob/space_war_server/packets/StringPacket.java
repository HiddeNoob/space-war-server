package com.hiddenoob.space_war_server.packets;

import java.nio.ByteBuffer;

public class StringPacket extends Packet {
    private final String data;

    protected StringPacket(String data) {
        this.data = data;
    }
    
    public PacketType getPacketType() {
        return PacketType.STRING;
    }

    public String getData() {
        return data;
    }

    @Override
    protected int getBodySize() {
        return data.getBytes().length;
    }

    @Override
    protected void writeToPacketBody(ByteBuffer buffer) {

        buffer.put(data.getBytes());

    }
    
}
