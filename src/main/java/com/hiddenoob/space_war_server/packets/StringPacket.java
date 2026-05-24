package com.hiddenoob.space_war_server.packets;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class StringPacket extends Packet {

    private static final Charset CHARSET = StandardCharsets.UTF_8;
    public static StringPacket decode(ByteBuffer buffer) {
        int bodySize = buffer.getInt();
        byte[] bytes = new byte[bodySize];
        buffer.get(bytes);
        return new StringPacket(new String(bytes, CHARSET));
    }
    private final String data;

    // ── Decode ───────────────────────────────────────────────────────────────

    protected StringPacket(String data) {
        this.data = data;
    }

    // ── Encode ───────────────────────────────────────────────────────────────

    @Override
    public PacketType getPacketType() {
        return PacketType.STRING;
    }

    @Override
    protected int getBodySize() {
        return data.getBytes(CHARSET).length;
    }

    @Override
    protected void writeToPacketBody(ByteBuffer buffer) {
        buffer.put(data.getBytes(CHARSET));
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String getData() {
        return data;
    }
}