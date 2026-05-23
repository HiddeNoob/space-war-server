package com.hiddenoob.space_war_server.packets;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class StringPacket extends Packet {
    private final String data;
    private static final Charset standartCharSet = StandardCharsets.UTF_8; // sistem dili sorun çıkarmasın
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
        return data.getBytes(standartCharSet).length;
    }

    @Override
    protected void writeToPacketBody(ByteBuffer buffer) {
        buffer.put(data.getBytes());
    }
}
    