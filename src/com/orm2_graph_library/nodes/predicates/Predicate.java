package com.orm2_graph_library.nodes.predicates;

import com.orm2_graph_library.core.*;
import com.orm2_graph_library.nodes_shapes.RectangleShape;
import com.orm2_graph_library.utils.Point2D;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Predicate extends Node implements Movable {
    // ================ ATTRIBUTES ================
    private ObjectifiedPredicate       _ownerObjectifiedPredicate = null;
    final private ArrayList<Role>      _roles                     = new ArrayList<>();
    private DiagramElement.Orientation _orientation               = DiagramElement.Orientation.HORIZONTAL;
    final private Set<RolesSequence>   _rolesSequences            = new HashSet<>();
    final private Set<RolesSequence>   _uniqueRolesSequences      = new HashSet<>();

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
    @Override protected void _initSelf() {}
    @Override protected void _finalizeSelf() {}

    @NotNull
    public ObjectifiedPredicate ownerObjectifiedPredicate() {
        /*
        ObjectifiedPredicate result = null;

        if (this.hasOwnerDiagram()) {
            ArrayList<ObjectifiedPredicate> objectifiedPredicatesOwners = this._ownerDiagram.getElements(ObjectifiedPredicate.class).filter(e -> e.innerPredicate() == this).collect(Collectors.toCollection(ArrayList::new));

            assert objectifiedPredicatesOwners.size() <= 1 : "ASSERT :: " + objectifiedPredicatesOwners.size() + " objectified predicates with same inner predicate in the diagram.";
            if (objectifiedPredicatesOwners.size() == 1) {
                result = objectifiedPredicatesOwners.get(0);
            }
        }

        return result;
        */
        return this._ownerObjectifiedPredicate;
    }

    public boolean hasOwnerObjectifiedPredicate() {
        /*
        ArrayList<ObjectifiedPredicate> objectifiedPredicatesOwners = new ArrayList<>();

        if (this.hasOwnerDiagram()) {
            objectifiedPredicatesOwners = this._ownerDiagram.getElements(ObjectifiedPredicate.class).filter(e -> e.innerPredicate() == this).collect(Collectors.toCollection(ArrayList::new));
            assert objectifiedPredicatesOwners.size() <= 1 : "ASSERT :: " + objectifiedPredicatesOwners.size() + " objectified predicates with same inner predicate in the diagram.";
        }

        return objectifiedPredicatesOwners.size() == 1;
        */
        return (this._ownerObjectifiedPredicate != null);
    }

    void _setOwnerObjectifiedPredicate(@NotNull ObjectifiedPredicate ownerObjectifiedPredicate) {
        if (!this.hasOwnerObjectifiedPredicate()) {
            this._ownerObjectifiedPredicate = ownerObjectifiedPredicate;
        }
        else {
            throw new RuntimeException("ERROR :: attempt to set objectified predicate as owner twice.");
        }
    }

    void _unsetOwnerObjectifiedPredicate() { this._ownerObjectifiedPredicate = null; }

    // ---------------- attributes ----------------
    // * Roles
    public Role getRole(int index) { return this._roles.get(index); }
    public Stream<Role> roles() { return this._roles.stream(); }

    public int arity() { return this._roles.size(); }

    public String rolesText(@NotNull String delimiter) {
        String result = this._roles.get(0).text();
        for (int i=1; i<this._roles.size(); i++) { result += delimiter + this._roles.get(i).text(); }

        return result;
    }

    public RolesSequence rolesSequence(int... rolesIndexes) {
        ArrayList<Role> roles = new ArrayList<>();
        for (int rolesIndex : Arrays.stream(rolesIndexes).sorted().toArray()) {
            roles.add(this._roles.get(rolesIndex));
        }

        if (new HashSet<>(roles).size() != roles.size()) {
            throw new RuntimeException("ERROR :: Attempt to get roles sequence with duplicated roles in it.");
        }

        RolesSequence rolesSequence = new RolesSequence(roles);
        rolesSequence._initSelf();
        ArrayList<RolesSequence> rolesSequences = this._rolesSequences.stream()
                .filter(e -> e.equals(rolesSequence))
                .collect(Collectors.toCollection(ArrayList::new));

        if (rolesSequences.isEmpty()) {
            this._rolesSequences.add(rolesSequence);
            this._addDiagramElementToOwnerDiagram(rolesSequence); // TODO - @modify :: Secure adding role sequence to the diagram.

            return rolesSequence;
        }
        else {
            return rolesSequences.get(0);
        }
    }

    public boolean isInternalRolesSequence(RolesSequence rolesSequence) { return this._rolesSequences.contains(rolesSequence); }
    public boolean hasRolesSequence(int... rolesIndexes) {
        ArrayList<Role> roles = new ArrayList<>();
        for (int rolesIndex : Arrays.stream(rolesIndexes).sorted().toArray()) {
            roles.add(this._roles.get(rolesIndex));
        }

        return this._rolesSequences.contains(new RolesSequence(roles));
    }

    public Stream<RolesSequence> usedRolesSequences() { return this._rolesSequences.stream().filter(e -> e.hasIncidentElements(DiagramElement.class)); }

    public boolean isRolesSequenceUnique(@NotNull RolesSequence rolesSequence) { return this._uniqueRolesSequences.contains(rolesSequence); }
    public boolean isRolesSequenceUnique(int... rolesIndexes) { return this.hasRolesSequence(rolesIndexes) && this.isRolesSequenceUnique(this.rolesSequence(rolesIndexes)); }
    public Stream<RolesSequence> uniqueRolesSequences() { return this._uniqueRolesSequences.stream(); }

    public void makeRolesSequenceUnique   (int... rolesIndexes) { this._ownerDiagramActionManager().executeAction(new MakeRolesSequenceUniqueAction   (this._ownerDiagram, this, this.rolesSequence(rolesIndexes))); }
    public void makeRolesSequenceNonUnique(int... rolesIndexes) { this._ownerDiagramActionManager().executeAction(new MakeRolesSequenceNonUniqueAction(this._ownerDiagram, this, this.rolesSequence(rolesIndexes))); }

    // * Anchor points
    @Override public Stream<AnchorPoint> anchorPoints() { return Stream.of(); }

    // * Geometry
    public DiagramElement.Orientation orientation() { return this._orientation; }

    public void setOrientation(DiagramElement.Orientation orientation) {
        if (!this._orientation.equals(orientation)) {
            this._ownerDiagramActionManager().executeAction(new ChangePredicateOrientationAction(this._ownerDiagram, this, this._orientation, orientation));
        }
    }

    public void setRolesBorderSize(int borderWidth, int borderHeight) {
        for (Role role : this._roles) { role.setBorderSize(borderWidth, borderHeight); }
    }
    public void moveTo(Point2D leftTop) {
        this._ownerDiagramActionManager().executeAction(new MovePredicateAction(this._ownerDiagram, this, this._leftTop, leftTop));
    }

    public void moveBy(int shiftX, int shiftY) {
        Point2D newLeftTop = new Point2D(this._leftTop);
        newLeftTop.translate(shiftX, shiftY);

        this._ownerDiagramActionManager().executeAction(new MovePredicateAction(this._ownerDiagram, this, this._leftTop, newLeftTop));
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
    public <T extends DiagramElement> Stream<T> getIncidentElements(Class<T> elementType) {
        Stream<T> result = super.getIncidentElements(elementType);
        for (Role role : this._roles) { result = Stream.concat(result, role.getIncidentElements(elementType)); }

        for (RolesSequence rolesSequence : this.usedRolesSequences().collect(Collectors.toCollection(ArrayList::new))) {
            result = Stream.concat(result, rolesSequence.getIncidentElements(elementType));
        }

        return result;
    }

    // ================= SUBTYPES =================
    public class ChangePredicateOrientationAction extends Action {
        private final Predicate                  _node;
        private final DiagramElement.Orientation _oldOrientation;
        private final DiagramElement.Orientation _newOrientation;

        private ChangePredicateOrientationAction(@NotNull Diagram diagram, @NotNull Predicate node, @NotNull DiagramElement.Orientation oldOrientation, @NotNull DiagramElement.Orientation newOrientation) {
            super(diagram);

            this._node           = node;
            this._oldOrientation = oldOrientation;
            this._newOrientation = newOrientation;
        }

        public Predicate node() { return this._node; }
        public DiagramElement.Orientation oldOrientation() { return this._oldOrientation; }
        public DiagramElement.Orientation newOrientation() { return this._newOrientation; }

        @Override public void _execute() { this._changeOrientationOfNodesAndItsComponents(this._newOrientation); }
        @Override public void _undo()    { this._changeOrientationOfNodesAndItsComponents(this._oldOrientation); }

        private void _changeOrientationOfNodesAndItsComponents(DiagramElement.Orientation orientation) {
            this._node._orientation = orientation;

            for (int i=0; i<this._node._roles.size(); i++) {
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

    public class MakeRolesSequenceUniqueAction extends Action {
        private final Predicate     _node;
        private final RolesSequence _rolesSequence;

        private MakeRolesSequenceUniqueAction(@NotNull Diagram diagram, @NotNull Predicate node, @NotNull RolesSequence rolesSequence) {
            super(diagram);

            this._node = node;
            this._rolesSequence = rolesSequence;
        }

        public Predicate node() { return this._node; }
        public RolesSequence rolesSequence() { return this._rolesSequence; }

        @Override public void _execute() { _uniqueRolesSequences.add(this._rolesSequence); }
        @Override public void _undo()    { _uniqueRolesSequences.remove(this._rolesSequence); }
    }


    public class MakeRolesSequenceNonUniqueAction extends Action {
        final private Predicate     _node;
        final private RolesSequence _rolesSequence;

        private MakeRolesSequenceNonUniqueAction(@NotNull Diagram diagram, @NotNull Predicate node, @NotNull RolesSequence rolesSequence) {
            super(diagram);

            this._node = node;
            this._rolesSequence = rolesSequence;
        }

        public Predicate node() { return this._node; }
        public RolesSequence rolesSequence() { return this._rolesSequence; }

        @Override public void _execute() { _uniqueRolesSequences.remove(this._rolesSequence); }
        @Override public void _undo()    { _uniqueRolesSequences.add(this._rolesSequence); }
    }

    /*
    public class PredicateBecomesObjectifiedAction extends Action {
        final private Predicate            _node;
        final private ObjectifiedPredicate _objectifiedPredicate;

        private PredicateBecomesObjectifiedAction(@NotNull Diagram diagram, @NotNull Predicate node, @NotNull ObjectifiedPredicate objectifiedPredicate) {
            super(diagram);

            this._node                 = node;
            this._objectifiedPredicate = objectifiedPredicate;
        }

        @Override
        public void _execute() {
            if (!hasOwnerObjectifiedPredicate()) { this._node._ownerObjectifiedPredicate = this._node._addDiagramElementToOwnerDiagram(this._objectifiedPredicate); }
            else                                 { throw new RuntimeException("ERROR :: attempt to objectified predicate that is already objectified."); }
        }

        @Override
        public void _undo() {
            this._node._ownerObjectifiedPredicate = null;
            this._node._removeDiagramElementToOwnerDiagram(this._objectifiedPredicate);
        }
    }
    */

    public class MovePredicateAction extends Action {
        private final Predicate _node;
        private final Point2D   _oldNodeLeftTop;
        private final Point2D   _newNodeLeftTop;

        private MovePredicateAction(@NotNull Diagram diagram, @NotNull Predicate node, @NotNull Point2D oldNodeLeftTop, @NotNull Point2D newNodeLeftTop) {
            super(diagram);

            this._node           = node;
            this._oldNodeLeftTop = oldNodeLeftTop;
            this._newNodeLeftTop = newNodeLeftTop;
        }

        public Predicate node() { return this._node; }
        public Point2D oldNodeLeftTop() { return this._oldNodeLeftTop; }
        public Point2D newNodeLeftTop() { return this._newNodeLeftTop; }

        @Override
        public void _execute() {
            this._node._leftTop.move(this._newNodeLeftTop.x, this._newNodeLeftTop.y);

            int shiftX = this._newNodeLeftTop.x - this._oldNodeLeftTop.x;
            int shiftY = this._newNodeLeftTop.y - this._oldNodeLeftTop.y;

            for (Role role : this._node._roles) {
                role._moveBy(shiftX, shiftY);
            }
        }

        @Override
        public void _undo() {
            this._node._leftTop.move(this._oldNodeLeftTop.x, this._oldNodeLeftTop.y);

            int shiftX = this._oldNodeLeftTop.x - this._newNodeLeftTop.x;
            int shiftY = this._oldNodeLeftTop.y - this._newNodeLeftTop.y;

            for (Role role : this._node._roles) {
                role._moveBy(shiftX, shiftY);
            }
        }
    }
}
