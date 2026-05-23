package com.hiddenoob.space_war_server.gameObjects;

import org.locationtech.jts.index.quadtree.Quadtree;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.locationtech.jts.geom.Envelope;

@Service
public class Map {
    private final Quadtree map = new Quadtree();

    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    public void addObject(Astreoid e) {
        if (e == null || e.getPosition() == null) {
            return;
        }
        rwLock.writeLock().lock();
        try {
            map.insert(new Envelope(e.getPosition().x, e.getPosition().x, e.getPosition().y, e.getPosition().y), e);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    @SuppressWarnings("unchecked")
    public List<Astreoid> queryRange(double minX, double maxX, double minY, double maxY) {
        Envelope env = new Envelope(minX, maxX, minY, maxY);

        rwLock.readLock().lock();
        try {
            return map.query(env);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @SuppressWarnings("unchecked")
    public List<Astreoid> getAll(){
        rwLock.readLock().lock();
        try {
            return map.queryAll();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void removeObject(Astreoid e) {
        if (e == null || e.getPosition() == null) {
            return;
        }
        rwLock.writeLock().lock();
        try {
            this.map.remove(new Envelope(e.getPosition().x, e.getPosition().x, e.getPosition().y, e.getPosition().y), e);
        } finally {
            rwLock.writeLock().unlock();
        }
    }
}