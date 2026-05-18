package com.hiddenoob.Math;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Vector2Test {

    @Test
    void testVectorInitialization() {
        double x = 10.5;
        double y = -20.3;
        Vector2 vector = new Vector2(x, y);
        assertEquals(x, vector.x, 1e-9);
        assertEquals(y, vector.y, 1e-9);
    }
}