package com.hiddenoob.space_war_server.events;

import com.hiddenoob.space_war_server.gameObjects.Player;
import org.springframework.context.ApplicationEvent;

public class SendMessageEvent extends ApplicationEvent {
    private final byte[] message;
    private final Player targetPlayer; // Mesajın gönderileceği oyuncu

    public SendMessageEvent(Object source, byte[] message, Player targetPlayer) {
        super(source);
        this.message = message;
        this.targetPlayer = targetPlayer;
    }

    public byte[] getMessage() {
        return message;
    }

    public Player getTargetPlayer() {
        return targetPlayer;
    }
}