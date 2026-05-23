package com.hiddenoob.space_war_server.packets;

import java.nio.ByteBuffer;


public class NotificationPacket extends Packet {


    private StringPacket datetime;
    private StringPacket message;
    private StringPacket sender;

    protected NotificationPacket(StringPacket sender, StringPacket message, StringPacket datetime) {
        this.sender = sender;
        this.message = message;
        this.datetime = datetime;
    }

    public StringPacket getDatetime() {
        return datetime;
    }

    public StringPacket getMessage() {
        return message;
    }

    public StringPacket getSender() {
        return sender;
    }

    @Override
    public PacketType getPacketType() { return PacketType.NOTIFICATION; }

    @Override
    public int getBodySize() {
        return sender.getPacketSize() + message.getPacketSize() + datetime.getPacketSize();
    }

    @Override
    public void writeToPacketBody(ByteBuffer buffer) {
        sender.exportPacketToBuffer(buffer);
        datetime.exportPacketToBuffer(buffer);
        message.exportPacketToBuffer(buffer);
    }
}
