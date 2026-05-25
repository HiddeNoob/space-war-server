package com.hiddenoob.space_war_server.packets;

import java.nio.ByteBuffer;

public class EntityStatePacket extends Packet {
    private final long id;
    private final Vector2Packet position;
    private final Vector2Packet velocity;
    private final float rotation;
    private final PolygonPacket polygon;

    public EntityStatePacket(long id, Vector2Packet position, Vector2Packet velocity, float rotation, PolygonPacket polygon) {
        this.id = id;
        this.position = position;
        this.velocity = velocity;
        this.rotation = rotation;
        this.polygon = polygon;
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.ENTITY_STATE;
    }

    @Override
    protected int getBodySize() {
        return Long.BYTES + position.getPacketSize() + velocity.getPacketSize() + Float.BYTES + polygon.getPacketSize();
    }

    @Override
    protected void writeToPacketBody(ByteBuffer buffer) {
        buffer.putLong(id);
        position.exportPacketToBuffer(buffer);
        velocity.exportPacketToBuffer(buffer);
        buffer.putFloat(rotation);
        polygon.exportPacketToBuffer(buffer);
    }

    public static EntityStatePacket decode(ByteBuffer buffer) {
        buffer.getInt(); // bodySize
        long id = buffer.getLong();
        Vector2Packet position = (Vector2Packet) PacketMapper.fromBuffer(buffer);
        Vector2Packet velocity = (Vector2Packet) PacketMapper.fromBuffer(buffer);
        float rotation = buffer.getFloat();
        PolygonPacket polygon = (PolygonPacket) PacketMapper.fromBuffer(buffer);
        return new EntityStatePacket(id, position, velocity, rotation, polygon);
    }

    public long getId() {
        return id;
    }

    public Vector2Packet getPosition() {
        return position;
    }

    public Vector2Packet getVelocity() {
        return velocity;
    }

    public float getRotation() {
        return rotation;
    }

    public PolygonPacket getPolygon() {
        return polygon;
    }
}
