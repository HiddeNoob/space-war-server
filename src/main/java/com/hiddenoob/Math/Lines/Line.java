package com.hiddenoob.Math.Lines;

import com.hiddenoob.Math.Vector2;
import com.hiddenoob.space_war_server.DTOs.Lines.LineDTO;

public class Line {
    protected Vector2 a;
    protected Vector2 b;

    public Line(Vector2 a, Vector2 b) {
        this.a = a;
        this.b = b;
    }

    public Vector2 getA() { return a; }
    public Vector2 getB() { return b; }

    public double length() { return a.distance(b); }

    public Vector2 midpoint() { return new Vector2((a.x + b.x)/2.0, (a.y + b.y)/2.0); }

    @Override
    public Object clone(){
        return new Line(a.clone(),b.clone());
    }

    public LineDTO toDTO(){
        return new LineDTO(this);
    }
}

