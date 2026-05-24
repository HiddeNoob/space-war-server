package com.hiddenoob.space_war_server.packets.action;

import java.nio.ByteBuffer;

public interface ActionDecoder<T extends Action> {
    /**
     * Buffer, action body başlangıcına konumlanmış şekilde gelir.
     * ActionPacket mask'i okuyup hangi action'ların geleceğini belirledikten
     * sonra sırayla bu decoder'ları çağırır.
     */
    T decode(ByteBuffer buffer);
}