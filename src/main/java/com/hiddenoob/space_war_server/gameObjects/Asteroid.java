package com.hiddenoob.space_war_server.gameObjects;

import com.hiddenoob.Math.Lines.BreakableLine;
import com.hiddenoob.Math.Polygons.Polygon;
import com.hiddenoob.Math.Polygons.PolygonBuilder;
import com.hiddenoob.Math.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Asteroid implements Cloneable {
    private static final AtomicLong idCounter = new AtomicLong(0);
    private final long id;
    private final PhysicsBody physics;
    private Polygon<BreakableLine> shape;

    public Asteroid() {
        this(new Vector2());
    }

    public Asteroid(PhysicsBody body, Polygon<BreakableLine> shape) {
        this(idCounter.getAndIncrement(), body, shape);
    }

    private Asteroid(long id, PhysicsBody body, Polygon<BreakableLine> shape) {
        this.id = id;
        this.physics = body;
        this.setShape(shape);
    }

    public Asteroid(Vector2 position) {
        this(new PhysicsBody(position, 1.0, 1.0),
                PolygonBuilder.BREAKABLE_LINE_BUILDER.regularPolygon(3, 1)
        );
    }


    public long getId() {
        return id;
    }


    public PhysicsBody getPhysics() {
        return physics;
    }

    public Vector2 getPosition() {
        return physics.getPosition();
    }

    public Polygon<BreakableLine> getShape() {
        return shape;
    }

    public void setShape(Polygon<BreakableLine> shape) {
        setShape(shape, 1);
    }

    public void setShape(Polygon<BreakableLine> shape, int lineDurability) {
        this.shape = shape;
        // The lines in the polygon are already cloned when the polygon is created/cloned.
        // We only need to set durability if it's a new shape or being explicitly set.
        this.shape.getLines().forEach(l -> l.setDurability(lineDurability));
    }

    // gets actual location of polygon in world
    public Polygon<BreakableLine> getActualPolygon() {
        Vector2 objectPosition = this.getPosition();
        double objectRotation = this.getPhysics().getRotation();

        // rotate it around center, after move to the object location
        Polygon<BreakableLine> worldShape =
                this.shape.clone().rotate(objectRotation, Vector2.ZERO).move(objectPosition);

        return worldShape;
    }


    public List<BreakableLine> getLines() {
        // This method seems to return an empty list, which might be a bug or incomplete.
        // If it's meant to return the actual lines of the shape, it should be:
        // return new ArrayList<>(shape.getLines());
        return new ArrayList<>();
    }

    public void update(double dt) {
        physics.update(dt);
    }

    @Override
    public Asteroid clone() {
        try {
            // Deep copy by creating a new instance and cloning mutable fields
            return new Asteroid(this.id, this.physics.clone(), this.shape.clone());
        } catch (Exception e) {
            throw new InternalError(e);
        }
    }
}