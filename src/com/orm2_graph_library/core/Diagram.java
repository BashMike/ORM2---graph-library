package com.orm2_graph_library.core;

import com.orm2_graph_library.action_errors.DiagramElementSelfConnectedActionError;
import com.orm2_graph_library.action_errors.DoubleConnectionActionError;
import com.orm2_graph_library.edges.RoleConstraintRelationEdge;
import com.orm2_graph_library.edges.RoleRelationEdge;
import com.orm2_graph_library.edges.SubtypingConstraintRelationEdge;
import com.orm2_graph_library.edges.SubtypingRelationEdge;
import com.orm2_graph_library.logic_errors.EntityTypeWithNoneRefModeLogicError;
import com.orm2_graph_library.nodes.common.EntityType;
import com.orm2_graph_library.nodes.common.ValueType;
import com.orm2_graph_library.nodes.constraints.*;
import com.orm2_graph_library.nodes.predicates.*;
import com.orm2_graph_library.post_validators.SubtypingCyclePostValidator;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO - @add :: Movement of anchor points when moving node or edge.

// TODO - @info  :: Method call map (Maybe components diagram).
// TODO - @clean :: Check accessibility of each components functionality from every point of code.
// TODO - @clean :: Change order of test expected and actual data.
// TODO - @clean :: Put "abstract" and "static" keywords of class members to the beginning.

// TODO - @now :: Anchor points (connect nodes with methods; not with methods in the diagram).
// TODO - @now :: Getting geometry approximation.
// TODO - @now :: Edge hierarchy.

// @info :: Any method which introduces saved action produces only and only one saved action.

/*
    @info :: Levels of validation:

    1 pre-validation (at compile time);
    2 in-time validation (at runtime):
        1.1 Runtime Exceptions;
        1.2 Without notification;
        1.3 With notification.
    3 post-validation (after executing the actions; using validators).
 */

public class Diagram {
    // ================== STATIC ==================
    static private Font _font = new Font(Font.MONOSPACED, Font.PLAIN, 14);

    static public Font font() { return Diagram._font; }
    static public void setFont(Font font) { Diagram._font = font; }

    // ================ ATTRIBUTES ================
    private final ArrayList<DiagramElement>  _innerElements        = new ArrayList<>();

    protected final ActionManager            _actionManager        = new ActionManager();
    protected ArrayList<LogicError>          _logicErrors          = new ArrayList<>();
    protected ArrayList<ActionErrorListener> _actionErrorListeners = new ArrayList<>();

    // ================ OPERATIONS ================
    // ---------------- attributes ----------------
    public Stream<LogicError> logicErrors() { return this._logicErrors.stream(); }
    public Stream<LogicError> getLogicErrorsFor(DiagramElement diagramElement) { return this._logicErrors.stream().filter(e -> e.isErrorParticipant(diagramElement)); }

    void _addLogicError(@NotNull LogicError logicError)    { this._logicErrors.add(logicError); }
    void _removeLogicError(@NotNull LogicError logicError) { this._logicErrors.remove(logicError); }

    public void addActionErrorListener(ActionErrorListener actionErrorListener) { this._actionErrorListeners.add(actionErrorListener); }

    // ----------------- contract -----------------
    public <T extends Node> T addNode(T node) {
        if      (node instanceof EntityType)            { this._actionManager.executeAction(new AddEntityTypeAction(this, (EntityType)node)); }
        else if (node instanceof ValueType)             { this._actionManager.executeAction(new AddValueTypeAction (this, (ValueType)node)); }
        else if (node instanceof Predicate)             { this._actionManager.executeAction(new AddPredicateAction(this, (Predicate)node)); }
        else if (node instanceof ObjectifiedPredicate)  { this._actionManager.executeAction(new AddObjectifiedPredicateAction(this, (ObjectifiedPredicate)node)); }

        else if (node instanceof SubsetConstraint)      { this._actionManager.executeAction(new AddConstraintAction(this, (SubsetConstraint)node)); }
        else if (node instanceof EqualityConstraint)    { this._actionManager.executeAction(new AddConstraintAction(this, (EqualityConstraint)node)); }
        else if (node instanceof UniquenessConstraint)  { this._actionManager.executeAction(new AddConstraintAction(this, (UniquenessConstraint)node)); }
        else if (node instanceof ExclusionConstraint)   { this._actionManager.executeAction(new AddConstraintAction(this, (ExclusionConstraint)node)); }
        else if (node instanceof ExclusiveOrConstraint) { this._actionManager.executeAction(new AddConstraintAction(this, (ExclusiveOrConstraint)node)); }
        else if (node instanceof InclusiveOrConstraint) { this._actionManager.executeAction(new AddConstraintAction(this, (InclusiveOrConstraint)node)); }

        else {
            throw new RuntimeException("ERROR :: Try to add incompatible type of node (" + node.getClass().descriptorString() + ").");
        }

        return node;
    }

