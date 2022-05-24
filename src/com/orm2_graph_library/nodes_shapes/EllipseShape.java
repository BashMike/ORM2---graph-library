package com.orm2_graph_library.nodes_shapes;

import com.orm2_graph_library.core.GeometryApproximation;
import com.orm2_graph_library.core.Shape;
import com.orm2_graph_library.utils.Point2D;

import java.awt.*;
import java.util.ArrayList;

public class EllipseShape extends Shape {
    @Override
    protected GeometryApproximation _geometryApproximation(Point2D borderLeftTop, int borderWidth, int borderHeight) {
        int pointsNum = 16;
        ArrayList<Point2D> points = new ArrayList<>();

        for (double angle=0.0; angle<2*Math.PI; angle+=2*Math.PI/pointsNum) {
            int x = (int)Math.round((borderWidth /2 * Math.cos(angle)) + borderLeftTop.x + borderWidth/2);
            int y = (int)Math.round((borderHeight/2 * Math.sin(angle)) + borderLeftTop.y + borderHeight/2);

            points.add(new Point2D(x, y));
        }

        return new GeometryApproximation(points);
    }
}
