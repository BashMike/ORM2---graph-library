package com.orm2_graph_library.core;

import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Node extends DiagramElement {
    // ================ ATTRIBUTES ================
    protected Geometry _geometry = null;

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public Node() {}

    // ---------------- connection ----------------
    @Override
    void setOwner(Diagram owner) {
        super.setOwner(owner);
    }

    // ---------------- attributes ----------------
    public Geometry geometry() { return this._geometry; }

    // ----------------- contract -----------------
    @Override
    public <T extends DiagramElement> ArrayList<T> getIncidentElements(Class<T> elementType) {
        ArrayList<T> result = new ArrayList<>();

        if (Edge.class.isAssignableFrom(elementType)) {
            for (Edge edge : this._owner.getElements(Edge.class).collect(Collectors.toCollection(ArrayList::new))) {
                if (elementType.isAssignableFrom(edge.getClass()) && (edge.begin() == this || edge.end() == this)) {
                    result.add((T)edge);
                }
            }
        }
        else if (Node.class.isAssignableFrom(elementType)) {
            for (Edge edge : this._owner.getElements(Edge.class).collect(Collectors.toCollection(ArrayList::new))) {
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