    public void removeNode(Node node) {
        if      (node instanceof EntityType)            { this._actionManager.executeAction(new RemoveEntityTypeAction(this, (EntityType)node)); }
        else if (node instanceof ValueType)             { this._actionManager.executeAction(new RemoveValueTypeAction (this, (ValueType)node)); }
        else if (node instanceof Predicate)             { this._actionManager.executeAction(new RemovePredicateAction(this, (Predicate)node)); }
        else if (node instanceof ObjectifiedPredicate)  { this._actionManager.executeAction(new RemoveObjectifiedPredicateAction(this, (ObjectifiedPredicate)node)); }

        else if (node instanceof SubsetConstraint)      { this._actionManager.executeAction(new RemoveConstraintAction(this, (SubsetConstraint)node)); }
        else if (node instanceof EqualityConstraint)    { this._actionManager.executeAction(new RemoveConstraintAction(this, (EqualityConstraint)node)); }
        else if (node instanceof UniquenessConstraint)  { this._actionManager.executeAction(new RemoveConstraintAction(this, (UniquenessConstraint)node)); }
        else if (node instanceof ExclusionConstraint)   { this._actionManager.executeAction(new RemoveConstraintAction(this, (ExclusionConstraint)node)); }
        else if (node instanceof ExclusiveOrConstraint) { this._actionManager.executeAction(new RemoveConstraintAction(this, (ExclusiveOrConstraint)node)); }
        else if (node instanceof InclusiveOrConstraint) { this._actionManager.executeAction(new RemoveConstraintAction(this, (InclusiveOrConstraint)node)); }

        else {
            throw new RuntimeException("ERROR :: Try to remove incompatible type of node (" + node.getClass().descriptorString() + ").");
        }
    }

    public <T extends EntityType, G extends EntityType> SubtypingRelationEdge connectBySubtypingRelation(AnchorPoint<T> beginEntityTypeAnchorPoint, AnchorPoint<G> endEntityTypeAnchorPoint) {
        SubtypingRelationEdge edge = new SubtypingRelationEdge((AnchorPoint<EntityType>)beginEntityTypeAnchorPoint, (AnchorPoint<EntityType>)endEntityTypeAnchorPoint);
        this._actionManager.executeAction(new ConnectBySubtypeRelationAction(this, edge));

        return edge;
    }

    public <T extends Role, G extends RoleParticipant> RoleRelationEdge connectByRoleRelation(AnchorPoint<T> roleAnchorPoint, AnchorPoint<G> roleParticipantAnchorPoint) {
        RoleRelationEdge edge = new RoleRelationEdge((AnchorPoint<Role>)roleAnchorPoint, (AnchorPoint<RoleParticipant>)roleParticipantAnchorPoint);
        this._actionManager.executeAction(new ConnectByRoleRelationAction(this, edge));

        return edge;
    }

    public <T extends Constraint> RoleConstraintRelationEdge connectByRoleConstraintRelation(RolesSequence rolesSequence, AnchorPoint<T> constraintAnchorPoint) {
        RoleConstraintRelationEdge edge = new RoleConstraintRelationEdge(rolesSequence.anchorPoint(), (AnchorPoint<Constraint>)constraintAnchorPoint);
        this._actionManager.executeAction(new ConnectByRoleConstraintRelationAction(this, edge));

        return edge;
    }

