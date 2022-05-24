package com.orm2_graph_library.core;

import com.orm2_graph_library.utils.Point2D;

import java.awt.*;
import java.util.ArrayList;

public abstract class Shape {
    abstract protected GeometryApproximation _geometryApproximation(Point2D borderLeftTop, int borderWidth, int borderHeight);
}
