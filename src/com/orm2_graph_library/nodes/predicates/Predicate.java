package com.orm2_graph_library.nodes.predicates;

import com.orm2_graph_library.core.*;
import com.orm2_graph_library.nodes_shapes.RectangleShape;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Predicate extends Node {
    // ================ ATTRIBUTES ================
    protected ArrayList<Role>          _roles          = new ArrayList<>();
    private DiagramElement.Orientation _orientation    = DiagramElement.Orientation.HORIZONTAL;
    private Set<RolesSequence>         _rolesSequences = new HashSet<>();

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public Predicate(int arity) {
        if (arity <= 0) { throw new RuntimeException("ERROR :: attempt to create predicate with non-positive count of roles."); }

        for (int i=0; i<arity; i++) { this._roles.add(new Role(this, i)); }

        this._shape = new RectangleShape();
    }

    Predicate(ArrayList<Role> roles) {
        assert (roles.size() <= 0) : "ASSERT :: attempt to create predicate with non-positive count of roles.";

        this._roles.addAll(roles);
    }

    // ---------------- connection ----------------
    @Override
    protected void _initSelf(Diagram owner) {
        owner._actionManager().stopRecordingActions();
        for (Role role : this._roles) { owner.addNode(role); }
        owner._actionManager().startRecordingActions();
    }

    // ---------------- attributes ----------------
    public Role getRole(int index) { return this._roles.get(index); }
    public int arity() { return this._roles.size(); }

    public DiagramElement.Orientation orientation() { return this._orientation; }

    public RolesSequence rolesSequence(int... rolesIndexes) {
        ArrayList<Role> roles = new ArrayList<>();
        for (int rolesIndex : rolesIndexes) { roles.add(this._roles.get(rolesIndex)); }

        if (new HashSet<>(roles).size() != roles.size()) {
            throw new RuntimeException("ERROR :: Attempt to get roles sequence with duplicated roles in it.");
        }

        RolesSequence rolesSequence = new RolesSequence(roles);
        rolesSequence._initSelf(this._ownerDiagram);
        ArrayList<RolesSequence> rolesSequences = this._rolesSequences.stream()
                .filter(e -> e.equals(rolesSequence))
                .collect(Collectors.toCollection(ArrayList::new));

        if (rolesSequences.isEmpty()) {
            this._rolesSequences.add(rolesSequence);
            this._ownerDiagram.addRolesSequence(rolesSequence);

            return rolesSequence;
        }
        else {
            return rolesSequences.get(0);
        }
    }

    public ArrayList<RolesSequence> allRolesSequences() { return new ArrayList<>(this._rolesSequences); }

    public void setOrientation(DiagramElement.Orientation orientation) {
        if (!this._orientation.equals(orientation)) {
            this._ownerDiagram._actionManager().executeAction(new ChangePredicateOrientationAction(this._ownerDiagram, this, this._orientation, orientation));
        }
    }

    public void setRolesBorderSize(int borderWidth, int borderHeight) {
        for (Role role : this._roles) {
            role.setBorderSize(borderWidth, borderHeight);
        }
    }

    @Override
    public int borderWidth() {
        if (this._orientation == DiagramElement.Orientation.HORIZONTAL)    { return this._roles.get(0).borderWidth() * this._roles.size(); }
        else if (this._orientation == DiagramElement.Orientation.VERTICAL) { return this._roles.get(0).borderWidth(); }

        assert false : "ASSERT :: Try to get border width with invalid orientation.";
        return -1;
    }

    @Override
    public int borderHeight() {
        if (this._orientation == DiagramElement.Orientation.HORIZONTAL)    { return this._roles.get(0).borderHeight(); }
        else if (this._orientation == DiagramElement.Orientation.VERTICAL) { return this._roles.get(0).borderHeight() * this._roles.size(); }

        assert false : "ASSERT :: Try to get border height with invalid orientation.";
        return -1;
    }

    // ----------------- contract -----------------
    @Override
    public <T extends DiagramElement> ArrayList<T> getIncidentElements(Class<T> elementType) {
        ArrayList<T> result = super.getIncidentElements(elementType);
        for (Role role : this._roles) { result.addAll(role.getIncidentElements(elementType)); }

        for (RolesSequence rolesSequence : this._rolesSequences) {
            result.addAll(rolesSequence.getIncidentElements(elementType));
        }

        return result;
    }

    // ================= SUBTYPES =================
    private class ChangePredicateOrientationAction extends Action {
        private final Predicate                  _node;
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
