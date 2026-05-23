package com.hiddenoob.Math.Lines;

import com.hiddenoob.Math.Vector2;

public class Line {
    protected Vector2 start;
    protected Vector2 end;

    public Line(Vector2 start, Vector2 end) {
        this.start = start.clone();
        this.end = end.clone();
    }

    public Vector2 getStart() {
        return start;
    }

    public Vector2 getEnd() {
        return end;
    }

    public double length() {
        return start.distance(end);
    }

    public Vector2 midpoint() {
        return new Vector2((start.x + end.x) / 2.0, (start.y + end.y) / 2.0);
    }

    public Line move(Vector2 translation) {
        this.start.add(translation);
        this.end.add(translation);
        return this;
    }

    public Line rotate(double angle, Vector2 pivot) {
        // To rotate a point (P) around a pivot (C) by an angle (A):
        // P' = (P - C) rotated by A + C
        this.start.sub(pivot).rotate(angle).add(pivot);
        this.end.sub(pivot).rotate(angle).add(pivot);
        return this;
    }

    @Override
    public Line clone() {
        return new Line(start.clone(), end.clone());
    }

}