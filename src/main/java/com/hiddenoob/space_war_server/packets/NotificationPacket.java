package com.hiddenoob.space_war_server.packets;

import java.nio.ByteBuffer;

public class NotificationPacket extends Packet {

    public static NotificationPacket decode(ByteBuffer buffer) {
        buffer.getInt(); // bodySize
        StringPacket sender = (StringPacket) PacketMapper.fromBuffer(buffer);
        StringPacket datetime = (StringPacket) PacketMapper.fromBuffer(buffer);
        StringPacket message = (StringPacket) PacketMapper.fromBuffer(buffer);
        return new NotificationPacket(sender, message, datetime);
    }
    private final StringPacket sender;
    private final StringPacket message;
    private final StringPacket datetime;

    // ── Decode ───────────────────────────────────────────────────────────────

    protected NotificationPacket(StringPacket sender, StringPacket message,
                                 StringPacket datetime) {
        this.sender = sender;
        this.message = message;
        this.datetime = datetime;
    }

    // ── Encode ───────────────────────────────────────────────────────────────

    @Override
    public PacketType getPacketType() {
        return PacketType.NOTIFICATION;
    }

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

    // ── Getters ──────────────────────────────────────────────────────────────

    public StringPacket getSender() {
        return sender;
    }

    public StringPacket getMessage() {
        return message;
    }

    public StringPacket getDatetime() {
        return datetime;
    }
}