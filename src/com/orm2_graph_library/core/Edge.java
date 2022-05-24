package com.orm2_graph_library.core;

import com.orm2_graph_library.utils.Point2D;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.stream.Stream;

public abstract class Edge <B extends DiagramElement, E extends DiagramElement> extends DiagramElement {
    // ================ ATTRIBUTES ================
    // Does we need to calculate
    protected AnchorPoint<B> _beginAnchorPoint;
    protected AnchorPoint<E> _endAnchorPoint;

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public Edge(@NotNull AnchorPoint<B> beginAnchorPoint, @NotNull AnchorPoint<E> endAnchorPoint) {
        this._beginAnchorPoint = beginAnchorPoint;
        this._endAnchorPoint   = endAnchorPoint;
    }

    // ---------------- attributes ----------------
    public B begin() { return this._beginAnchorPoint._owner; }
    public E end()   { return this._endAnchorPoint._owner; }

    public AnchorPoint<B> beginAnchorPoint() { return this._beginAnchorPoint; }
    public AnchorPoint<E> endAnchorPoint()   { return this._endAnchorPoint; }

    void _setBeginAnchorPoint(@NotNull AnchorPoint<B> beginAnchorPoint) { this._beginAnchorPoint = beginAnchorPoint; }
    void _setEndAnchorPoint(@NotNull AnchorPoint<E> endAnchorPoint)     { this._endAnchorPoint = endAnchorPoint; }

    // TODO - @add :: Getting starting and ending points depending on attachment points of begin and end elements.
    @Override
    public GeometryApproximation geometryApproximation() {
        ArrayList<Point2D> points = new ArrayList<>();
        points.add(this._beginAnchorPoint.position());
        points.add(this._endAnchorPoint.position());

        return new GeometryApproximation(points);
    }

    // ----------------- contract -----------------
    @Override
    public <T extends DiagramElement> Stream<T> getIncidentElements(Class<T> elementType) {
        Stream<T> result = Stream.of();
        if (elementType.isAssignableFrom(this.begin().getClass())) { result = Stream.concat(result, Stream.of((T)this.begin())); }
        if (elementType.isAssignableFrom(this.end().getClass()))   { result = Stream.concat(result, Stream.of((T)this.end())); }

        return result;
    }

    public boolean isSameTo(@NotNull Edge other)     { return (this._beginAnchorPoint.owner() == other._beginAnchorPoint.owner() && this._endAnchorPoint.owner() == other._endAnchorPoint  .owner()); }
    public boolean isOppositeTo(@NotNull Edge other) { return (this._beginAnchorPoint.owner() == other._endAnchorPoint  .owner() && this._endAnchorPoint.owner() == other._beginAnchorPoint.owner()); }
}
