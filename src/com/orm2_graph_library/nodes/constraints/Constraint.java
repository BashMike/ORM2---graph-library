package com.orm2_graph_library.nodes.constraints;

import com.orm2_graph_library.anchor_points.AnchorPosition;
import com.orm2_graph_library.anchor_points.NodeAnchorPoint;
import com.orm2_graph_library.core.*;
import com.orm2_graph_library.nodes.predicates.ObjectifiedPredicate;
import com.orm2_graph_library.nodes.predicates.Predicate;
import com.orm2_graph_library.nodes_shapes.EllipseShape;

import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

public abstract class Constraint extends Node implements Movable {
    // ================ ATTRIBUTES ================
    protected int _borderWidth  = -1;
    protected int _borderHeight = -1;

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public Constraint() { this._shape = new EllipseShape(); }

    // ---------------- connection ----------------
    @Override
    protected void _initSelf() {}

    // ---------------- attributes ----------------
    public AnchorPoint<Constraint> centerAnchorPoint() { return new NodeAnchorPoint<>(this, AnchorPosition.CENTER); }

    @Override public void moveTo(Point leftTop) { this._leftTop.move(leftTop.x, leftTop.y); }
    @Override public void moveBy(int shiftX, int shiftY) { this._leftTop.translate(shiftX, shiftY); }

    @Override public int borderWidth()  { return this._borderWidth; }
    @Override public int borderHeight() { return this._borderHeight; }

    public void setBorderSize(int borderSize) {
        this._borderWidth  = borderSize;
        this._borderHeight = borderSize;
    }

    // ----------------- contract -----------------
    @Override
    public <T extends DiagramElement> ArrayList<T> getIncidentElements(Class<T> elementType) {
        ArrayList<T> result = super.getIncidentElements(elementType);

        if (Predicate.class.isAssignableFrom(elementType)) {
            result.addAll(this._ownerDiagram.getElements(elementType)
                    .filter(e -> e.isIncidentElement(this))
                    .collect(Collectors.toCollection(ArrayList::new)));
        }
        else if (ObjectifiedPredicate.class.isAssignableFrom(elementType)) {
            result.addAll(this._ownerDiagram.getElements(elementType)
                    .filter(e -> e.isIncidentElement(this))
                    .collect(Collectors.toCollection(ArrayList::new)));
        }

        return result;
    }
}
