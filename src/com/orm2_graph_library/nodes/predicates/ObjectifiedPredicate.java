package com.orm2_graph_library.nodes.predicates;

import com.orm2_graph_library.anchor_points.AnchorPosition;
import com.orm2_graph_library.anchor_points.NodeAnchorPoint;
import com.orm2_graph_library.core.Action;
import com.orm2_graph_library.core.AnchorPoint;
import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.DiagramElement;
import com.orm2_graph_library.nodes.common.ObjectType;
import com.orm2_graph_library.nodes_shapes.RoundedRectangleShape;
import com.orm2_graph_library.utils.Point2D;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.stream.Stream;

public class ObjectifiedPredicate extends RoleParticipant {
    // ================ ATTRIBUTES ================
    final private Predicate _innerPredicate;
    private int _horizontalGapDistance;
    private int _verticalGapDistance;

    private String  _name;
    private boolean _isIndependent = false;

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public ObjectifiedPredicate(Predicate innerPredicate) { this._innerPredicate = innerPredicate; }

    // ---------------- connection ----------------
    @Override
    protected void _initSelf() {
        // Set default name
        boolean isVacantNameFound = false;
        int index;
        for (index=1; index<Integer.MAX_VALUE && !isVacantNameFound; index++) {
            String name = this.basicName() + " " + index;
            isVacantNameFound = this._ownerDiagram.getElements(ObjectifiedPredicate.class).noneMatch(e -> e._name.equals(name));
        }

        assert isVacantNameFound : "ERROR :: Failed to find proper index for object type.";
        this._name = this.basicName() + " " + (index-1);

        // Check if other objectified predicate in the diagram has same inner predicate
        this._innerPredicate._setOwnerObjectifiedPredicate(this);
        if (this._ownerDiagram.getElements(ObjectifiedPredicate.class).anyMatch(e -> e._innerPredicate == this._innerPredicate)) {
            throw new RuntimeException("ERROR :: attempt to add objectified predicate with inner predicate that is occupied by other objectified predicate in the diagram.");
        }
    }

    @Override protected void _finalizeSelf() { this._innerPredicate._unsetOwnerObjectifiedPredicate(); }

    public Predicate innerPredicate() { return this._innerPredicate; }

    public Predicate becomeUnobjectified() {
        this._ownerDiagramActionManager().executeAction(new PredicateBecomesUnobjectifiedAction(this._ownerDiagram, this));

        return this._innerPredicate;
    }

    // ---------------- attributes ----------------
    // * Name
    public String name() { return this._name; }
    public String basicName() { return "Objectified predicate"; }

    public void setName(String name) { this._ownerDiagramActionManager().executeAction(new ObjectifiedPredicateNameChangeAction(this._ownerDiagram, this, this._name, name)); }

    // * "Is independent" flag
    public boolean isIndependent() { return this._isIndependent; }
    public void setIsIndependent(boolean isIndependent) { this._ownerDiagramActionManager().executeAction(new ObjectifiedPredicateIsIndependentFlagChangeAction(this._ownerDiagram, this, this._isIndependent, isIndependent)); }

    // * Anchor points
    public AnchorPoint<ObjectifiedPredicate> upAnchorPoint()    { return new NodeAnchorPoint<>(this, AnchorPosition.UP); }
    public AnchorPoint<ObjectifiedPredicate> downAnchorPoint()  { return new NodeAnchorPoint<>(this, AnchorPosition.DOWN); }
    public AnchorPoint<ObjectifiedPredicate> rightAnchorPoint() { return new NodeAnchorPoint<>(this, AnchorPosition.RIGHT); }
    public AnchorPoint<ObjectifiedPredicate> leftAnchorPoint()  { return new NodeAnchorPoint<>(this, AnchorPosition.LEFT); }

    @Override public Stream<AnchorPoint> anchorPoints() { return Stream.of(this.upAnchorPoint(), this.downAnchorPoint(), this.rightAnchorPoint(), this.leftAnchorPoint()); }

    // * Geometry
    public void setGapsDistances(int horizontalGapDistance, int verticalGapDistance) {
        this._horizontalGapDistance = horizontalGapDistance;
        this._verticalGapDistance   = verticalGapDistance;
    }

    public void setBorderRoundingDegree(int borderRoundingDegree) {
        this._shape = new RoundedRectangleShape(borderRoundingDegree);
    }

