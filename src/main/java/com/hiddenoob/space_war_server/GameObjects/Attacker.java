package com.hiddenoob.space_war_server.GameObjects;

import com.hiddenoob.Math.Lines.BreakableLine;
import com.hiddenoob.Math.Polygons.ConcavePolygon;

public class Attacker extends Astreoid {

    public Attacker() {
        super();
    }

    public Attacker(PhysicsBody physicsConfig, ConcavePolygon<BreakableLine> shape) {
        super(physicsConfig, shape);
    }


}

