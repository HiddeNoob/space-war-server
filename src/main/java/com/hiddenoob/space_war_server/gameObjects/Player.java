package com.hiddenoob.space_war_server.gameObjects;

import org.springframework.web.socket.WebSocketSession;

public class Player extends Attacker implements Cloneable {
    
    private WebSocketSession session;

    public Player(WebSocketSession client){
        this.session = client;
    }

    public WebSocketSession getSession(){ return session; }

    void shoot(){
        
    }

    @Override
    public Player clone() {
        Player cloned = (Player) super.clone();
        // WebSocketSession is not clonable and represents a unique connection.
        // A cloned player typically represents a game state copy rather than an active connection.
        cloned.session = null; 
        return cloned;
    }
}