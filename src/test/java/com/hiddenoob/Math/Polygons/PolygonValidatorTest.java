package com.hiddenoob.Math.Polygons;

import com.hiddenoob.Math.Vector2;
import com.hiddenoob.Math.Lines.Line;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class PolygonValidatorTest {

    private Line mockLine(Vector2 a, Vector2 b) {
        Line line = Mockito.mock(Line.class);
        when(line.getA()).thenReturn(a);
        when(line.getB()).thenReturn(b);
        return line;
    }

    @Test
    void testValidConcavePolygon() {
        // An L-shaped polygon is concave
        Vector2 p1 = new Vector2(0, 0);
        Vector2 p2 = new Vector2(4, 0);
        Vector2 p3 = new Vector2(4, 2);
        Vector2 p4 = new Vector2(2, 2);
        Vector2 p5 = new Vector2(2, 4);
        Vector2 p6 = new Vector2(0, 4);

        List<Line> lines = Arrays.asList(
            mockLine(p1, p2),
            mockLine(p2, p3),
            mockLine(p3, p4),
            mockLine(p4, p5),
            mockLine(p5, p6),
            mockLine(p6, p1)
        );

        assertDoesNotThrow(() -> PolygonValidator.validateConcavePolygon(lines));
    }

    @Test
    void testConvexPolygonFailsConcavityCheck() {
        // A square is convex, not concave
        Vector2 p1 = new Vector2(0, 0);
        Vector2 p2 = new Vector2(2, 0);
        Vector2 p3 = new Vector2(2, 2);
        Vector2 p4 = new Vector2(0, 2);

        List<Line> lines = Arrays.asList(
            mockLine(p1, p2),
            mockLine(p2, p3),
            mockLine(p3, p4),
            mockLine(p4, p1)
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            PolygonValidator.validateConcavePolygon(lines)
        );
        assertEquals("Polygon is not concave", exception.getMessage());
    }

    @Test
    void testInvalidSideCount() {
        Vector2 p1 = new Vector2(0, 0);
        Vector2 p2 = new Vector2(2, 0);

        List<Line> lines = Arrays.asList(mockLine(p1, p2));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            PolygonValidator.validateConcavePolygon(lines)
        );
        assertEquals("Concave polygon requires at least 3 sides", exception.getMessage());
    }
}