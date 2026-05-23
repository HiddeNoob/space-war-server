package com.hiddenoob.Math;

public class Vector2 {
    public static final Vector2 ZERO = new Vector2();
    public static final Vector2 ONE = new Vector2(1, 1);
    public double x;
    public double y;

    public Vector2() {
        this(0, 0);
    }

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2 add(Vector2 o) {
        this.x += o.x;
        this.y += o.y;
        return this;
    }

    public Vector2 sub(Vector2 o) {
        this.x -= o.x;
        this.y -= o.y;
        return this;
    }

    public Vector2 mul(double s) {
        this.x *= s;
        this.y *= s;
        return this;
    }

    public double length() {
        return Math.hypot(x, y);
    }

    public double distance(Vector2 o) {
        return Math.hypot(this.x - o.x, this.y - o.y);
    }

    public Vector2 rotate(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double newX = this.x * cos - this.y * sin;
        double newY = this.x * sin + this.y * cos;
        this.x = newX;
        this.y = newY;
        return this;
    }

    @Override
    public String toString() {
        return "Vector2(" + x + "," + y + ")";
    }

    @Override
    public Vector2 clone() {
        return new Vector2(x, y);
    }


}