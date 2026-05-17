package com.hiddenoob.space_war_server.server;

import org.locationtech.jts.index.quadtree.Quadtree;

import com.hiddenoob.space_war_server.GameObjects.Astreoid;

import java.util.List;

import org.locationtech.jts.geom.Envelope;

public class Map {
    private final Quadtree map = new Quadtree();

    public void addObject(Astreoid e) {
        if (e == null || e.getPosition() == null) {
            return;
        }
        map.insert(new Envelope(e.getPosition().x, e.getPosition().x, e.getPosition().y, e.getPosition().y), e);
    }

    @SuppressWarnings("unchecked")
    public List<Astreoid> queryRange(double minX, double maxX, double minY, double maxY) {
        Envelope env = new Envelope(minX, maxX, minY, maxY);
        List<Astreoid> query = map.query(env);
        return query;
    }
}
