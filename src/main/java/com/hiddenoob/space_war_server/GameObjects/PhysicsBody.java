package com.hiddenoob.space_war_server.GameObjects;

import com.hiddenoob.Math.Vector2;

public class PhysicsBody {
    private Vector2 position;
    private Vector2 velocity;
    private Vector2 force;
    private double mass;
    private double inertia;
    private double rotation;

    public PhysicsBody() {
        this(new Vector2(), 1.0, 1.0);
    }

    public PhysicsBody(Vector2 position, double mass, double inertia) {
        this.position = position != null ? position : new Vector2();
        this.velocity = new Vector2();
        this.force = new Vector2();
        this.mass = Math.max(0.001, mass);
        this.inertia = Math.max(0.001, inertia);
        this.rotation = 0.0;
    }

    public Vector2 getPosition() { return position; }
    public void setPosition(Vector2 position) { this.position = position; }

    public Vector2 getVelocity() { return velocity; }
    public void setVelocity(Vector2 velocity) { this.velocity = velocity; }

    public Vector2 getForce() { return force; }
    public void applyForce(Vector2 force) { this.force = this.force.add(force); }

    public double getMass() { return mass; }
    public void setMass(double mass) { this.mass = Math.max(0.001, mass); }

    public double getInertia() { return inertia; }
    public void setInertia(double inertia) { this.inertia = Math.max(0.001, inertia); }

    public double getRotation() { return rotation; }
    public void setRotation(double rotation) { this.rotation = rotation; }

    public double getSpeed() { return velocity.length(); }

    public void update(double dt) {
        Vector2 acceleration = force.mul(1.0 / mass);
        velocity = velocity.add(acceleration.mul(dt));
        position = position.add(velocity.mul(dt));
        force = new Vector2();
    }
}
