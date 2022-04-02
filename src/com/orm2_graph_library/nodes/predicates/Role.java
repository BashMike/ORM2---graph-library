package com.orm2_graph_library.nodes.predicates;

import com.orm2_graph_library.anchor_points.AnchorPosition;
import com.orm2_graph_library.anchor_points.NodeAnchorPoint;
import com.orm2_graph_library.anchor_points.RoleAnchorPoint;
import com.orm2_graph_library.core.*;
import com.orm2_graph_library.nodes.common.EntityType;
import com.orm2_graph_library.nodes_shapes.RectangleShape;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

// TODO - @modify   :: Should ROLE calculate its geometry position with geometry of its parent?
//      # @question :: How INNER PREDICATE can be inherited from PREDICATE if its geometry must calculate its position by parent and
//                     STANDALONE PREDICATE don't have to?
//      # @idea     :: We can: 1) re-calculate position with parent (current decision); 2) calculate position by parent.

public class Role extends Node {
    // ================ ATTRIBUTES ================
    private DiagramElement.Orientation _orientation;
    private String          _text; // TODO - @modify :: Change name of role's text.
    private final Predicate _ownerPredicate;
    private final int       _indexInPredicate;

    protected int _borderWidth  = -1;
    protected int _borderHeight = -1;

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    Role(@NotNull Predicate predicate, int indexInPredicate) {
        this._ownerPredicate   = predicate;
        this._orientation      = predicate.orientation();
        this._indexInPredicate = indexInPredicate;

        this._shape = new RectangleShape();
    }

    @Override
    protected void _initSelf() {}

    // ----------------- attributes -----------------
    public String text() { return this._text; }
    public void setText(String text) {
        this._ownerDiagramActionManager().executeAction(new RoleTextChangeAction(this._ownerDiagram, this, this._text, text));
    }

    public Predicate ownerPredicate() { return this._ownerPredicate; }

    public boolean hasAnchorPoint(AnchorPosition anchorPosition)        {
        boolean result = false;

        if (this._orientation == DiagramElement.Orientation.HORIZONTAL) {
            if (this._ownerPredicate.arity() == 1) {
                result = (anchorPosition == AnchorPosition.LEFT || anchorPosition == AnchorPosition.RIGHT);
            }
            else if (this._ownerPredicate.arity() == 2) {
                if      (this == this._ownerPredicate.getRole(0)) { result = (anchorPosition == AnchorPosition.LEFT); }
                else if (this == this._ownerPredicate.getRole(1)) { result = (anchorPosition == AnchorPosition.RIGHT); }
            }
            else if (this._ownerPredicate.arity() > 2) {
                if (this == this._ownerPredicate.getRole(0))                                   { result = (anchorPosition == AnchorPosition.LEFT); }
                else if (this == this._ownerPredicate.getRole(this._ownerPredicate.arity()-1)) { result = (anchorPosition == AnchorPosition.RIGHT); }
                else                                                                           { result = (anchorPosition == AnchorPosition.UP || anchorPosition == AnchorPosition.DOWN); }
            }
        }
        else if (this._orientation == DiagramElement.Orientation.VERTICAL) {
            if (this._ownerPredicate.arity() == 1) {
                result = (anchorPosition == AnchorPosition.UP || anchorPosition == AnchorPosition.DOWN);
            }
            else if (this._ownerPredicate.arity() == 2) {
                if      (this == this._ownerPredicate.getRole(0)) { result = (anchorPosition == AnchorPosition.UP); }
                else if (this == this._ownerPredicate.getRole(1)) { result = (anchorPosition == AnchorPosition.DOWN); }
            }
            else if (this._ownerPredicate.arity() > 2) {
                if (this == this._ownerPredicate.getRole(0))                                   { result = (anchorPosition == AnchorPosition.UP); }
                else if (this == this._ownerPredicate.getRole(this._ownerPredicate.arity()-1)) { result = (anchorPosition == AnchorPosition.DOWN); }
                else                                                                           { result = (anchorPosition == AnchorPosition.LEFT || anchorPosition == AnchorPosition.RIGHT); }
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

    @Override
    public Point borderLeftTop() {
        Point result = this._ownerPredicate.borderLeftTop();
        int shiftX = this.borderWidth()  * (this._ownerPredicate.orientation() == DiagramElement.Orientation.HORIZONTAL ? this._indexInPredicate : 0);
        int shiftY = this.borderHeight() * (this._ownerPredicate.orientation() == DiagramElement.Orientation.VERTICAL   ? this._indexInPredicate : 0);
        result.translate(shiftX, shiftY);

        return new Point(result.x, result.y);
    }

    @Override
    public int borderWidth() {
        if (this._orientation == DiagramElement.Orientation.HORIZONTAL)    { return this._borderWidth; }
        else if (this._orientation == DiagramElement.Orientation.VERTICAL) { return this._borderHeight; }

        assert false : "ASSERT :: Try to get border width with invalid orientation.";
        return -1;
    }

    @Override
    public int borderHeight() {
        if (this._orientation == DiagramElement.Orientation.HORIZONTAL)    { return this._borderHeight; }
        else if (this._orientation == DiagramElement.Orientation.VERTICAL) { return this._borderWidth; }

        assert false : "ASSERT :: Try to get border height with invalid orientation.";
        return -1;
    }

    void setBorderSize(int borderWidth, int borderHeight) {
        this._borderWidth  = borderWidth;
        this._borderHeight = borderHeight;
    }

    // TODO - @modify :: Width and height depending on orientation.
    void _setOrientation(DiagramElement.Orientation orientation) { this._orientation = orientation; }

    void _moveTo(Point leftTop)          { this._leftTop.move(leftTop.x, leftTop.y); }
    void _moveBy(int shiftX, int shiftY) { this._leftTop.translate(shiftX, shiftY); }

    // ================= SUBTYPES =================
    class RoleTextChangeAction extends Action {
        final private Role   _role;
        final private String _oldText;
        final private String _newText;

        public RoleTextChangeAction(Diagram diagram, Role role, String oldText, String newText) {
            super(diagram);

            this._role    = role;
            this._oldText = oldText;
            this._newText = newText;
        }

        @Override protected void _execute() { this._role._text = this._newText; }
        @Override protected void _undo()    { this._role._text = this._oldText; }
    }
}
