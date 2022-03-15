package com.orm2_graph_library.core;

import java.awt.*;
import java.util.ArrayList;

public class GeometryApproximation {
    private ArrayList<Point> _points;

    public GeometryApproximation(ArrayList<Point> points) {
        this._points = points;
    }

    public boolean intersectsWith(GeometryApproximation other) {
        assert false : "@todo";
        return true;
    }

    public ArrayList<Point> points() { return (ArrayList<Point>)this._points.clone(); }
}
