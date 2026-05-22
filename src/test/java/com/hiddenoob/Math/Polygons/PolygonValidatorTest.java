package com.hiddenoob.Math.Polygons;

import org.junit.jupiter.api.Test;

import com.hiddenoob.Math.Vector2;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PolygonValidatorTest {

    @Test
    void testValidPolygon() {
        List<Vector2> vertices = List.of(
            new Vector2(0, 0),
            new Vector2(10, 0),
            new Vector2(10, 10),
            new Vector2(0, 10)
        );

        assertDoesNotThrow(() -> PolygonBuilder.LINE_BUILDER.fromVertices(vertices), "Geçerli bir kare polygon hata vermemeli.");
    }

    @Test
    void testInsufficientVertices() {
        // Sadece 2 köşe verildiğinde builder 2 kenar oluşturur, bu da bir poligon için yetersizdir (en az 3 kenar gerekir).
        List<Vector2> vertices = List.of(
            new Vector2(0, 0),
            new Vector2(10, 0)
        );

        assertThrows(IllegalArgumentException.class, () -> PolygonBuilder.LINE_BUILDER.fromVertices(vertices), "Yetersiz kenar sayısı hata fırlatmalı.");
    }

    @Test
    void testNullOrEmpty() {
        assertThrows(IllegalArgumentException.class, () -> PolygonBuilder.LINE_BUILDER.fromVertices(null), "Null vertices listesi hata fırlatmalı.");
        assertThrows(IllegalArgumentException.class, () -> PolygonBuilder.LINE_BUILDER.fromVertices(new ArrayList<>()), "Boş vertices listesi hata fırlatmalı.");
    }
}