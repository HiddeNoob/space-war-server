package com.hiddenoob.space_war_server.logics;

import com.hiddenoob.space_war_server.events.PacketArriveEvent;
import com.hiddenoob.space_war_server.gameObjects.Player;
import com.hiddenoob.space_war_server.packets.Packet;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LogicHandler {

    private final List<GameLogic> logics;

    public LogicHandler(List<GameLogic> logics) {
        this.logics = logics;
    }

    @EventListener
    public void handlePacketArrive(PacketArriveEvent event) {
        processPacket(event.getPlayer(), event.getPacket());
    }

    private void processPacket(Player player, Packet packet) {
        if (player == null || packet == null) return;
        
        for (GameLogic logic : logics) {
            logic.handle(player, packet);
        }
    }
}
