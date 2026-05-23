package com.hiddenoob.space_war_server.packets;

import java.nio.ByteBuffer;

public class BreakableLinePacket extends LinePacket {

    private final int health;

    public BreakableLinePacket(int health,Vector2Packet a, Vector2Packet b) {
        super(a, b);
        this.health = health;
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.BREAKABLE_LINE;
    }

    public int getHealth() {
        return health;
    }

    @Override
    protected int getBodySize() {
        return Integer.BYTES + super.getBodySize();
    }

    @Override
    public void writeToPacketBody(ByteBuffer buffer) {
        buffer.putInt(health);
        super.writeToPacketBody(buffer);
    }
}
