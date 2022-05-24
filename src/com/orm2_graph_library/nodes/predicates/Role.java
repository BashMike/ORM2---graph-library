package com.orm2_graph_library.nodes.predicates;

import com.orm2_graph_library.anchor_points.AnchorPosition;
import com.orm2_graph_library.anchor_points.NodeAnchorPoint;
import com.orm2_graph_library.anchor_points.RoleAnchorPoint;
import com.orm2_graph_library.core.*;
import com.orm2_graph_library.nodes.common.EntityType;
import com.orm2_graph_library.nodes_shapes.RectangleShape;
import com.orm2_graph_library.utils.Point2D;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.stream.Stream;

// TODO - @modify   :: Should ROLE calculate its geometry position with geometry of its parent?
//      # @question :: How INNER PREDICATE can be inherited from PREDICATE if its geometry must calculate its position by parent and
//                     STANDALONE PREDICATE don't have to?
//      # @idea     :: We can: 1) re-calculate position with parent (current decision); 2) calculate position by parent.

public class Role extends Node {
    // ================ ATTRIBUTES ================
    private String          _text = null;
    private final Predicate _ownerPredicate;
    private final int       _indexInPredicate;

    protected int _borderWidth  = -1;
    protected int _borderHeight = -1;

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    Role(@NotNull Predicate predicate, int indexInPredicate) {
        this._ownerPredicate   = predicate;
        this._indexInPredicate = indexInPredicate;

        this._shape = new RectangleShape();
    }

    @Override protected void _initSelf() {}
    @Override protected void _finalizeSelf() {}

    // ----------------- connection -----------------
    public Predicate ownerPredicate() { return this._ownerPredicate; }

    // ----------------- attributes -----------------
    // * Personal
    public String text() {
        if (this._text != null) {
            return this._text;
        }
        else {
            throw new RuntimeException("ERROR :: attempt to return text of role without text.");
        }
    }

    public boolean hasText() { return (this._text != null); }

    public void setText(String text) { this._ownerDiagramActionManager().executeAction(new RoleTextChangeAction(this._ownerDiagram, this, this._text, text)); }

    public int indexInPredicate() { return this._indexInPredicate; }

    // * Anchor points
    public boolean hasAnchorPoint(AnchorPosition anchorPosition) {
        boolean result = false;

        if (this._ownerPredicate.orientation() == DiagramElement.Orientation.HORIZONTAL) {
            if (this._ownerPredicate.arity() == 1) {
                result = (anchorPosition == AnchorPosition.LEFT || anchorPosition == AnchorPosition.RIGHT);
            }
            else if (this._ownerPredicate.arity() == 2) {
                if      (this.indexInPredicate() == 0) { result = (anchorPosition == AnchorPosition.LEFT); }
                else if (this.indexInPredicate() == 1) { result = (anchorPosition == AnchorPosition.RIGHT); }
            }
            else if (this._ownerPredicate.arity() > 2) {
                if      (this.indexInPredicate() == 0)                              { result = (anchorPosition == AnchorPosition.LEFT); }
                else if (this.indexInPredicate() == this._ownerPredicate.arity()-1) { result = (anchorPosition == AnchorPosition.RIGHT); }
                else                                                                { result = (anchorPosition == AnchorPosition.UP || anchorPosition == AnchorPosition.DOWN); }
            }
        }
        else if (this._ownerPredicate.orientation() == DiagramElement.Orientation.VERTICAL) {
            if (this._ownerPredicate.arity() == 1) {
                result = (anchorPosition == AnchorPosition.UP || anchorPosition == AnchorPosition.DOWN);
            }
            else if (this._ownerPredicate.arity() == 2) {
                if      (this.indexInPredicate() == 0) { result = (anchorPosition == AnchorPosition.UP); }
                else if (this.indexInPredicate() == 1) { result = (anchorPosition == AnchorPosition.DOWN); }
            }
            else if (this._ownerPredicate.arity() > 2) {
                if      (this.indexInPredicate() == 0)                              { result = (anchorPosition == AnchorPosition.UP); }
                else if (this.indexInPredicate() == this._ownerPredicate.arity()-1) { result = (anchorPosition == AnchorPosition.DOWN); }
                else                                                                { result = (anchorPosition == AnchorPosition.LEFT || anchorPosition == AnchorPosition.RIGHT); }
            }
        }

        return result;
    }

