package com.devi.main;

import com.devi.main.utils.AreaChecker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AreaCheckerTest {


    @Test
    public void testIsInAreaForTriangle() {
        // Inside the triangle
        assertTrue(AreaChecker.isInArea(5, 0, 10));

        // Outside the triangle
        assertFalse(AreaChecker.isInArea(10.1, 0, 10));
        assertFalse(AreaChecker.isInArea(6, -5, 10));
    }


    @Test
    public void testForZeroCoords() {
        assertTrue(AreaChecker.isInArea(0, 0, 10),
                "Точка в центре должна быть в области");
        assertFalse(
                AreaChecker.isInArea(0, 0, 0),
                "При нулевом радиусе точка не должна быть внутри области, даже в центре");
    }

    @Test
    public void testBoundaryConditions() {
        // Граничные условия для полукруга
        assertTrue(AreaChecker.isInArea(-5, 0, 10),
                "Точка на границе полукруга должна быть внутри.");
        assertFalse(AreaChecker.isInArea(-5.1, 0, 10),
                "Точка вне границы полукруга должна быть снаружи.");

        // Граничные условия для прямоугольника
        assertTrue(AreaChecker.isInArea(10, 5, 10),
                "Точка на границе прямоугольника должна быть внутри.");
        assertFalse(AreaChecker.isInArea(10.1, 5, 10),
                "Точка вне границ прямоугольника должна быть снаружи.");

        // Граничные условия для треугольника
        assertTrue(AreaChecker.isInArea(5, 0, 10),
                "Точка на границе треугольника должна быть внутри.");
        assertFalse(AreaChecker.isInArea(5.1, -0.1, 10),
                "Точка вне границ треугольника должна быть снаружи.");
    }
}
