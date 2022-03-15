package com.orm2_graph_library.core;

import java.awt.*;
import java.util.ArrayList;

public abstract class Shape {
    abstract protected GeometryApproximation _geometryApproximation(Point borderLeftTop, int borderWidth, int borderHeight);
}
