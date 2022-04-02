package com.orm2_graph_library.nodes.predicates;

import com.orm2_graph_library.anchor_points.AnchorPosition;
import com.orm2_graph_library.anchor_points.NodeAnchorPoint;
import com.orm2_graph_library.core.Action;
import com.orm2_graph_library.core.AnchorPoint;
import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.DiagramElement;
import com.orm2_graph_library.nodes.common.ObjectType;
import com.orm2_graph_library.nodes_shapes.RoundedRectangleShape;
import com.orm2_graph_library.post_validators.ObjectTypesNameDuplicationPostValidator;
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

    private String _name;

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public ObjectifiedPredicate(Predicate innerPredicate) {
        this._innerPredicate = innerPredicate;
        this._innerPredicate.setOwnerObjectifiedPredicate(this);
    }

    // ---------------- connection ----------------
    @Override
    protected void _initSelf() {
        boolean isVacantNameFound = false;
        int index;
        for (index=1; index<Integer.MAX_VALUE && !isVacantNameFound; index++) {
            String name = this.basicName() + " " + index;
            isVacantNameFound = this._ownerDiagram.getElements(ObjectifiedPredicate.class).noneMatch(e -> e._name.equals(name));
        }

        assert isVacantNameFound : "ERROR :: Failed to find proper index for object type.";
        this._name = this.basicName() + " " + (index-1);
    }

    // ---------------- attributes ----------------
    public Predicate innerPredicate() { return this._innerPredicate; }

    public String name() { return this._name; }
    public void setName(String name) { this._ownerDiagramActionManager().executeAction(new ObjectifiedPredicateNameChangeAction(this._ownerDiagram, this, this._name, name)); }

    public String basicName() { return "Objectified predicate"; }

    public AnchorPoint<ObjectifiedPredicate> upAnchorPoint()    { return new NodeAnchorPoint<>(this, AnchorPosition.UP); }
    public AnchorPoint<ObjectifiedPredicate> downAnchorPoint()  { return new NodeAnchorPoint<>(this, AnchorPosition.DOWN); }
    public AnchorPoint<ObjectifiedPredicate> rightAnchorPoint() { return new NodeAnchorPoint<>(this, AnchorPosition.RIGHT); }
    public AnchorPoint<ObjectifiedPredicate> leftAnchorPoint()  { return new NodeAnchorPoint<>(this, AnchorPosition.LEFT); }

    public void setGapsDistances(int horizontalGapDistance, int verticalGapDistance) {
        this._horizontalGapDistance = horizontalGapDistance;
        this._verticalGapDistance   = verticalGapDistance;
    }

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

    // ================= SUBTYPES =================
    private class ObjectifiedPredicateNameChangeAction extends Action {
        final private ObjectifiedPredicate _node;
        final private String               _oldName;
        final private String               _newName;

        public ObjectifiedPredicateNameChangeAction(@NotNull Diagram diagram, @NotNull ObjectifiedPredicate node, @NotNull String oldName, @NotNull String newName) {
            super(diagram);

            this._node    = node;
            this._oldName = oldName;
            this._newName = newName;

            // TODO - @check :: Objectified predicate and object type have same names.
            // this._postValidators.add(new ObjectTypesNameDuplicationPostValidator(diagram, this, node, oldName, newName));
        }

        @Override
        public void _execute() { this._node._name = this._newName; }
        @Override
        public void _undo() { this._node._name = this._oldName; }
    }
}
