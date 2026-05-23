package com.hiddenoob.space_war_server.gameObjects;

import com.hiddenoob.Math.Lines.BreakableLine;
import com.hiddenoob.Math.Polygons.Polygon;

public class Attacker extends Asteroid implements Cloneable {

    public Attacker() {
        super();
    }

    public Attacker(PhysicsBody physicsConfig, Polygon<BreakableLine> shape) {
        super(physicsConfig, shape);
    }

    @Override
    public Attacker clone() {
        // Attacker does not add any new mutable fields, so a super.clone() is sufficient
        // The Asteroid.clone() method already handles the deep cloning of physics and shape.
        return (Attacker) super.clone();
    }
}