package com.hiddenoob.Math.Polygons;

import com.hiddenoob.Math.Lines.Line;
import com.hiddenoob.Math.Vector2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

class PolygonValidator {

    private static final double EPS = 1e-9;

    protected static <T extends Line> void validatePolygon(Collection<T> lines) {
        if (lines == null || lines.size() < 3) {
            throw new IllegalArgumentException("A valid polygon requires at " +
                    "least 3 sides");
        }

        List<Vector2> vertices = orderVertices(lines);
        if (vertices.size() < 3) {
            throw new IllegalArgumentException("Lines do not form a valid " +
                    "polygon");
        }

        if (!isClosed(vertices)) {
            throw new IllegalArgumentException("Polygon is not closed (open " +
                    "loop)");
        }

    }

    private static <T extends Line> List<Vector2> orderVertices(Collection<T> lines) {
        List<T> remaining = new ArrayList<>(lines);
        if (remaining.isEmpty()) return List.of();

        List<Vector2> vertices = new ArrayList<>();
        T first = remaining.remove(0);
        Vector2 start = first.getStart();
        Vector2 current = first.getEnd();
        vertices.add(start);
        vertices.add(current);

        while (!remaining.isEmpty()) {
            boolean found = false;
            Iterator<T> iterator = remaining.iterator();
            while (iterator.hasNext()) {
                T line = iterator.next();
                if (almostEquals(line.getStart(), current)) {
                    current = line.getEnd();
                    vertices.add(current);
                    iterator.remove();
                    found = true;
                    break;
                }
                if (almostEquals(line.getEnd(), current)) {
                    current = line.getStart();
                    vertices.add(current);
                    iterator.remove();
                    found = true;
                    break;
                }
            }
            if (!found) return List.of(); // Çizgiler kopuksa patlat
        }
        return vertices;
    }

    private static boolean isClosed(List<Vector2> vertices) {
        if (vertices.size() < 2) return false;
        return almostEquals(vertices.get(0), vertices.get(vertices.size() - 1));
    }

    private static boolean almostEquals(Vector2 p1, Vector2 p2) {
        return Math.abs(p1.x - p2.x) < EPS && Math.abs(p1.y - p2.y) < EPS;
    }
}