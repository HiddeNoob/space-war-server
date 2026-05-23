package com.hiddenoob.space_war_server.gameObjects;

import com.hiddenoob.Math.Polygons.Rectangle;
import com.hiddenoob.Math.Vector2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class MapTest {

    private Map gameMap;

    @BeforeEach
    void setUp() {
        gameMap = new Map();
    }

    @Test
    void testAddAndQueryObject() {
        Asteroid asteroid = Mockito.mock(Asteroid.class);
        Vector2 position = new Vector2(10.0, 10.0);
        when(asteroid.getPosition()).thenReturn(position);

        gameMap.addObject(asteroid);

        // Query range that includes the asteroid
        List<Asteroid> results = gameMap.queryRange(new Rectangle(0, 20, 0,
                20));
        assertEquals(1, results.size(), "Should find exactly one asteroid");
        assertEquals(asteroid, results.getFirst(), "The found asteroid should" +
                " be " +
                "the one we added");
    }

    @Test
    void testQueryOutsideRange() {
        Asteroid asteroid = Mockito.mock(Asteroid.class);
        Vector2 position = new Vector2(50.0, 50.0);
        when(asteroid.getPosition()).thenReturn(position);

        gameMap.addObject(asteroid);

        // Query range that excludes the asteroid
        List<Asteroid> results = gameMap.queryRange(new Rectangle(0, 10, 0,
                10));
        assertTrue(results.isEmpty());
    }

    @Test
    void testAddNullHandling() {
        assertDoesNotThrow(() -> gameMap.addObject(null));

        Asteroid asteroidWithNullPos = Mockito.mock(Asteroid.class);
        when(asteroidWithNullPos.getPosition()).thenReturn(null);
        assertDoesNotThrow(() -> gameMap.addObject(asteroidWithNullPos));
    }
}