    public AnchorPoint<Role> anchorPoint(AnchorPosition anchorPosition) {
        if (!this.hasAnchorPoint(anchorPosition)) {
            throw new RuntimeException("ERROR :: Try to get non-existent anchor point for role at given anchor position.");
        }

        return new RoleAnchorPoint(this, anchorPosition);
    }

    @Override public Stream<AnchorPoint> anchorPoints() {
        Stream<AnchorPoint> result = Stream.of();
        if (this.hasAnchorPoint(AnchorPosition.UP))    { result = Stream.concat(result, Stream.of(this.anchorPoint(AnchorPosition.UP))); }
        if (this.hasAnchorPoint(AnchorPosition.DOWN))  { result = Stream.concat(result, Stream.of(this.anchorPoint(AnchorPosition.DOWN))); }
        if (this.hasAnchorPoint(AnchorPosition.RIGHT)) { result = Stream.concat(result, Stream.of(this.anchorPoint(AnchorPosition.RIGHT))); }
        if (this.hasAnchorPoint(AnchorPosition.LEFT))  { result = Stream.concat(result, Stream.of(this.anchorPoint(AnchorPosition.LEFT))); }

        return result;
    }

    // * Geometry
    void setBorderSize(int borderWidth, int borderHeight) {
        this._borderWidth  = borderWidth;
        this._borderHeight = borderHeight;
    }

    void _moveTo(Point leftTop)          { this._leftTop.move(leftTop.x, leftTop.y); }
    void _moveBy(int shiftX, int shiftY) { this._leftTop.translate(shiftX, shiftY); }

    @Override
    public Point2D borderLeftTop() {
        Point2D result = this._ownerPredicate.borderLeftTop();
        int shiftX = this.borderWidth()  * (this._ownerPredicate.orientation() == DiagramElement.Orientation.HORIZONTAL ? this._indexInPredicate : 0);
        int shiftY = this.borderHeight() * (this._ownerPredicate.orientation() == DiagramElement.Orientation.VERTICAL   ? this._indexInPredicate : 0);
        result.translate(shiftX, shiftY);

        return new Point2D(result);
    }

    @Override
    public int borderWidth() {
        if (this._ownerPredicate.orientation() == DiagramElement.Orientation.HORIZONTAL)    { return this._borderWidth; }
        else if (this._ownerPredicate.orientation() == DiagramElement.Orientation.VERTICAL) { return this._borderHeight; }

        assert false : "ASSERT :: Try to get border width with invalid orientation.";
        return -1;
    }

    @Override
    public int borderHeight() {
        if (this._ownerPredicate.orientation() == DiagramElement.Orientation.HORIZONTAL)    { return this._borderHeight; }
        else if (this._ownerPredicate.orientation() == DiagramElement.Orientation.VERTICAL) { return this._borderWidth; }

        assert false : "ASSERT :: Try to get border height with invalid orientation.";
        return -1;
    }

    // ================= SUBTYPES =================
    public class RoleTextChangeAction extends Diagram.DiagramElementAttributeChangeAction {
        private RoleTextChangeAction(Diagram diagram, Role role, String oldText, String newText) {
            super(diagram, role, oldText, newText);
        }

        public Role role() { return (Role)this._diagramElement; }
        public String newText() { return (String)this._newAttributeValue; }
        public String oldText() { return (String)this._oldAttributeValue; }

        @Override protected void _execute() { ((Role)this._diagramElement)._text = (String)this._newAttributeValue; }
        @Override protected void _undo()    { ((Role)this._diagramElement)._text = (String)this._oldAttributeValue; }
    }
}
