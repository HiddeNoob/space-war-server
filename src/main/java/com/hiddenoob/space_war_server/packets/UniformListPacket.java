package com.hiddenoob.space_war_server.packets;

import java.nio.ByteBuffer;

public class UniformListPacket<T extends Packet> extends ListPacket {

    protected UniformListPacket(T[] data) {
        super(data);
    }

    // ── Encode ───────────────────────────────────────────────────────────────

    @Override
    public PacketType getPacketType() {
        return PacketType.UNIFORM_ARRAY;
    }

    /**
     * Header: PacketType(1) + BodySize(4) + ItemType(1) + ItemCount(4) = 10
     * byte
     */
    @Override
    protected int getHeaderSize() {
        return super.getHeaderSize() + Byte.BYTES + Integer.BYTES;
    }

    @Override
    protected void writeToPacketHeader(ByteBuffer buffer) {
        super.writeToPacketHeader(buffer);       // PacketType(1) + BodySize(4)
        buffer.put(getItemType().getId());        // ItemType(1)
        buffer.putInt(getData().length);          // ItemCount(4)
    }

    /**
     * Body: her öğenin sadece body'si yazılır (header yok).
     * Tüm öğeler aynı tipte olduğundan sabit boyut × count.
     */
    @Override
    protected int getBodySize() {
        return getData().length * getData()[0].getBodySize();
    }

    @Override
    protected void writeToPacketBody(ByteBuffer buffer) {
        for (Packet item : getData()) item.writeToPacketBody(buffer);
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public PacketType getItemType() {
        return getData()[0].getPacketType();
    }
}