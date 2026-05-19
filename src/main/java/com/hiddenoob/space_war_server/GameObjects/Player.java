package com.hiddenoob.space_war_server.GameObjects;

import org.springframework.web.socket.WebSocketSession;

public class Player extends Attacker {
    
    private WebSocketSession session;

    public Player(WebSocketSession client){
        this.session = client;
    }

    public WebSocketSession getSession(){ return session; }

    void shoot(){
        
    }
}
