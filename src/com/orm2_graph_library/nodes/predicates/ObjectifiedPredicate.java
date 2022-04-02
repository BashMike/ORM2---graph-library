package com.orm2_graph_library.nodes.predicates;

import com.orm2_graph_library.anchor_points.AnchorPosition;
import com.orm2_graph_library.anchor_points.NodeAnchorPoint;
import com.orm2_graph_library.core.AnchorPoint;
import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.DiagramElement;
import com.orm2_graph_library.nodes_shapes.RoundedRectangleShape;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

// TODO - @add :: Return to standalone predicate.

public class ObjectifiedPredicate extends RoleParticipant {
    // ================ ATTRIBUTES ================
    final private Predicate _innerPredicate;
    private int _horizontalGapDistance;
    private int _verticalGapDistance;
    private int _borderRoundingDegree;

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public ObjectifiedPredicate(Predicate innerPredicate) {
        this._innerPredicate = innerPredicate;
        this._innerPredicate.setOwnerObjectifiedPredicate(this);
    }

    // ---------------- connection ----------------
    @Override
    protected void _initSelf() {}

    // ---------------- attributes ----------------
    public Predicate innerPredicate() { return this._innerPredicate; }

    public AnchorPoint<ObjectifiedPredicate> upAnchorPoint()    { return new NodeAnchorPoint<>(this, AnchorPosition.UP); }
    public AnchorPoint<ObjectifiedPredicate> downAnchorPoint()  { return new NodeAnchorPoint<>(this, AnchorPosition.DOWN); }
    public AnchorPoint<ObjectifiedPredicate> rightAnchorPoint() { return new NodeAnchorPoint<>(this, AnchorPosition.RIGHT); }
    public AnchorPoint<ObjectifiedPredicate> leftAnchorPoint()  { return new NodeAnchorPoint<>(this, AnchorPosition.LEFT); }

    public void setGapsDistances(int horizontalGapDistance, int verticalGapDistance) {
        this._horizontalGapDistance = horizontalGapDistance;
        this._verticalGapDistance   = verticalGapDistance;
    }

    @NotNull
    public void setBorderRoundingDegree(int borderRoundingDegree) {
        this._shape = new RoundedRectangleShape(borderRoundingDegree);
    }

    @Override
    public Point borderLeftTop() {
        int x = this._innerPredicate.borderLeftTop().x - this._horizontalGapDistance;
        int y = this._innerPredicate.borderLeftTop().y - this._verticalGapDistance;

        return new Point(x, y);
    }

    @Override
    public int borderWidth() {
        if (this._innerPredicate.orientation() == DiagramElement.Orientation.HORIZONTAL) {
            return this._horizontalGapDistance * 2 + this._innerPredicate.getRole(0).borderWidth() * this._innerPredicate.arity();
        }
        else if (this._innerPredicate.orientation() == DiagramElement.Orientation.VERTICAL) {
            return this._horizontalGapDistance * 2 + this._innerPredicate.getRole(0).borderWidth();
        }

        assert false : "ASSERT :: Try to get border height with invalid orientation of inner predicate.";
        return -1;
    }

    @Override
    public int borderHeight() {
        if (this._innerPredicate.orientation() == DiagramElement.Orientation.HORIZONTAL) {
            return this._verticalGapDistance * 2 + this._innerPredicate.getRole(0).borderHeight();
        }
        else if (this._innerPredicate.orientation() == DiagramElement.Orientation.VERTICAL) {
            return this._verticalGapDistance * 2 + this._innerPredicate.getRole(0).borderHeight() * this._innerPredicate.arity();
        }

        assert false : "ASSERT :: Try to get border height with invalid orientation of inner predicate.";
        return -1;
    }


    // ----------------- contract -----------------
    @Override
    public <T extends DiagramElement> ArrayList<T> getIncidentElements(Class<T> elementType) {
        ArrayList<T> result = super.getIncidentElements(elementType);
        result.addAll(this._innerPredicate.getIncidentElements(elementType));

        return result;
    }
}
