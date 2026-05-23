package com.hiddenoob.space_war_server.gameObjects.weaponTypes;

import com.hiddenoob.Math.Polygons.PolygonBuilder;
import com.hiddenoob.space_war_server.gameObjects.Asteroid;

public class Bullet extends Asteroid {

    protected Bullet() {
        super();
        PolygonBuilder.BREAKABLE_LINE_BUILDER.regularPolygon(3, 3);
    }

}
