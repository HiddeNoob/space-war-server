package com.hiddenoob.space_war_server.gameObjects;

import org.locationtech.jts.index.quadtree.Quadtree;
import org.springframework.stereotype.Service;

import java.util.List;

import org.locationtech.jts.geom.Envelope;

@Service
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

    @SuppressWarnings("unchecked")
    public List<Astreoid> getAll(){
        return map.queryAll();
    }    

    public void removeObject(Astreoid e) {
        this.map.remove(new Envelope(e.getPosition().x, e.getPosition().x, e.getPosition().y, e.getPosition().y),e);
    }
}
