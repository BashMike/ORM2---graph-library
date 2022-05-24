package com.orm2_graph_library.core;

import com.orm2_graph_library.utils.Point2D;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.geom.Area;
import java.util.ArrayList;

public class GeometryApproximation {
    private Polygon _polygon;

    public GeometryApproximation(@NotNull ArrayList<Point2D> points) {
        int[] pointsX = new int[points.size()];
        int[] pointsY = new int[points.size()];

        for (int i=0; i<points.size(); i++) {
            pointsX[i] = points.get(i).x;
            pointsY[i] = points.get(i).y;
        }

        this._polygon = new Polygon(pointsX, pointsY, points.size());
    }

    private boolean _isConvexPolygon(@NotNull ArrayList<Point> points) {
        return false;
    }

    public Area getIntersectionWith(GeometryApproximation other) {
        Area intersectArea = new Area(this._polygon);
        intersectArea.intersect(new Area(other._polygon));

        return intersectArea;
    }

    public boolean intersectsWith(GeometryApproximation other) { return this.getIntersectionWith(other).isEmpty(); }
    public boolean intersectsWith(Point point)                 { return this._polygon.contains(point); }

    public Polygon polygon() { return new Polygon(this._polygon.xpoints.clone(), this._polygon.ypoints.clone(), this._polygon.npoints); }
}
