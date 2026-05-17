package com.hiddenoob.Math.Polygons;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.hiddenoob.Math.Vector2;
import com.hiddenoob.Math.Lines.Line;

class PolygonValidator {

    private static final double EPS = 1e-9;

    protected static <T extends Line> void validateConcavePolygon(Collection<T> lines) {
        if (lines == null || lines.size() < 3) {
            throw new IllegalArgumentException("Concave polygon requires at least 3 sides");
        }

        List<Vector2> vertices = orderVertices(lines);
        if (vertices.size() < 3) {
            throw new IllegalArgumentException("Lines do not form a valid polygon");
        }

        if (!isClosed(vertices)) {
            throw new IllegalArgumentException("Lines are not connected and closed");
        }

        if (!isConcave(vertices)) {
            throw new IllegalArgumentException("Polygon is not concave");
        }
    }

    private static <T extends Line> List<Vector2> orderVertices(Collection<T> lines) {
        List<T> remaining = new ArrayList<>(lines);
        if (remaining.isEmpty()) {
            return List.of();
        }

        List<Vector2> vertices = new ArrayList<>();
        T first = remaining.remove(0);
        Vector2 start = first.getA();
        Vector2 current = first.getB();
        vertices.add(start);
        vertices.add(current);

        while (!remaining.isEmpty()) {
            boolean found = false;
            Iterator<T> iterator = remaining.iterator();
            while (iterator.hasNext()) {
                T line = iterator.next();
                if (almostEquals(line.getA(), current)) {
                    current = line.getB();
                    vertices.add(current);
                    iterator.remove();
                    found = true;
                    break;
                }
                if (almostEquals(line.getB(), current)) {
                    current = line.getA();
                    vertices.add(current);
                    iterator.remove();
                    found = true;
                    break;
                }
            }
            if (!found) {
                return List.of();
            }
        }

        return vertices;
    }

    private static boolean isClosed(List<Vector2> vertices) {
        if (vertices.size() < 2) {
            return false;
        }
        return almostEquals(vertices.get(0), vertices.get(vertices.size() - 1));
    }

    private static boolean isConcave(List<Vector2> vertices) {
        int count = vertices.size();
        if (count < 4) {
            return false;
        }

        Boolean hasPositive = false;
        Boolean hasNegative = false;

        for (int i = 0; i < count - 2; i++) {
            Vector2 a = vertices.get(i);
            Vector2 b = vertices.get(i + 1);
            Vector2 c = vertices.get(i + 2);
            double cross = cross(a, b, c);
            if (Math.abs(cross) < EPS) {
                continue;
            }
            if (cross > 0) {
                hasPositive = true;
            } else if (cross < 0) {
                hasNegative = true;
            }
            if (hasPositive && hasNegative) {
                return true;
            }
        }

        return false;
    }

    private static double cross(Vector2 a, Vector2 b, Vector2 c) {
        double dx1 = b.x - a.x;
        double dy1 = b.y - a.y;
        double dx2 = c.x - b.x;
        double dy2 = c.y - b.y;
        return dx1 * dy2 - dy1 * dx2;
    }

    private static boolean almostEquals(Vector2 p1, Vector2 p2) {
        return Math.abs(p1.x - p2.x) < EPS && Math.abs(p1.y - p2.y) < EPS;
    }
}
