package com.hiddenoob.space_war_server.gameObjects;

import com.hiddenoob.Math.Vector2;

public class PhysicsBody implements Cloneable {
    private final static double FRICTION_RATE = 0.4;
    private final static double MIN_MASS = 1;
    private final static double MIN_INERTIA = 1;
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
        this.position = position != null ? position.clone() : new Vector2();
        // Clone position
        this.velocity = new Vector2();
        this.force = new Vector2();
        setMass(mass);
        setInertia(inertia);
        setRotation(0.0);
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    public Vector2 getForce() {
        return force;
    }

    public void applyForce(Vector2 force) {
        this.force.add(force);
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = Math.max(1, mass);
    }

    public double getInertia() {
        return inertia;
    }

    public void setInertia(double inertia) {
        this.inertia = Math.max(1, inertia);
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public double getSpeed() {
        return velocity.length();
    }

    public void update(double dt) {

        Vector2 acceleration = force.clone().mul(1.0 / mass);

        velocity.add(acceleration.mul(dt));
        velocity.mul(Math.pow(1.0 - FRICTION_RATE, dt));

        position.add(velocity.clone().mul(dt));


        // Reset force for the next frame by setting its components to zero.
        force.x = 0;
        force.y = 0;
    }

    @Override
    public PhysicsBody clone() {
        try {
            PhysicsBody cloned = (PhysicsBody) super.clone();
            cloned.position = this.position.clone();
            cloned.velocity = this.velocity.clone();
            cloned.force = this.force.clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e); // Should not happen as we implement
            // Cloneable
        }
    }
}