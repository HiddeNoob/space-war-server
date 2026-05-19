package com.hiddenoob.Math.Polygons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hiddenoob.Math.Lines.Line;
import com.hiddenoob.space_war_server.DTOs.Polygons.PolygonDTO;

public abstract class Polygon<T extends Line> {

    protected ArrayList<T> lines;

    protected Polygon(ArrayList<T> lines){
        this.lines = lines;
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

    public PolygonDTO toDTO(){
        return new PolygonDTO(this.getLines().stream().map(Line::toDTO).toList());
    }

}

