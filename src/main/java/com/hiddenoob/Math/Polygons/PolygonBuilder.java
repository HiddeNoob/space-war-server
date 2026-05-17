package com.hiddenoob.Math.Polygons;

import java.util.ArrayList;
import java.util.List;

import com.hiddenoob.Math.Vector2;
import com.hiddenoob.Math.Lines.BreakableLine;
import com.hiddenoob.Math.Lines.Line;

public class PolygonBuilder<T extends Line> {

    // singletons
    public static final PolygonBuilder<Line> LINE_BUILDER = new PolygonBuilder<>(Line::new);
    public static final PolygonBuilder<BreakableLine> BREAKABLE_LINE_BUILDER = new PolygonBuilder<>(BreakableLine::new);

    // factory'e line nasıl oluşturuluyor soylemek icin
    private final SegmentFactory<T> lineFactory;

    private PolygonBuilder(SegmentFactory<T> lineFactory) {
        this.lineFactory = lineFactory;
    }


    

    public ConcavePolygon<T> rectangle(double width, double height) {
        double hw = width / 2.0, hh = height / 2.0;
        return fromVertices(List.of(
            new Vector2(-hw, -hh),
            new Vector2( hw, -hh),
            new Vector2( hw,  hh),
            new Vector2(-hw,  hh)
        ));
    }

    public ConcavePolygon<T> regularPolygon(int sides, double radius) {
        if (sides < 3) throw new IllegalArgumentException("En az 3 kenar gerekli");
        ArrayList<Vector2> vertices = new ArrayList<>();
        double step = 2 * Math.PI / sides;
        for (int i = 0; i < sides; i++) {
            vertices.add(new Vector2(Math.cos(i * step) * radius, Math.sin(i * step) * radius));
        }
        return fromVertices(vertices);
    }

    public ConcavePolygon<T> star(int points, double outerRadius, double innerRadius) {
        if (points < 2) throw new IllegalArgumentException("En az 2 köşe gerekli");
        ArrayList<Vector2> vertices = new ArrayList<>();
        double step = Math.PI / points;
        for (int i = 0; i < points * 2; i++) {
            double r = (i % 2 == 0) ? outerRadius : innerRadius;
            vertices.add(new Vector2(Math.cos(i * step) * r, Math.sin(i * step) * r));
        }
        return fromVertices(vertices);
    }

    public ConcavePolygon<T> fromVertices(List<Vector2> vertices) {
        ArrayList<T> lines = new ArrayList<>();
        if (vertices == null || vertices.size() < 2) return new ConcavePolygon<>(lines);
        int count = vertices.size();
        for (int i = 0; i < count; i++) {
            lines.add(lineFactory.create(vertices.get(i), vertices.get((i + 1) % count)));
        }
        return new ConcavePolygon<>(lines);
    }

    @FunctionalInterface
    public interface SegmentFactory<T extends Line> {
        T create(Vector2 a, Vector2 b);
    }


}