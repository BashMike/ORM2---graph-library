package com.orm2_graph_library.nodes_shapes;

import com.orm2_graph_library.core.GeometryApproximation;
import com.orm2_graph_library.core.Shape;

import java.awt.*;
import java.util.ArrayList;

public class RoundedRectangleShape extends Shape {
    @Override
    protected GeometryApproximation _geometryApproximation(Point borderLeftTop, int borderWidth, int borderHeight) {
        int arcPointsNum = 5;
        int radius       = 15;
        ArrayList<Point> points = new ArrayList<>();

        if (radius != 0) {
            points.add(new Point(borderLeftTop.x, borderLeftTop.y + radius));
            for (double angle=Math.PI + 0.5*Math.PI/arcPointsNum; angle<1.5*Math.PI; angle+=0.5*Math.PI/arcPointsNum) {
                int x = (int)Math.round((radius * Math.cos(angle)) + borderLeftTop.x + radius);
                int y = (int)Math.round((radius * Math.sin(angle)) + borderLeftTop.y + radius);
                points.add(new Point(x, y));
            }
            points.add(new Point(borderLeftTop.x + radius, borderLeftTop.y));

            points.add(new Point(borderLeftTop.x + borderWidth - radius, borderLeftTop.y));
            for (double angle=1.5*Math.PI + 0.5*Math.PI/arcPointsNum; angle<2.0*Math.PI; angle+=0.5*Math.PI/arcPointsNum) {
                int x = (int)Math.round((radius * Math.cos(angle)) + borderLeftTop.x + borderWidth - radius);
                int y = (int)Math.round((radius * Math.sin(angle)) + borderLeftTop.y + radius);
                points.add(new Point(x, y));
            }
            points.add(new Point(borderLeftTop.x + borderWidth, borderLeftTop.y + radius));

            points.add(new Point(borderLeftTop.x + borderWidth, borderLeftTop.y + borderHeight - radius));
            for (double angle=0.5*Math.PI/arcPointsNum; angle<0.5*Math.PI; angle+=0.5*Math.PI/arcPointsNum) {
                int x = (int)Math.round((radius * Math.cos(angle)) + borderLeftTop.x + borderWidth - radius);
                int y = (int)Math.round((radius * Math.sin(angle)) + borderLeftTop.y + borderHeight - radius);
                points.add(new Point(x, y));
            }
            points.add(new Point(borderLeftTop.x + borderWidth - radius, borderLeftTop.y + borderHeight));

            points.add(new Point(borderLeftTop.x + radius, borderLeftTop.y + borderHeight));
            for (double angle=0.5*Math.PI + 0.5*Math.PI/arcPointsNum; angle<Math.PI; angle+=0.5*Math.PI/arcPointsNum) {
                int x = (int)Math.round((radius * Math.cos(angle)) + borderLeftTop.x + radius);
                int y = (int)Math.round((radius * Math.sin(angle)) + borderLeftTop.y + borderHeight - radius);
                points.add(new Point(x, y));
            }
            points.add(new Point(borderLeftTop.x, borderLeftTop.y + borderHeight - radius));

            return new GeometryApproximation(points);
        }
        else {
            points.add(new Point(borderLeftTop.x              , borderLeftTop.y));
            points.add(new Point(borderLeftTop.x + borderWidth, borderLeftTop.y));
            points.add(new Point(borderLeftTop.x + borderWidth, borderLeftTop.y + borderHeight));
            points.add(new Point(borderLeftTop.x              , borderLeftTop.y + borderHeight));

            return new GeometryApproximation(points);
        }
    }
}
