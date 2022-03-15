package com.orm2_graph_library.nodes.predicates;

import com.orm2_graph_library.core.Action;
import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.DiagramElement;
import com.orm2_graph_library.core.Node;
import com.orm2_graph_library.nodes_shapes.RectangleShape;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;

public abstract class Predicate extends Node {
    // ================ ATTRIBUTES ================
    protected ArrayList<Role>    _roles       = new ArrayList<>();
    private DiagramElement.Orientation _orientation = DiagramElement.Orientation.HORIZONTAL;

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public Predicate(int arity) {
        if (arity <= 0) {
            throw new RuntimeException("ERROR :: attempt to create predicate with non-positive count of roles.");
        }

        for (int i=0; i<arity; i++) { this._roles.add(new Role(this, i)); }

        this._shape = new RectangleShape();
    }

    Predicate(ArrayList<Role> roles) { this._roles.addAll(roles); }

    // ---------------- attributes ----------------
    public ArrayList<Role> roles() { return new ArrayList<>(this._roles); }
    public DiagramElement.Orientation orientation() { return this._orientation; }

    @Override public int borderWidth()  { return this._roles.get(0).borderWidth()  * (this._orientation == DiagramElement.Orientation.HORIZONTAL ? this._roles.size() : 1); }
    @Override public int borderHeight() { return this._roles.get(0).borderHeight() * (this._orientation == DiagramElement.Orientation.VERTICAL   ? this._roles.size() : 1); }

    // TODO - @modify :: Width and height depending on orientation.
    public void setOrientation(DiagramElement.Orientation orientation) {
        if (!this._orientation.equals(orientation)) {
            this._owner._actionManager().executeAction(new ChangePredicateOrientationAction(this._owner, this, this._orientation, orientation));
        }
    }

    // ================= SUBTYPES =================
    private class ChangePredicateOrientationAction extends Action {
        private final Predicate            _node;
        private final DiagramElement.Orientation _oldOrientation;
        private final DiagramElement.Orientation _newOrientation;

        public ChangePredicateOrientationAction(@NotNull Diagram diagram, @NotNull Predicate node, @NotNull DiagramElement.Orientation oldOrientation, @NotNull DiagramElement.Orientation newOrientation) {
            super(diagram);

            this._node           = node;
            this._oldOrientation = oldOrientation;
            this._newOrientation = newOrientation;
        }

        @Override public void _execute() { this._changeOrientationOfNodesAndItsComponents(this._newOrientation); }
        @Override public void _undo()    { this._changeOrientationOfNodesAndItsComponents(this._oldOrientation); }

        private void _changeOrientationOfNodesAndItsComponents(DiagramElement.Orientation orientation) {
            this._node._orientation = orientation;

            for (int i=0; i<this._node._roles.size(); i++) {
                this._node._roles.get(i)._setOrientation(orientation);

                if (orientation == DiagramElement.Orientation.VERTICAL) {
                    int roleHeight = this._node._roles.get(i).borderHeight();
                    this._node._roles.get(i)._moveTo(new Point(this._node._leftTop.x, this._node._leftTop.y + i*roleHeight));
                }
                else if (orientation == DiagramElement.Orientation.HORIZONTAL) {
                    int roleWidth = this._node._roles.get(i).borderWidth();
                    this._node._roles.get(i)._moveTo(new Point(this._node._leftTop.x + i*roleWidth, this._node._leftTop.y));
                }
            }
        }
    }
}
