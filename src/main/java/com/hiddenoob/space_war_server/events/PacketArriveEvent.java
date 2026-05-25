package com.hiddenoob.space_war_server.events;

import com.hiddenoob.space_war_server.gameObjects.Player;
import com.hiddenoob.space_war_server.packets.Packet;
import org.springframework.context.ApplicationEvent;

public class PacketArriveEvent extends ApplicationEvent {
    private final Player player;
    private final Packet packet;

    public PacketArriveEvent(Object source, Player player, Packet packet) {
        super(source);
        this.player = player;
        this.packet = packet;
    }

    public Player getPlayer() {
        return player;
    }

    public Packet getPacket() {
        return packet;
    }
}
