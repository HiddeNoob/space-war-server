package com.hiddenoob.space_war_server.packets;

import java.nio.ByteBuffer;

public class PlayerStatePacket extends Packet {
    private final Vector2Packet position;
    private final float rotation;
    private final PolygonPacket polygon;

    public PlayerStatePacket(Vector2Packet position, float rotation, PolygonPacket polygon) {
        this.position = position;
        this.rotation = rotation;
        this.polygon = polygon;
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.PLAYER_STATE;
    }

    @Override
    protected int getBodySize() {
        return position.getPacketSize() + Float.BYTES + polygon.getPacketSize();
    }

    @Override
    protected void writeToPacketBody(ByteBuffer buffer) {
        position.exportPacketToBuffer(buffer);
        buffer.putFloat(rotation);
        polygon.exportPacketToBuffer(buffer);
    }

    public static PlayerStatePacket decode(ByteBuffer buffer) {
        buffer.getInt(); // bodySize
        Vector2Packet position = (Vector2Packet) PacketMapper.fromBuffer(buffer);
        float rotation = buffer.getFloat();
        PolygonPacket polygon = (PolygonPacket) PacketMapper.fromBuffer(buffer);
        return new PlayerStatePacket(position, rotation, polygon);
    }

    public Vector2Packet getPosition() {
        return position;
    }

    public float getRotation() {
        return rotation;
    }

    public PolygonPacket getPolygon() {
        return polygon;
    }
}
