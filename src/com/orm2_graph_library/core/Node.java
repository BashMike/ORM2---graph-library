package com.orm2_graph_library.core;

import com.orm2_graph_library.utils.Point2D;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Node extends DiagramElement {
    // ================ ATTRIBUTES ================
    protected Shape _shape   = null;
    protected Point2D _leftTop = new Point2D(0, 0);

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public Node() {}

    // ---------------- attributes ----------------
    // * Geometry
    public Point2D borderLeftTop()     { return new Point2D(this._leftTop.x, this._leftTop.y); }
    public Point2D borderLeftBottom()  { return new Point2D(this.borderLeftTop().x,                      this.borderLeftTop().y + this.borderHeight()); }
    public Point2D borderRightTop()    { return new Point2D(this.borderLeftTop().x + this.borderWidth(), this.borderLeftTop().y); }
    public Point2D borderRightBottom() { return new Point2D(this.borderLeftTop().x + this.borderWidth(), this.borderLeftTop().y + this.borderHeight()); }

    abstract public int borderWidth();
    abstract public int borderHeight();

    @NotNull
    public Shape shape() { return this._shape; }
    @Override
    public GeometryApproximation geometryApproximation() { return this._shape._geometryApproximation(this.borderLeftTop(), this.borderWidth(), this.borderHeight()); }

    // ----------------- contract -----------------
    @Override
    public <T extends DiagramElement> Stream<T> getIncidentElements(Class<T> elementType) {
        Stream<T> result = Stream.of();

        if (Edge.class.isAssignableFrom(elementType)) {
            result = this._ownerDiagram.getElements(elementType)
                    .filter(e -> ((Edge)e).begin() == this || ((Edge)e).end() == this);
        }
        else if (Node.class.isAssignableFrom(elementType)) {
            for (Edge edge : this._ownerDiagram.getElements(Edge.class).collect(Collectors.toCollection(ArrayList::new))) {
                if (edge.beginAnchorPoint().owner() == this && elementType.isAssignableFrom(edge.endAnchorPoint().owner().getClass())) {
                    result = Stream.concat(result, Stream.of((T)edge.end()));
                }
                if (edge.endAnchorPoint().owner() == this && elementType.isAssignableFrom(edge.beginAnchorPoint().getClass())) {
                    result = Stream.concat(result, Stream.of((T)edge.begin()));
                }
            }
        }

        return result;
    }
}
