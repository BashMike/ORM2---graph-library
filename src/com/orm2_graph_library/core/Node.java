package com.orm2_graph_library.core;

import com.orm2_graph_library.attributes.IntegerAttribute;

import java.awt.*;
import java.util.stream.Stream;

public abstract class Node extends DiagramElement {
    // ================ ATTRIBUTES ================
    private Point _leftTop = null;

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public Node() {}

    // ---------------- attributes ----------------
    public void moveTo(int x, int y)           { this._leftTop = new Point(x, y); }
    public void moveBy(int shiftX, int shiftY) { this._leftTop.translate(shiftX, shiftY); }

    // ---------------- connection ----------------
    @Override
    void setOwner(Diagram owner) {
        super.setOwner(owner);
        this._leftTop = new Point(0, 0);
    }

    // ----------------- contract -----------------
    @Override
    public Stream<? extends Edge> getIncidentElements(Class<? extends DiagramElement> elementType) {
        return null;
    }
}
