package com.hiddenoob.space_war_server.server;

import com.hiddenoob.space_war_server.GameObjects.Astreoid;
import com.hiddenoob.Math.Vector2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class MapTest {

    private Map gameMap;

    @BeforeEach
    void setUp() {
        gameMap = new Map();
    }

    @Test
    void testAddAndQueryObject() {
        Astreoid asteroid = Mockito.mock(Astreoid.class);
        Vector2 position = new Vector2(10.0, 10.0);
        when(asteroid.getPosition()).thenReturn(position);

        gameMap.addObject(asteroid);

        // Query range that includes the asteroid
        List<Astreoid> results = gameMap.queryRange(0, 20, 0, 20);
        assertEquals(1, results.size(), "Should find exactly one asteroid");
        assertEquals(asteroid, results.get(0), "The found asteroid should be the one we added");
    }

    @Test
    void testQueryOutsideRange() {
        Astreoid asteroid = Mockito.mock(Astreoid.class);
        Vector2 position = new Vector2(50.0, 50.0);
        when(asteroid.getPosition()).thenReturn(position);

        gameMap.addObject(asteroid);

        // Query range that excludes the asteroid
        List<Astreoid> results = gameMap.queryRange(0, 10, 0, 10);
        assertTrue(results.isEmpty());
    }

    @Test
    void testAddNullHandling() {
        assertDoesNotThrow(() -> gameMap.addObject(null));
        
        Astreoid asteroidWithNullPos = Mockito.mock(Astreoid.class);
        when(asteroidWithNullPos.getPosition()).thenReturn(null);
        assertDoesNotThrow(() -> gameMap.addObject(asteroidWithNullPos));
    }
}