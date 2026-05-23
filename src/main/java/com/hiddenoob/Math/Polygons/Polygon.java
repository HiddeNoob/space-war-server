package com.hiddenoob.Math.Polygons;

import com.hiddenoob.Math.Lines.Line;
import com.hiddenoob.Math.Vector2;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Polygon<T extends Line> {

    protected List<T> lines;

    protected Polygon(List<T> lines) {
        // Clone each line to ensure the Polygon owns its internal Line objects
        this.lines =
                lines.stream().map(line -> (T) line.clone()).collect(Collectors.toList());
        PolygonValidator.validatePolygon(this.lines);
    }


    /**
     * Returns a view of the lines.
     * You can not modify lines.
     */
    public List<T> getLines() {
        return Collections.unmodifiableList(lines);
    }

    public int getLineCount() {
        return lines.size();
    }

    public Polygon<T> move(Vector2 translation) {
        for (T line : lines) {
            line.move(translation); // Modify line in place
        }
        return this; // Return this for chaining
    }

    public Polygon<T> rotate(double angle, Vector2 pivot) {
        for (T line : lines) {
            line.rotate(angle, pivot); // Modify line in place
        }
        return this; // Return this for chaining
    }

    @Override
    public Polygon<T> clone() {
        // Create a new list of cloned lines for the new Polygon instance
        List<T> clonedLines = this.lines.stream()
                .map(line -> (T) line.clone())
                .collect(Collectors.toList());
        return new Polygon<>(clonedLines);
    }

    /**
     * Calculates the maximum and minimum x and y coordinates of the
     * polygon's vertices.
     */
    public Rectangle getMaxEdges() {
        double x_max = Double.MIN_VALUE;
        double x_min = Double.MAX_VALUE;
        double y_max = Double.MIN_VALUE;
        double y_min = Double.MAX_VALUE;

        if (lines.isEmpty()) {
            // Handle empty polygon case, returning a Rectangle with initial
            // min/max values
            return new Rectangle(x_min, x_max, y_min, y_max);
        }

        for (T line : lines) {
            Vector2 start = line.getStart();
            Vector2 end = line.getEnd();

            x_max = Math.max(x_max, start.x);
            x_max = Math.max(x_max, end.x);
            x_min = Math.min(x_min, start.x);
            x_min = Math.min(x_min, end.x);

            y_max = Math.max(y_max, start.y);
            y_max = Math.max(y_max, end.y);
            y_min = Math.min(y_min, start.y);
            y_min = Math.min(y_min, end.y);
        }

        return new Rectangle(x_min, x_max, y_min, y_max);
    }
}