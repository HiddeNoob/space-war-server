package com.hiddenoob.space_war_server.packets;

public enum PacketType {
    UNKNOWN((byte) 0),
    VECTOR2((byte) 1),
    LINE((byte) 2),
    BREAKABLE_LINE((byte) 3),
    POLYGON((byte) 4),
    NOTIFICATION((byte) 5),
    STRING((byte) 6),
    ARRAY((byte) 7),
    UNIFORM_ARRAY((byte) 8),
    ACTION((byte) 9),
    WORLD_STATE((byte) 10),
    PLAYER_STATE((byte) 11),
    ENTITY_STATE((byte) 12);

    private final byte id;

    PacketType(byte id) {
        this.id = id;
    }

    public byte getId() {
        return id;
    }
}