    @Override
    public Point2D borderLeftTop() {
        int x = this._innerPredicate.borderLeftTop().x - this._horizontalGapDistance;
        int y = this._innerPredicate.borderLeftTop().y - this._verticalGapDistance;

        return new Point2D(x, y);
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

    @Override
    public void moveTo(Point2D leftTop) {
        this._ownerDiagramActionManager().executeAction(new MoveObjectifiedPredicateAction(this._ownerDiagram, this, this._leftTop, leftTop));
    }

    @Override
    public void moveBy(int shiftX, int shiftY) {
        Point2D newLeftTop = new Point2D(this._leftTop);
        newLeftTop.translate(shiftX, shiftY);

        this._ownerDiagramActionManager().executeAction(new MoveObjectifiedPredicateAction(this._ownerDiagram, this, this._leftTop, newLeftTop));
    }

    // ----------------- contract -----------------
    @Override
    public <T extends DiagramElement> Stream<T> getIncidentElements(Class<T> elementType) {
        Stream<T> result = super.getIncidentElements(elementType);
        result = Stream.concat(result, this._innerPredicate.getIncidentElements(elementType));

        return result;
    }

    // ================= SUBTYPES =================
    public class ObjectifiedPredicateNameChangeAction extends Diagram.DiagramElementAttributeChangeAction {
        private ObjectifiedPredicateNameChangeAction(@NotNull Diagram diagram, @NotNull ObjectifiedPredicate node, @NotNull String oldName, @NotNull String newName) {
            super(diagram, node, oldName, newName);

            // TODO - @check :: Objectified predicate and object type have same names.
            // this._postValidators.add(new ObjectTypesNameDuplicationPostValidator(diagram, this, node, oldName, newName));
        }

        public ObjectifiedPredicate node() { return (ObjectifiedPredicate)this._diagramElement; }
        public String newName() { return (String)this._newAttributeValue; }
        public String oldName() { return (String)this._oldAttributeValue; }

        @Override public void _execute() { ((ObjectifiedPredicate)this._diagramElement)._name = (String)this._newAttributeValue; }
        @Override public void _undo()    { ((ObjectifiedPredicate)this._diagramElement)._name = (String)this._oldAttributeValue; }
    }

    public class ObjectifiedPredicateIsIndependentFlagChangeAction extends Diagram.DiagramElementAttributeChangeAction {
        private ObjectifiedPredicateIsIndependentFlagChangeAction(@NotNull Diagram diagram, @NotNull ObjectifiedPredicate node, @NotNull boolean oldIsIndependent, @NotNull boolean newIsIndependent) {
            super(diagram, node, oldIsIndependent, newIsIndependent);

            // TODO - @check :: Objectified predicate and object type have same names.
            // this._postValidators.add(new ObjectTypesNameDuplicationPostValidator(diagram, this, node, oldName, newName));
        }

        public ObjectifiedPredicate node() { return (ObjectifiedPredicate)this._diagramElement; }
        public boolean newIsIndependent() { return (Boolean)this._newAttributeValue; }
        public boolean oldIsIndependent() { return (Boolean)this._oldAttributeValue; }

        @Override public void _execute() { ((ObjectifiedPredicate)this._diagramElement)._isIndependent = (Boolean)this._newAttributeValue; }
        @Override public void _undo()    { ((ObjectifiedPredicate)this._diagramElement)._isIndependent = (Boolean)this._oldAttributeValue; }
    }

    public class PredicateBecomesUnobjectifiedAction extends Action {
        final private ObjectifiedPredicate _node;
        final private Predicate            _innerPredicate;

        private PredicateBecomesUnobjectifiedAction(@NotNull Diagram diagram, @NotNull ObjectifiedPredicate node) {
            super(diagram);

            this._node           = node;
            this._innerPredicate = this._node.innerPredicate();

            // TODO - @check :: Objectified predicate and object type have same names.
            // this._postValidators.add(new ObjectTypesNameDuplicationPostValidator(diagram, this, node, oldName, newName));
        }

        public ObjectifiedPredicate node() { return this._node; }
        public Predicate innerPredicate()  { return this._innerPredicate; }

        @Override public void _execute() { this._node._removeDiagramElementFromDiagram(this._diagram, this._node); }
        @Override public void _undo()    { this._node._addDiagramElementToDiagram(this._diagram, this._node); }
    }

    public class MoveObjectifiedPredicateAction extends Action {
        final private ObjectifiedPredicate _node;
        final private Point2D              _oldLeftTop;
        final private Point2D              _newLeftTop;

        private MoveObjectifiedPredicateAction(@NotNull Diagram diagram, @NotNull ObjectifiedPredicate node, @NotNull Point2D oldLeftTop, @NotNull Point2D newLeftTop) {
            super(diagram);

            this._node       = node;
            this._newLeftTop = new Point2D(newLeftTop.x + node._horizontalGapDistance, newLeftTop.y + node._verticalGapDistance);
            this._oldLeftTop = new Point2D(oldLeftTop);
        }

        public ObjectifiedPredicate _node() { return this._node; }
        public Point2D _newLeftTop() { return this._newLeftTop; }
        public Point2D _oldLeftTop() { return this._oldLeftTop; }

        @Override public void _execute() { this._node._innerPredicate.moveTo(this._newLeftTop); }
        @Override public void _undo()    { this._node._innerPredicate.moveTo(this._oldLeftTop); }
    }
}
