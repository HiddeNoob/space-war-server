package com.hiddenoob.space_war_server.packets;

import java.nio.ByteBuffer;

public class UniformListPacket<T extends Packet> extends  ListPacket{
    protected UniformListPacket(T[] data) {
        super(data);

    }

    @Override
    protected void writeToPacketHeader(ByteBuffer buffer) {
        super.writeToPacketHeader(buffer);
        buffer.put(getItemType().getId()); // her bir paket ayni zaten, packet id'sini koyalım
    }

    public PacketType getItemType(){
        return getData()[0].getPacketType();
    }

    @Override
    public PacketType getPacketType() { return PacketType.UNIFORM_ARRAY; }

    @Override
    protected int getBodySize(){
        return getData().length * getData()[0].getBodySize();
    }

    @Override
    protected int getHeaderSize(){
        return super.getHeaderSize() + 1;
    }

    @Override
    protected void writeToPacketBody(ByteBuffer buffer) {
        for (Packet item : getData()) {
            item.writeToPacketBody(buffer);
        }
    }
}