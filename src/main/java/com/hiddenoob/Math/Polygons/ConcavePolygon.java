package com.hiddenoob.Math.Polygons;

import java.util.ArrayList;

import com.hiddenoob.Math.Lines.Line;

public class ConcavePolygon<T extends Line> extends Polygon<T> {

    protected ConcavePolygon(ArrayList<T> lines) {
        super(lines);
        PolygonValidator.validateConcavePolygon(lines);
    }

    @SuppressWarnings("unchecked")
    private ConcavePolygon(ConcavePolygon<T> polygon){
        super(polygon.lines);
        for (int i = 0; i < lines.size() ; i++) {
            lines.set(i, (T) lines.get(i).clone());
        }
        
    }

    @Override
    public ConcavePolygon<T> clone() {
        return new ConcavePolygon<>(this);
    }
}
