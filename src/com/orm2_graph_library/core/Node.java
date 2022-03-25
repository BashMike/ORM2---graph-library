package com.orm2_graph_library.core;

import com.orm2_graph_library.edges.SubtypingRelationEdge;
import com.orm2_graph_library.nodes.predicates.Predicate;
import com.orm2_graph_library.nodes.predicates.RolesSequence;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            result = this._ownerDiagram.getElements(elementType)
                    .filter(e -> ((Edge)e).begin() == this || ((Edge)e).end() == this)
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        else if (Node.class.isAssignableFrom(elementType)) {
            for (Edge edge : this._ownerDiagram.getElements(Edge.class).collect(Collectors.toCollection(ArrayList::new))) {
                if (edge.beginAnchorPoint().owner() == this && elementType.isAssignableFrom(edge.endAnchorPoint().owner().getClass())) {
                    result.add((T)edge.end());
                }
                if (edge.endAnchorPoint().owner() == this && elementType.isAssignableFrom(edge.beginAnchorPoint().getClass())) {
                    result.add((T)edge.begin());
                }
            }
        }

        return result;
    }
}
