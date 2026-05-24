package com.hiddenoob.space_war_server.packets.action;

import java.nio.ByteBuffer;

public abstract class Action {

    public abstract ActionType getActionType();

    /**
     * Bu action'ın body'ye katkısı (byte).
     */
    public abstract int getBodySize();

    /**
     * Body'ye yazar — ActionPacket tarafından çağrılır.
     */
    protected abstract void writeToBody(ByteBuffer buffer);
}