package com.orm2_graph_library.nodes.constraints;

import com.orm2_graph_library.anchor_points.AnchorPosition;
import com.orm2_graph_library.anchor_points.NodeAnchorPoint;
import com.orm2_graph_library.core.*;
import com.orm2_graph_library.nodes.predicates.ObjectifiedPredicate;
import com.orm2_graph_library.nodes.predicates.Predicate;
import com.orm2_graph_library.nodes.predicates.RolesSequence;
import com.orm2_graph_library.nodes_shapes.EllipseShape;
import com.orm2_graph_library.utils.Point2D;

import java.awt.*;
import java.util.stream.Stream;

public abstract class Constraint extends Node implements Movable {
    // ================ ATTRIBUTES ================
    protected int _borderWidth  = -1;
    protected int _borderHeight = -1;

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public Constraint() { this._shape = new EllipseShape(); }

    // ---------------- connection ----------------
    @Override protected void _initSelf() {}
    @Override protected void _finalizeSelf() {}

    // ---------------- attributes ----------------
    public AnchorPoint<Constraint> centerAnchorPoint() { return new NodeAnchorPoint<>(this, AnchorPosition.CENTER); }

    @Override public Stream<AnchorPoint> anchorPoints() { return Stream.of(this.centerAnchorPoint()); }

    @Override public void moveTo(Point2D leftTop) { this._leftTop.move(leftTop.x, leftTop.y); }
    @Override public void moveBy(int shiftX, int shiftY) { this._leftTop.translate(shiftX, shiftY); }

    @Override public int borderWidth()  { return this._borderWidth; }
    @Override public int borderHeight() { return this._borderHeight; }

    public void setBorderSize(int borderSize) {
        this._borderWidth  = borderSize;
        this._borderHeight = borderSize;
    }

    abstract public int minRequiredConnectionsCount();
    abstract public int maxRequiredConnectionsCount(); // -1 means that constraint can have infinity number of connections

    // ----------------- contract -----------------
    @Override
    public <T extends DiagramElement> Stream<T> getIncidentElements(Class<T> elementType) {
        Stream<T> result = super.getIncidentElements(elementType);

        if (Predicate.class.isAssignableFrom(elementType)) {
            result = Stream.concat(result, this._ownerDiagram.getElements(elementType)
                    .filter(e -> e.isIncidentElement(this)));
        }
        else if (ObjectifiedPredicate.class.isAssignableFrom(elementType)) {
            result = Stream.concat(result, this._ownerDiagram.getElements(elementType)
                    .filter(e -> e.isIncidentElement(this)));
        }
        else if (RolesSequence.class.isAssignableFrom(elementType)) {
            result = Stream.concat(result, this._ownerDiagram.getElements(elementType)
                    .filter(e -> e.isIncidentElement(this)));
        }

        return result;
    }
}
