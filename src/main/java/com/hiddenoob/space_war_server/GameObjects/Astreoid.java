package com.hiddenoob.space_war_server.GameObjects;

import java.util.ArrayList;
import java.util.List;

import com.hiddenoob.Math.Vector2;
import com.hiddenoob.Math.Lines.BreakableLine;
import com.hiddenoob.Math.Polygons.ConcavePolygon;

public class Astreoid {
    protected final PhysicsBody physics;
    protected ConcavePolygon<BreakableLine> shape;

    public Astreoid() {
        this(new Vector2(), 1.0, 1.0);
    }

    public Astreoid(PhysicsBody config){
        this.physics = config;
    }

    public Astreoid(Vector2 position){
        this.physics = new PhysicsBody();
        this.physics.setPosition(position);
    }


    public Astreoid(Vector2 position, double mass, double inertia) {
        this.physics = new PhysicsBody(position, mass, inertia);
    }


    public PhysicsBody getPhysics() {
        return physics;
    }

    public Vector2 getPosition(){
        return physics.getPosition();
    }

    public ConcavePolygon<BreakableLine> getShape() {
        return shape;
    }

    public void setShape(ConcavePolygon<BreakableLine> shape, int lineDurability) {
        this.shape = shape;
    }

    public void setShape(ConcavePolygon<BreakableLine> shape) {
        setShape(shape, 1);
    }




    public List<BreakableLine> getLines() {
        return new ArrayList<>();
    }

    public void update(double dt) {
        physics.update(dt);
    }

}
