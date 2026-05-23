package com.hiddenoob.Math.Polygons;

public class Rectangle {
    private final double xMin;
    private final double xMax;
    private final double yMin;
    private final double yMax;

    public Rectangle(double xMin, double xMax, double yMin, double yMax) {
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
    }

    public double getxMin() {
        return xMin;
    }

    public double getyMin() {
        return yMin;
    }

    public double getxMax() {
        return xMax;
    }

    public double getyMax() {
        return yMax;
    }
}