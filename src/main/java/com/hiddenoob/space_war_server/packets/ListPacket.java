package com.hiddenoob.space_war_server.packets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ListPacket extends Packet {

    private final Packet[] data;

    protected ListPacket(Packet[] data) {
        this.data = data;
    }

    public static ListPacket decode(ByteBuffer buffer) {
        int bodySize = buffer.getInt();
        List<Packet> items = new ArrayList<>();
        int read = 0;
        while (read < bodySize) {
            int before = buffer.position();
            items.add(PacketMapper.fromBuffer(buffer));
            read += buffer.position() - before;
        }
        return new ListPacket(items.toArray(new Packet[0]));
    }

    // ── Decode ───────────────────────────────────────────────────────────────

    @Override
    public PacketType getPacketType() {
        return PacketType.ARRAY;
    }

    // ── Encode ───────────────────────────────────────────────────────────────

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

    // ── Getters ──────────────────────────────────────────────────────────────


}