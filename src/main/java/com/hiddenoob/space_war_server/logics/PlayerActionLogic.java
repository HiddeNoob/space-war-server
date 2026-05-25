package com.hiddenoob.space_war_server.logics;

import com.hiddenoob.Math.Vector2;
import com.hiddenoob.space_war_server.gameObjects.Player;
import com.hiddenoob.space_war_server.packets.Packet;
import com.hiddenoob.space_war_server.packets.action.ActionPacket;
import com.hiddenoob.space_war_server.packets.action.ForceAction;
import com.hiddenoob.space_war_server.packets.action.RotationAction;
import org.springframework.stereotype.Component;

@Component
public class PlayerActionLogic implements GameLogic {

    @Override
    public void handle(Player player, Packet packet) {
        if (!(packet instanceof ActionPacket actionPacket)) {
            return;
        }

        actionPacket.getActions().forEach(action -> {
            if (action instanceof ForceAction forceAction) {
                player.getPhysics().applyForce(new Vector2(
                    forceAction.getDx() * 100.0, 
                    forceAction.getDy() * 100.0
                ));
            } else if (action instanceof RotationAction rotationAction) {
                player.getPhysics().setRotation(rotationAction.getTargetAngle());
            }
        });
    }
}