    public <T extends SubtypingRelationEdge, G extends Constraint> SubtypingConstraintRelationEdge connectBySubtypingConstraintRelation(AnchorPoint<T> subtypingRelationEdgeAnchorPoint, AnchorPoint<G> constraintAnchorPoint) {
        SubtypingConstraintRelationEdge edge = new SubtypingConstraintRelationEdge((AnchorPoint<SubtypingRelationEdge>)subtypingRelationEdgeAnchorPoint, (AnchorPoint<Constraint>)constraintAnchorPoint);
        this._actionManager.executeAction(new ConnectBySubtypingConstraintRelationAction(this, edge));

        return edge;
    }

    public <T extends DiagramElement> Stream<T> getElements(Class<T> elementType) { return (Stream<T>)this._innerElements.stream().filter(elem -> elementType.isAssignableFrom(elem.getClass())); }

    public <T extends EntityType, G extends EntityType> SubtypingRelationEdge getConnectBySubtypingRelation(T begin, G end) {
        Stream<SubtypingRelationEdge> edgesStream = this.getElements(SubtypingRelationEdge.class).filter(e -> e.begin() == begin && e.end() == end);

        ArrayList<SubtypingRelationEdge> edges = edgesStream.collect(Collectors.toCollection(ArrayList::new));
        if (!edges.isEmpty()) { return edges.get(0); }
        else                  { throw new RuntimeException("ERROR :: Try to get non-existent subtyping relation edge between given diagram elements."); }
    }

    public <T extends Role, G extends RoleParticipant> RoleRelationEdge getConnectByRoleRelation(T begin, G end) {
        Stream<RoleRelationEdge> edgesStream = this.getElements(RoleRelationEdge.class).filter(e -> e.begin() == begin && e.end() == end);

        ArrayList<RoleRelationEdge> edges = edgesStream.collect(Collectors.toCollection(ArrayList::new));
        if (!edges.isEmpty()) { return edges.get(0); }
        else                  { throw new RuntimeException("ERROR :: Try to get non-existent subtyping relation edge between given diagram elements."); }
    }

    public <T extends Constraint> RoleConstraintRelationEdge getConnectByRoleConstraintRelation(RolesSequence begin, T end) {
        Stream<RoleConstraintRelationEdge> edgesStream = this.getElements(RoleConstraintRelationEdge.class).filter(e -> e.begin() == begin && e.end() == end);

        ArrayList<RoleConstraintRelationEdge> edges = edgesStream.collect(Collectors.toCollection(ArrayList::new));
        if (!edges.isEmpty()) { return edges.get(0); }
        else                  { throw new RuntimeException("ERROR :: Try to get non-existent subtyping relation edge between given diagram elements."); }
    }

    public <T extends SubtypingRelationEdge, G extends Constraint> SubtypingConstraintRelationEdge getConnectBySubtypingConstraintRelation(T begin, G end) {
        Stream<SubtypingConstraintRelationEdge> edgesStream = this.getElements(SubtypingConstraintRelationEdge.class).filter(e -> e.begin() == begin && e.end() == end);

        ArrayList<SubtypingConstraintRelationEdge> edges = edgesStream.collect(Collectors.toCollection(ArrayList::new));
        if (!edges.isEmpty()) { return edges.get(0); }
        else                  { throw new RuntimeException("ERROR :: Try to get non-existent subtyping relation edge between given diagram elements."); }
    }

    public <T extends EntityType, G extends EntityType>            boolean hasConnectBySubtypingRelation           (T             begin, G end) { return this.getElements(SubtypingRelationEdge.class)           .anyMatch(e -> e.begin() == begin && e.end() == end); }
    public <T extends Role, G extends RoleParticipant>             boolean hasConnectByRoleRelation                (T             begin, G end) { return this.getElements(RoleRelationEdge.class)                .anyMatch(e -> e.begin() == begin && e.end() == end); }
    public <T extends Constraint>                                  boolean hasConnectByRoleConstraintRelation      (RolesSequence begin, T end) { return this.getElements(RoleConstraintRelationEdge.class)      .anyMatch(e -> e.begin() == begin && e.end() == end); }
    public <T extends SubtypingRelationEdge, G extends Constraint> boolean hasConnectBySubtypingConstraintRelation (T             begin, G end) { return this.getElements(SubtypingConstraintRelationEdge.class) .anyMatch(e -> e.begin() == begin && e.end() == end); }
    public <T extends DiagramElement, G extends DiagramElement>    boolean hasConnectByAnyRelation                 (T             begin, G end) { return this.getElements(Edge.class)                            .anyMatch(e -> e.begin() == begin && e.end() == end); }

