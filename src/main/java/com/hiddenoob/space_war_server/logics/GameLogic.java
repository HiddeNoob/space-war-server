package com.hiddenoob.space_war_server.logics;

import com.hiddenoob.space_war_server.gameObjects.Player;
import com.hiddenoob.space_war_server.packets.Packet;

public interface GameLogic {
    void handle(Player player, Packet packet);
}
