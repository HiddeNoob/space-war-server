package com.hiddenoob.Math.Lines;

import com.hiddenoob.Math.Vector2;

public class BreakableLine extends Line {

    private final int maxHealth; 
    private int health = 100;
    private int durability = 10;

    public BreakableLine(Vector2 a, Vector2 b,int health, int durability) {
        super(a,b);
        this.maxHealth = health;
        this.health = health;
    }

    public BreakableLine(Vector2 a, Vector2 b){
        super(a,b);
        this.maxHealth = health;
    }

    public void hit(int damage) {
        health -= damage / durability;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public void setHealth(int health) {
        this.health = Math.min(health, maxHealth);
    }


    public int getHealth() { return health; }

    public BreakableLine copy() { return new BreakableLine(a, b); }
}

