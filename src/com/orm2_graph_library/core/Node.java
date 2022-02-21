package com.orm2_graph_library.core;

import com.orm2_graph_library.attributes.IntegerAttribute;

import java.awt.*;
import java.util.ArrayList;
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
    public <T extends DiagramElement> ArrayList<T> getIncidentElements(Class<T> elementType) {
        ArrayList<T> result = new ArrayList<>();

        if (Edge.class.isAssignableFrom(elementType)) {
            for (Edge edge : this._owner.getElements(Edge.class)) {
                if (elementType.isAssignableFrom(edge.getClass()) && (edge.begin() == this || edge.end() == this)) {
                    result.add((T)edge);
                }
            }
        }
        else if (Node.class.isAssignableFrom(elementType)) {
            for (Edge edge : this._owner.getElements(Edge.class)) {
                if (edge.begin() == this && elementType.isAssignableFrom(edge.end().getClass())) {
                    result.add((T)edge.end());
                }
                if (edge.end() == this && elementType.isAssignableFrom(edge.begin().getClass())) {
                    result.add((T)edge.begin());
                }
            }
        }

        return result;
    }
}
