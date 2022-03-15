package com.orm2_graph_library.nodes.constraints;

import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.Movable;
import com.orm2_graph_library.core.Node;
import com.orm2_graph_library.nodes_shapes.EllipseShape;

import java.awt.*;

public abstract class Constraint extends Node implements Movable {
    // ================== STATIC ==================
    static private int _size = 40;

    static public int size()             { return Constraint._size; }
    static public void setSize(int size) { Constraint._size = size; }

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public Constraint() { this._shape = new EllipseShape(); }

    // ---------------- connection ----------------
    @Override
    protected void _initSelf(Diagram owner) {}

    // ---------------- attributes ----------------
    @Override
    public int borderWidth() { return Constraint.size(); }
    @Override
    public int borderHeight() { return Constraint.size(); }

    @Override public void moveTo(Point leftTop) { this._leftTop.move(leftTop.x, leftTop.y); }
    @Override public void moveBy(int shiftX, int shiftY) { this._leftTop.translate(shiftX, shiftY); }
}