    public void addRolesSequence(RolesSequence rolesSequence) { this._addElement(rolesSequence); }

    // Undo & redo state
    public boolean canUndoState() { return this._actionManager.canUndo(); }
    public void    undoState()    { this._actionManager.undo(); }
    public boolean canRedoState() { return this._actionManager.canRedo(); }
    public void    redoState()    { this._actionManager.redo(); }

    // -------------- sub-operations --------------
    private <T extends DiagramElement> T _addElement(T element) {
        element.setOwnerDiagram(this);
        this._innerElements.add(element);

        return element;
    }

    private void _removeElement(DiagramElement element) {
        element.unsetOwnerDiagram();
        this._innerElements.remove(element);
    }

    // ================= SUBTYPES =================
    private abstract class AddNodeAction<T extends Node> extends Action {
        protected final T _node;

        public AddNodeAction(Diagram diagram, @NotNull T node) {
            super(diagram);
            this._node = node;
        }

        @Override
        public void _execute() { this._diagram._addElement(this._node); }
        @Override
        public void _undo() { this._diagram._removeElement(this._node); }
    }

    private class AddEntityTypeAction extends AddNodeAction<EntityType> {
        public AddEntityTypeAction(Diagram diagram, @NotNull EntityType node) {
            super(diagram, node);
            this._emergedLogicErrors.add(new EntityTypeWithNoneRefModeLogicError(node));
        }
    }

    private class AddValueTypeAction extends AddNodeAction<ValueType> {
        public AddValueTypeAction(Diagram diagram, @NotNull ValueType node) { super(diagram, node); }
    }

    private class AddPredicateAction<G extends Predicate> extends AddNodeAction<G> {
        public AddPredicateAction(Diagram diagram, @NotNull G node) { super(diagram, node); }

        @Override
        public void _execute() {
            super._execute();
            this._node.roles().forEach(Diagram.this::_addElement);
        }

        @Override
        public void _undo() {
            super._undo();
            this._node.roles().forEach(Diagram.this::_removeElement);
        }
    }

    private class AddObjectifiedPredicateAction extends AddNodeAction<ObjectifiedPredicate> {
        public AddObjectifiedPredicateAction(Diagram diagram, @NotNull ObjectifiedPredicate node) { super(diagram, node); }

        @Override
        public void _execute() {
            super._execute();
            _addElement(this._node.innerPredicate());
            this._node.innerPredicate().roles().forEach(Diagram.this::_addElement);
        }

        @Override
        public void _undo() {
            super._undo();
            _removeElement(this._node.innerPredicate());
            this._node.innerPredicate().roles().forEach(Diagram.this::_removeElement);
        }
    }

    private class AddConstraintAction<G extends Constraint> extends AddNodeAction<G> {
        public AddConstraintAction(Diagram diagram, @NotNull G node) { super(diagram, node); }
    }

    private abstract class RemoveNodeAction<T extends Node> extends Action {
        protected final T               _node;
        protected final ArrayList<Edge> _incidentEdges;

        public RemoveNodeAction(Diagram diagram, @NotNull T node) {
            super(diagram);

            this._node          = node;
            this._incidentEdges = node.getIncidentElements(Edge.class);
        }

        @Override
        public void _execute() {
            this._diagram._removeElement(this._node);
            for (Edge edge : this._incidentEdges) { this._diagram._removeElement(edge); }
        }

        @Override
        public void _undo() {
            this._diagram._addElement(this._node);
            for (var edge : this._incidentEdges) { this._diagram._addElement(edge); }
        }
    }

    private class RemoveEntityTypeAction extends RemoveNodeAction<EntityType> {
        public RemoveEntityTypeAction(Diagram diagram, @NotNull EntityType node) { super(diagram, node); }
    }

    private class RemoveValueTypeAction extends RemoveNodeAction<ValueType> {
        public RemoveValueTypeAction(Diagram diagram, @NotNull ValueType node) { super(diagram, node); }
    }

