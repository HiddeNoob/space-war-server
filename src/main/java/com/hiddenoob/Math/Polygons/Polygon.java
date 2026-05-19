package com.hiddenoob.Math.Polygons;

import java.util.List;

import com.hiddenoob.Math.Lines.Line;

import java.util.Collections;

public class Polygon<T extends Line> {

    protected List<T> lines;

    protected Polygon(List<T> lines){
        this.lines = lines;
        PolygonValidator.validatePolygon(lines); // enforce all polygons to be convex
    }

    
    /**
     * Returns a view of the lines.
     * You can not modify lines.
     */
    public List<T> getLines(){
        return Collections.unmodifiableList(lines);
    }

    public int getLineCount() {
        return lines.size();
    }


}

