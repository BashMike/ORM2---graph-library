package com.orm2_graph_library.core;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;

public abstract class Edge <B extends DiagramElement, E extends DiagramElement> extends DiagramElement {
    // ================ ATTRIBUTES ================
    private final AnchorPoint<B> _beginAnchorPoint;
    private final AnchorPoint<E> _endAnchorPoint;

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public Edge(@NotNull AnchorPoint<B> beginAnchorPoint, @NotNull AnchorPoint<E> endAnchorPoint) {
        this._beginAnchorPoint = beginAnchorPoint;
        this._endAnchorPoint   = endAnchorPoint;
    }

    // ---------------- attributes ----------------
    public AnchorPoint<B> beginAnchorPoint() { return this._beginAnchorPoint; }
    public AnchorPoint<E> endAnchorPoint()   { return this._endAnchorPoint; }

    // TODO - @add :: Getting starting and ending points depending on attachment points of begin and end elements.
    @Override
    public GeometryApproximation geometryApproximation() {
        ArrayList<Point> points = new ArrayList<>();
        points.add(this._beginAnchorPoint.position());
        points.add(this._endAnchorPoint.position());

        return new GeometryApproximation(points);
    }

    // ----------------- contract -----------------
    @Override
    public <T extends DiagramElement> ArrayList<T> getIncidentElements(Class<T> elementType) {
        ArrayList<T> result = new ArrayList<>();
        assert false : "TODO";
        return result;
    }
}
