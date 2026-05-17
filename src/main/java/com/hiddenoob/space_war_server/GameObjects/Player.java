package com.hiddenoob.space_war_server.GameObjects;

import java.net.Socket;

public class Player extends Attacker {
    Socket connection;

    Player(Socket client){
        this.connection = client;
    }

    void shoot(){
        
    }
}
