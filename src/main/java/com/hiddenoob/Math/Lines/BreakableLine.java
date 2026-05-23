package com.hiddenoob.Math.Lines;

import com.hiddenoob.Math.Vector2;

public class BreakableLine extends Line {

    private final int maxHealth;
    private int health = 100;
    private int durability = 10;

    public BreakableLine(Vector2 a, Vector2 b, int health, int durability) {
        super(a, b);
        this.maxHealth = health;
        this.health = health;
        this.durability = durability; // Initialize durability
    }

    public BreakableLine(Vector2 a, Vector2 b) {
        super(a, b);
        this.maxHealth = health; // Default maxHealth
    }

    public void hit(int damage) {
        health -= damage / durability;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = Math.min(health, maxHealth);
    }

    @Override
    public BreakableLine clone() {
        // Return a new BreakableLine instance, preserving its state
        return new BreakableLine(start.clone(), end.clone(), maxHealth, durability);
    }
}