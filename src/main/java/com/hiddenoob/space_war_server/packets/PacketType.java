package com.hiddenoob.space_war_server.packets;

public enum PacketType {
    VECTOR2((byte) 1),
    LINE((byte) 2),
    POLYGON((byte) 3);

    private final byte id;

    PacketType(byte id) {
        this.id = id;
    }

    public byte getId() {
        return id;
    }
}