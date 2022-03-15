package com.orm2_graph_library.core;

import com.orm2_graph_library.nodes.predicates.Predicate;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

// TODO - @add :: Add storage of all sizes and distances.
// TODO - @add :: Add SHAPE class (GEOMETRY class has information about size, position and type of diagram element's figure).
// TODO - @add :: SHAPE class must have method for getting its geometry approximation.
public abstract class Node extends DiagramElement {
    // ================ ATTRIBUTES ================
    protected Shape _shape   = null;
    protected Point _leftTop = new Point(0, 0);

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public Node() {}

    // ---------------- connection ----------------
    @Override
    void setOwner(Diagram owner) { super.setOwner(owner); }

    // ---------------- attributes ----------------
    public Point borderLeftTop()     { return new Point(this._leftTop.x, this._leftTop.y); }
    public Point borderLeftBottom()  { return new Point(this.borderLeftTop().x,                      this.borderLeftTop().y + this.borderHeight()); }
    public Point borderRightTop()    { return new Point(this.borderLeftTop().x + this.borderWidth(), this.borderLeftTop().y); }
    public Point borderRightBottom() { return new Point(this.borderLeftTop().x + this.borderWidth(), this.borderLeftTop().y + this.borderHeight()); }

    abstract public int borderWidth();
    abstract public int borderHeight();

    @NotNull
    public Shape shape() { return this._shape; }
    @Override
    public GeometryApproximation geometryApproximation() { return this._shape._geometryApproximation(this.borderLeftTop(), this.borderWidth(), this.borderHeight()); }

    // ----------------- contract -----------------
    @Override
    public <T extends DiagramElement> ArrayList<T> getIncidentElements(Class<T> elementType) {
        ArrayList<T> result = new ArrayList<>();

        if (Edge.class.isAssignableFrom(elementType)) {
            for (Edge edge : this._owner.getElements(Edge.class).collect(Collectors.toCollection(ArrayList::new))) {
                if (elementType.isAssignableFrom(edge.getClass()) && (edge.beginAnchorPoint().owner() == this || edge.endAnchorPoint().owner() == this)) {
                    result.add((T)edge);
                }
            }
        }
        else if (Node.class.isAssignableFrom(elementType)) {
            for (Edge edge : this._owner.getElements(Edge.class).collect(Collectors.toCollection(ArrayList::new))) {
                if (edge.beginAnchorPoint().owner() == this && elementType.isAssignableFrom(edge.endAnchorPoint().owner().getClass())) {
                    result.add((T)edge.endAnchorPoint().owner());
                }
                if (edge.endAnchorPoint().owner() == this && elementType.isAssignableFrom(edge.beginAnchorPoint().getClass())) {
                    result.add((T)edge.beginAnchorPoint().owner());
                }
            }
        }

        return result;
    }
}
