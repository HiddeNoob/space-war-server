package com.hiddenoob.space_war_server.packets;

import java.nio.ByteBuffer;

public class WorldStatePacket extends Packet {
    private final PlayerStatePacket localPlayer;
    private final ListPacket nearObjects;

    public WorldStatePacket(PlayerStatePacket localPlayer,
                            ListPacket nearObjects) {
        this.localPlayer = localPlayer;
        this.nearObjects = nearObjects;
    }

    public static WorldStatePacket decode(ByteBuffer buffer) {
        buffer.getInt(); // bodySize
        PlayerStatePacket localPlayer =
                (PlayerStatePacket) PacketMapper.fromBuffer(buffer);
        ListPacket nearObjects = (ListPacket) PacketMapper.fromBuffer(buffer);
        return new WorldStatePacket(localPlayer, nearObjects);
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.WORLD_STATE;
    }

    @Override
    protected int getBodySize() {
        return localPlayer.getPacketSize() + nearObjects.getPacketSize();
    }

    @Override
    protected void writeToPacketBody(ByteBuffer buffer) {
        localPlayer.exportPacketToBuffer(buffer);
        nearObjects.exportPacketToBuffer(buffer);
    }
}
