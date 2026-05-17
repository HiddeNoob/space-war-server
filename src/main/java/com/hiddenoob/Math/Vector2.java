package com.hiddenoob.Math;

public class Vector2 {
    public double x;
    public double y;

    public Vector2() { this(0,0); }
    public Vector2(double x, double y) { this.x = x; this.y = y; }

    public Vector2 add(Vector2 o) { return new Vector2(this.x + o.x, this.y + o.y); }
    public Vector2 sub(Vector2 o) { return new Vector2(this.x - o.x, this.y - o.y); }
    public Vector2 mul(double s) { return new Vector2(this.x * s, this.y * s); }

    public double length() { return Math.hypot(x, y); }
    public double distance(Vector2 o) { return Math.hypot(this.x - o.x, this.y - o.y); }

    @Override
    public String toString() { return "Vector2(" + x + "," + y + ")"; }

    @Override
    public Vector2 clone() { return new Vector2(x,y); }
}
