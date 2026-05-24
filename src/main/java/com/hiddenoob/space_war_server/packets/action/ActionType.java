package com.hiddenoob.space_war_server.packets.action;

public enum ActionType {
    FORCE(1),
    ROTATION(2),
    ATTACK(4),
    BOOST(8);

    private final int bit;

    ActionType(int bit) {
        this.bit = bit;
    }

    public int getBit() {
        return bit;
    }
}