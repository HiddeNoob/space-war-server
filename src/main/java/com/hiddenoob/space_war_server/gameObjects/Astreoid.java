package com.hiddenoob.space_war_server.gameObjects;

import java.util.ArrayList;
import java.util.List;

import com.hiddenoob.Math.Vector2;
import com.hiddenoob.Math.Lines.BreakableLine;
import com.hiddenoob.Math.Polygons.Polygon;
import com.hiddenoob.Math.Polygons.PolygonBuilder;

public class Astreoid {
    private final PhysicsBody physics;
    private Polygon<BreakableLine> shape;

    public Astreoid() {
        this(new Vector2());
    }

    public Astreoid(PhysicsBody body, Polygon<BreakableLine> shape){
        this.setShape(shape);
        this.physics = body;
    }

    public Astreoid(Vector2 position){
        this(new PhysicsBody(position, 1.0, 1.0),
            PolygonBuilder.BREAKABLE_LINE_BUILDER.regularPolygon(3, 1)    
    );
    }




    public PhysicsBody getPhysics() {
        return physics;
    }

    public Vector2 getPosition(){
        return physics.getPosition();
    }

    public Polygon<BreakableLine> getShape() {
        return shape;
    }

    public void setShape(Polygon<BreakableLine> shape, int lineDurability) {
        this.shape = shape;
        this.shape.getLines().forEach(l -> l.setDurability(lineDurability));
    }

    public void setShape(Polygon<BreakableLine> shape) {
        setShape(shape, 1);
    }




    public List<BreakableLine> getLines() {
        return new ArrayList<>();
    }

    public void update(double dt) {
        physics.update(dt);
    }

}