    private class RemovePredicateAction extends RemoveNodeAction<Predicate> {
        public RemovePredicateAction(Diagram diagram, @NotNull Predicate node) {
            super(diagram, node);

            if (node.hasOwnerObjectifiedPredicate() && node.ownerObjectifiedPredicate().hasOwnerDiagram()) {
                throw new RuntimeException("ERROR :: Try to remove predicate which has non-removed owner on the diagram.");
            }
        }

        @Override
        public void _execute() {
            super._execute();
            this._node.roles().forEach(Diagram.this::_removeElement);
        }

        @Override
        public void _undo() {
            super._undo();
            this._node.roles().forEach(Diagram.this::_addElement);
        }
    }

    private class RemoveObjectifiedPredicateAction extends RemoveNodeAction<ObjectifiedPredicate> {
        public RemoveObjectifiedPredicateAction(Diagram diagram, @NotNull ObjectifiedPredicate node) { super(diagram, node); }

        @Override
        public void _execute() {
            super._execute();
            _removeElement(this._node.innerPredicate());
            this._node.innerPredicate().roles().forEach(Diagram.this::_removeElement);
        }

        @Override
        public void _undo() {
            super._undo();
            _addElement(this._node.innerPredicate());
            this._node.innerPredicate().roles().forEach(Diagram.this::_addElement);
        }
    }

    private class RemoveConstraintAction<G extends Constraint> extends RemoveNodeAction<G> {
        public RemoveConstraintAction(Diagram diagram, @NotNull G node) { super(diagram, node); }
    }

    private abstract class ConnectAction<T extends DiagramElement, G extends DiagramElement> extends Action {
        protected final Edge<T, G> _edge;

        public ConnectAction(@NotNull Diagram diagram, @NotNull Edge<T, G> edge) {
            super(diagram);
            this._edge = edge;

            // Check if edge connects diagram element with itself
            if (this._edge.beginAnchorPoint().owner() == this._edge.endAnchorPoint().owner()) {
                this._throwActionError(new DiagramElementSelfConnectedActionError(this._edge.beginAnchorPoint().owner()));
            }

            // Check if edge connects diagram elements twice
            var existEdges = this._diagram.getElements(Edge.class).filter(e -> e.isSameTo(this._edge) || e.isOppositeTo(this._edge)).collect(Collectors.toCollection(ArrayList::new));

            if (!existEdges.isEmpty() && !(existEdges.get(0) instanceof SubtypingRelationEdge && existEdges.get(0).isOppositeTo(this._edge))) {
                this._throwActionError(new DoubleConnectionActionError(this._edge.beginAnchorPoint().owner(), this._edge.endAnchorPoint().owner(), existEdges.get(0)));
            }
        }

        @Override
        public void _execute() { this._diagram._addElement(this._edge); }
        @Override
        public void _undo() { this._diagram._removeElement(this._edge); }
    }

    private class ConnectBySubtypeRelationAction extends ConnectAction {
        public ConnectBySubtypeRelationAction(@NotNull Diagram diagram, @NotNull SubtypingRelationEdge edge) {
            super(diagram, edge);
            this._postValidators.add(new SubtypingCyclePostValidator(diagram, this, edge.beginAnchorPoint().owner()));
        }
    }

    private class ConnectByRoleRelationAction extends ConnectAction {
        public ConnectByRoleRelationAction(@NotNull Diagram diagram, @NotNull RoleRelationEdge edge) {
            super(diagram, edge);
        }
    }

    private class ConnectByRoleConstraintRelationAction extends ConnectAction {
        public ConnectByRoleConstraintRelationAction(@NotNull Diagram diagram, @NotNull RoleConstraintRelationEdge edge) {
            super(diagram, edge);
        }
    }

    private class ConnectBySubtypingConstraintRelationAction extends ConnectAction {
        public ConnectBySubtypingConstraintRelationAction(@NotNull Diagram diagram, @NotNull SubtypingConstraintRelationEdge edge) {
            super(diagram, edge);
        }
    }

    private abstract class DisconnectAction extends Action {
        private final Edge _edge;

        public DisconnectAction(@NotNull Diagram diagram, @NotNull Edge edge) {
            super(diagram);
            this._edge = edge;
        }

        @Override
        public void _execute() { this._diagram._removeElement(this._edge); }
        @Override
        public void _undo() { this._diagram._addElement(this._edge); }
    }
}
