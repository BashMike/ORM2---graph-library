package com.orm2_graph_library.nodes_shapes;

import com.orm2_graph_library.core.GeometryApproximation;
import com.orm2_graph_library.core.Shape;
import com.orm2_graph_library.utils.Point2D;

import java.awt.*;
import java.util.ArrayList;

public class RectangleShape extends Shape {
    @Override
    protected GeometryApproximation _geometryApproximation(Point2D borderLeftTop, int borderWidth, int borderHeight) {
        ArrayList<Point2D> points = new ArrayList<>();
        points.add(new Point2D(borderLeftTop.x              , borderLeftTop.y));
        points.add(new Point2D(borderLeftTop.x + borderWidth, borderLeftTop.y));
        points.add(new Point2D(borderLeftTop.x + borderWidth, borderLeftTop.y + borderHeight));
        points.add(new Point2D(borderLeftTop.x              , borderLeftTop.y + borderHeight));

        return new GeometryApproximation(points);
    }
}
