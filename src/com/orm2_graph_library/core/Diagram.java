package com.orm2_graph_library.core;

import com.orm2_graph_library.action_errors.DiagramElementSelfConnectedActionError;
import com.orm2_graph_library.action_errors.DoubleConnectionActionError;
import com.orm2_graph_library.action_errors.ObjectifiedPredicateIsConnectedToItsInnerPredicateActionError;
import com.orm2_graph_library.edges.RoleConstraintRelationEdge;
import com.orm2_graph_library.edges.RoleRelationEdge;
import com.orm2_graph_library.edges.SubtypingConstraintRelationEdge;
import com.orm2_graph_library.edges.SubtypingRelationEdge;
import com.orm2_graph_library.logic_errors.ConstraintHasNotEnoughConnectsLogicError;
import com.orm2_graph_library.logic_errors.EntityTypeWithNoneRefModeLogicError;
import com.orm2_graph_library.logic_errors.RoleHasNoTextSetLogicError;
import com.orm2_graph_library.nodes.common.EntityType;
import com.orm2_graph_library.nodes.common.ValueType;
import com.orm2_graph_library.nodes.constraints.*;
import com.orm2_graph_library.nodes.predicates.*;
import com.orm2_graph_library.post_validators.*;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO - @add :: Movement of anchor points when moving node or edge.

// TODO - @info  :: Method call map (Maybe components diagram).
// TODO - @clean :: Check accessibility of each components functionality from every point of code.
// TODO - @clean :: Change order of test expected and actual data.
// TODO - @clean :: Put "abstract" and "static" keywords of class members to the beginning.
// TODO - @clean && @check :: Warnings in code.

// TODO - @now :: Anchor points (connect nodes with methods; not with methods in the diagram).
// TODO - @now :: Getting geometry approximation.
// TODO - @now :: Edge hierarchy.

// @info :: Any method which introduces saved action produces only and only one saved action.

/*
    @info :: Levels of validation:

    1 pre-validation (at compile time);
    2 in-time validation (at runtime):
        2.1 Runtime Exceptions;
        2.2 Without notification;
        2.3 With notification.
    3 post-validation (after executing the actions; using validators).
 */

public class Diagram implements Serializable {
    // ================ ATTRIBUTES ================
    private final Map<Class, ArrayList<DiagramElement>> _innerElements        = new HashMap<>();

    protected ActionManager                             _actionManager        = new ActionManager(this);
    protected ArrayList<LogicError>                     _logicErrors          = new ArrayList<>();
    protected ArrayList<ActionErrorListener>            _actionErrorListeners = new ArrayList<>();

    protected boolean                                   _initDiagramElementsFlag = true;

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public void saveToXmlFile(String xmlFilePath, boolean isActionsHistorySaved) {
        this._removeNotConnectedRolesSequence(); // Current query to diagram removes roles sequence

        Diagram savedDiagram = (isActionsHistorySaved ? this._copyWithActionHistory() : this._copyWithoutActionHistory());

        try {
            XStream xstream = new XStream(new StaxDriver());
            xstream.addPermission(AnyTypePermission.ANY);

            FileOutputStream fileStream = new FileOutputStream(xmlFilePath);
            fileStream.write(xstream.toXML(savedDiagram).getBytes(StandardCharsets.UTF_8));
        }
        catch (Exception e) {
            throw new RuntimeException("ERROR :: attempt to save diagram to XML file (cause: " + e.getMessage() + ").");
        }
    }

    static public Diagram loadFromXmlFile(String xmlFilePath) {
        try {
            XStream xstream = new XStream(new StaxDriver());
            xstream.addPermission(AnyTypePermission.ANY);

            FileInputStream fileStream = new FileInputStream(xmlFilePath);

            Diagram diagram = (Diagram)xstream.fromXML(fileStream);
            // diagram.getElements(DiagramElement.class).forEach(e -> e.setOwnerDiagram(diagram));

            return diagram;
        }
        catch (Exception e) {
            throw new RuntimeException("ERROR :: attempt to load diagram from XML file (cause: " + e.getMessage() + ").");
        }
    }

    private Diagram _copyWithActionHistory() {
        Diagram result = new Diagram();
        result._innerElements.putAll(this._innerElements);
        result._actionManager = this._actionManager._copyWithoutListeners();
        result._logicErrors.addAll(this._logicErrors);

        return result;
    }

    private Diagram _copyWithoutActionHistory() {
        Diagram result = new Diagram();
        result._innerElements.putAll(this._innerElements);
        result._logicErrors.addAll(this._logicErrors);

        return result;
    }

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

        else if (node instanceof SubsetConstraint)      { this._actionManager.executeAction(new AddConstraintAction<>(this, (SubsetConstraint)node)); }
        else if (node instanceof EqualityConstraint)    { this._actionManager.executeAction(new AddConstraintAction<>(this, (EqualityConstraint)node)); }
        else if (node instanceof UniquenessConstraint)  { this._actionManager.executeAction(new AddConstraintAction<>(this, (UniquenessConstraint)node)); }
        else if (node instanceof ExclusionConstraint)   { this._actionManager.executeAction(new AddConstraintAction<>(this, (ExclusionConstraint)node)); }
        else if (node instanceof ExclusiveOrConstraint) { this._actionManager.executeAction(new AddConstraintAction<>(this, (ExclusiveOrConstraint)node)); }
        else if (node instanceof InclusiveOrConstraint) { this._actionManager.executeAction(new AddConstraintAction<>(this, (InclusiveOrConstraint)node)); }

        else {
            throw new RuntimeException("ERROR :: attempt to add incompatible type of node (" + node.getClass().descriptorString() + ").");
        }

        return node;
    }

    public void removeNode(Node node) {
        if      (node instanceof EntityType)            { this._actionManager.executeAction(new RemoveEntityTypeAction(this, (EntityType)node)); }
        else if (node instanceof ValueType)             { this._actionManager.executeAction(new RemoveValueTypeAction (this, (ValueType)node)); }
        else if (node instanceof Predicate)             { this._actionManager.executeAction(new RemovePredicateAction(this, (Predicate)node)); }
        else if (node instanceof ObjectifiedPredicate)  { this._actionManager.executeAction(new RemoveObjectifiedPredicateAction(this, (ObjectifiedPredicate)node)); }

        else if (node instanceof SubsetConstraint)      { this._actionManager.executeAction(new RemoveConstraintAction<>(this, (SubsetConstraint)node)); }
        else if (node instanceof EqualityConstraint)    { this._actionManager.executeAction(new RemoveConstraintAction<>(this, (EqualityConstraint)node)); }
        else if (node instanceof UniquenessConstraint)  { this._actionManager.executeAction(new RemoveConstraintAction<>(this, (UniquenessConstraint)node)); }
        else if (node instanceof ExclusionConstraint)   { this._actionManager.executeAction(new RemoveConstraintAction<>(this, (ExclusionConstraint)node)); }
        else if (node instanceof ExclusiveOrConstraint) { this._actionManager.executeAction(new RemoveConstraintAction<>(this, (ExclusiveOrConstraint)node)); }
        else if (node instanceof InclusiveOrConstraint) { this._actionManager.executeAction(new RemoveConstraintAction<>(this, (InclusiveOrConstraint)node)); }

        else {
            throw new RuntimeException("ERROR :: Try to remove incompatible type of node (" + node.getClass().descriptorString() + ").");
        }
    }

    public <T extends DiagramElement> Stream<T> getElements(Class<T> elementType) {
        if (this._innerElements.containsKey(elementType)) {
            return (Stream<T>)this._innerElements.get(elementType).stream();
        }
        else {
            Stream<T> result = Stream.empty();

            for (var diagramElementGroupInfo : this._innerElements.entrySet()) {
                if (elementType.isAssignableFrom(diagramElementGroupInfo.getKey())) {
                    result = Stream.concat(result, (Stream<T>)diagramElementGroupInfo.getValue().stream());
                }
            }

            return result;
        }
    }

    public boolean hasElements(Class<? extends DiagramElement> elementType)     { return this.getElements(elementType).findAny().isPresent(); }
    public boolean hasOnlyElements(Class<? extends DiagramElement> elementType) { return this.getElements(elementType).allMatch(e -> elementType.isAssignableFrom(e.getClass())); }

    public boolean isInnerElement(@NotNull DiagramElement diagramElement) {
        if (diagramElement.isOwnerDiagram(this)) {
            return this.getElements(diagramElement.getClass()).anyMatch(e -> e == diagramElement);
        }

        return false;
    }

    // * Connecting
    public <T extends EntityType, G extends EntityType> SubtypingRelationEdge connectBySubtypingRelation(AnchorPoint<T> beginEntityTypeAnchorPoint, AnchorPoint<G> endEntityTypeAnchorPoint) {
        SubtypingRelationEdge edge = new SubtypingRelationEdge((AnchorPoint<EntityType>)beginEntityTypeAnchorPoint, (AnchorPoint<EntityType>)endEntityTypeAnchorPoint);
        this._actionManager.executeAction(new ConnectBySubtypingRelationAction(this, edge));

        return edge;
    }

    public <T extends Role, G extends RoleParticipant> RoleRelationEdge connectByRoleRelation(AnchorPoint<T> roleAnchorPoint, AnchorPoint<G> roleParticipantAnchorPoint) {
        RoleRelationEdge edge = new RoleRelationEdge((AnchorPoint<Role>)roleAnchorPoint, (AnchorPoint<RoleParticipant>)roleParticipantAnchorPoint);
        this._actionManager.executeAction(new ConnectByRoleRelationAction(this, edge));

        return edge;
    }

    public <T extends Constraint, G extends SubtypingRelationEdge> SubtypingConstraintRelationEdge connectBySubtypingConstraintRelation(AnchorPoint<T> constraintAnchorPoint, AnchorPoint<G> subtypingRelationEdgeAnchorPoint) {
        SubtypingConstraintRelationEdge edge = new SubtypingConstraintRelationEdge((AnchorPoint<Constraint>)constraintAnchorPoint, (AnchorPoint<SubtypingRelationEdge>)subtypingRelationEdgeAnchorPoint);
        this._actionManager.executeAction(new ConnectBySubtypingConstraintRelationAction(this, edge));

        return edge;
    }

    public <T extends Constraint> RoleConstraintRelationEdge connectByRoleConstraintRelation(AnchorPoint<T> constraintAnchorPoint, RolesSequence rolesSequence) {
        RoleConstraintRelationEdge edge = new RoleConstraintRelationEdge((AnchorPoint<Constraint>)constraintAnchorPoint, rolesSequence.anchorPoint());
        this._actionManager.executeAction(new ConnectByRoleConstraintRelationAction(this, edge));

        return edge;
    }

    public void disconnectSubtypingRelation(EntityType begin, EntityType end)                            { this._actionManager.executeAction(new DisconnectSubtypingRelationAction(this, begin, end)); }
    public void disconnectRoleRelation(Role role, RoleParticipant roleParticipant)                       { this._actionManager.executeAction(new DisconnectRoleRelationAction(this, role, roleParticipant)); }
    public void disconnectSubtypingConstraintRelation(Constraint constraint, SubtypingRelationEdge edge) { this._actionManager.executeAction(new DisconnectSubtypingConstraintRelationAction(this, constraint, edge)); }
    public void disconnectRoleConstraintRelation(Constraint constraint, RolesSequence rolesSequence)     { this._actionManager.executeAction(new DisconnectRoleConstraintRelationAction(this, constraint, rolesSequence)); }

    public void disconnectRelation(EntityType begin, EntityType end)                  { this._actionManager.executeAction(new DisconnectSubtypingRelationAction(this, begin, end)); }
    public void disconnectRelation(Role role, RoleParticipant roleParticipant)        { this._actionManager.executeAction(new DisconnectRoleRelationAction(this, role, roleParticipant)); }
    public void disconnectRelation(Constraint constraint, SubtypingRelationEdge edge) { this._actionManager.executeAction(new DisconnectSubtypingConstraintRelationAction(this, constraint, edge)); }
    public void disconnectRelation(Constraint constraint, RolesSequence edge)         { this._actionManager.executeAction(new DisconnectRoleConstraintRelationAction(this, constraint, edge)); }
    public void disconnectRelation(Edge edge) {
        if (edge.begin() instanceof EntityType && edge.end() instanceof EntityType) {
            this._actionManager.executeAction(new DisconnectSubtypingRelationAction(this, (EntityType)edge.begin(), (EntityType)edge.end()));
        }
        else if (edge.begin() instanceof Role && edge.end() instanceof RoleParticipant) {
            this._actionManager.executeAction(new DisconnectRoleRelationAction(this, (Role)edge.begin(), (RoleParticipant)edge.end()));
        }
        else if (edge.begin() instanceof SubtypingRelationEdge && edge.end() instanceof Constraint) {
            this._actionManager.executeAction(new DisconnectSubtypingConstraintRelationAction(this, (Constraint)edge.begin(), (SubtypingRelationEdge)edge.end()));
        }
        else if (edge.begin() instanceof RolesSequence && edge.end() instanceof Constraint) {
            this._actionManager.executeAction(new DisconnectRoleConstraintRelationAction(this, (Constraint)edge.begin(), (RolesSequence)edge.end()));
        }
    }

    public <T extends EntityType> SubtypingRelationEdge reconnectBySubtypingRelation(AnchorPoint<T> beginEntityTypeAnchorPoint, SubtypingRelationEdge edge) {
        this._actionManager.executeAction(new ReconnectBySubtypingRelationAction(this, (AnchorPoint<EntityType>)beginEntityTypeAnchorPoint, edge));
        return edge;
    }

    public <T extends EntityType> SubtypingRelationEdge reconnectBySubtypingRelation(SubtypingRelationEdge edge, AnchorPoint<T> endEntityTypeAnchorPoint) {
        this._actionManager.executeAction(new ReconnectBySubtypingRelationAction(this, edge, (AnchorPoint<EntityType>)endEntityTypeAnchorPoint));
        return edge;
    }

    public <T extends Role> RoleRelationEdge reconnectByRoleRelation(AnchorPoint<T> roleAnchorPoint, RoleRelationEdge edge) {
        this._actionManager.executeAction(new ReconnectByRoleRelationAction(this, (AnchorPoint<Role>)roleAnchorPoint, edge));
        return edge;
    }

    public <T extends RoleParticipant> RoleRelationEdge reconnectByRoleRelation(RoleRelationEdge edge, AnchorPoint<T> roleParticipantAnchorPoint) {
        this._actionManager.executeAction(new ReconnectByRoleRelationAction(this, edge, (AnchorPoint<RoleParticipant>)roleParticipantAnchorPoint));
        return edge;
    }

    public <T extends Constraint, G extends SubtypingRelationEdge> SubtypingConstraintRelationEdge reconnectBySubtypingConstraintRelation(AnchorPoint<T> constraintAnchorPoint, SubtypingConstraintRelationEdge edge) {
        this._actionManager.executeAction(new ReconnectBySubtypingConstraintRelationAction(this, (AnchorPoint<Constraint>)constraintAnchorPoint, edge));
        return edge;
    }

    public <T extends SubtypingRelationEdge> SubtypingConstraintRelationEdge reconnectBySubtypingConstraintRelation(SubtypingConstraintRelationEdge edge, AnchorPoint<T> subtypingRelationEdgeAnchorPoint) {
        this._actionManager.executeAction(new ReconnectBySubtypingConstraintRelationAction(this, edge, (AnchorPoint<SubtypingRelationEdge>)subtypingRelationEdgeAnchorPoint));
        return edge;
    }

    public <T extends Constraint> RoleConstraintRelationEdge reconnectByRoleConstraintRelation(AnchorPoint<T> constraintAnchorPoint, RoleConstraintRelationEdge edge) {
        this._actionManager.executeAction(new ReconnectByRoleConstraintRelationAction(this, (AnchorPoint<Constraint>)constraintAnchorPoint, edge));
        return edge;
    }

    public RoleConstraintRelationEdge reconnectByRoleConstraintRelation(RoleConstraintRelationEdge edge, RolesSequence rolesSequence) {
        this._actionManager.executeAction(new ReconnectByRoleConstraintRelationAction(this, edge, rolesSequence.anchorPoint()));
        return edge;
    }

    // * Checking connections
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

    public <T extends Constraint> RoleConstraintRelationEdge getConnectByRoleConstraintRelation(T begin, RolesSequence end) {
        Stream<RoleConstraintRelationEdge> edgesStream = this.getElements(RoleConstraintRelationEdge.class).filter(e -> e.begin() == begin && e.end() == end);

        ArrayList<RoleConstraintRelationEdge> edges = edgesStream.collect(Collectors.toCollection(ArrayList::new));
        if (!edges.isEmpty()) { return edges.get(0); }
        else                  { throw new RuntimeException("ERROR :: Try to get non-existent subtyping relation edge between given diagram elements."); }
    }

    public <T extends Constraint, G extends SubtypingRelationEdge> SubtypingConstraintRelationEdge getConnectBySubtypingConstraintRelation(T begin, G end) {
        Stream<SubtypingConstraintRelationEdge> edgesStream = this.getElements(SubtypingConstraintRelationEdge.class).filter(e -> e.begin() == begin && e.end() == end);

        ArrayList<SubtypingConstraintRelationEdge> edges = edgesStream.collect(Collectors.toCollection(ArrayList::new));
        if (!edges.isEmpty()) { return edges.get(0); }
        else                  { throw new RuntimeException("ERROR :: Try to get non-existent subtyping relation edge between given diagram elements."); }
    }

    public <T extends Edge> T getConnectByRelation(DiagramElement begin, DiagramElement end, Class<T> edgeType) {
        Stream<T> edgesStream;

        if (edgeType == SubtypingRelationEdge.class) { edgesStream = this.getElements(edgeType).filter(e -> e.begin() == begin && e.end() == end); }
        else                                         { edgesStream = this.getElements(edgeType).filter(e -> e.begin() == begin && e.end() == end || e.begin() == end && e.end() == begin); }

        ArrayList<T> edges = edgesStream.collect(Collectors.toCollection(ArrayList::new));
        if (!edges.isEmpty()) { return edges.get(0); }
        else                  { throw new RuntimeException("ERROR :: Try to get non-existent relation edge between given diagram elements (" + edgeType + ")."); }
    }

    public <T extends EntityType, G extends EntityType>            boolean hasConnectBySubtypingRelation(T begin, G end)                  { return this.getElements(SubtypingRelationEdge.class)           .anyMatch(e -> e.begin() == begin && e.end() == end); }
    public <T extends Role, G extends RoleParticipant>             boolean hasConnectByRoleRelation(T begin, G end)                       { return this.getElements(RoleRelationEdge.class)                .anyMatch(e -> e.begin() == begin && e.end() == end); }
    public <T extends Constraint>                                  boolean hasConnectByRoleConstraintRelation(T begin, RolesSequence end) { return this.getElements(RoleConstraintRelationEdge.class)      .anyMatch(e -> e.begin() == begin && e.end() == end); }
    public <T extends Constraint, G extends SubtypingRelationEdge> boolean hasConnectBySubtypingConstraintRelation(T begin, G end)        { return this.getElements(SubtypingConstraintRelationEdge.class) .anyMatch(e -> e.begin() == begin && e.end() == end); }
    public <T extends DiagramElement, G extends DiagramElement>    boolean hasConnectByAnyRelation(T begin, G end)                        { return this.getElements(Edge.class)                            .anyMatch(e -> e.begin() == begin && e.end() == end); }

    public boolean hasConnectByRelation(DiagramElement begin, DiagramElement end, Class<? extends Edge> edgeType) { return this.getElements(edgeType).anyMatch(e -> e.begin() == begin && e.end() == end); }

    // * Undo & redo state
    public boolean canUndoState() { return this._actionManager.canUndo(); }
    public void    undoState()    { this._actionManager.undo(); }
    public boolean canRedoState() { return this._actionManager.canRedo(); }
    public void    redoState()    { this._actionManager.redo(); }

    // -------------- sub-operations --------------
    protected <T extends DiagramElement> T _addElement(T element) {
        if (element.isOwnerDiagram(this)) {
            throw new RuntimeException("ERROR :: attempt to add diagram element to the diagram when it is already added.");
        }

        element.setOwnerDiagram(this);

        if (!this._innerElements.containsKey(element.getClass())) {
            this._innerElements.put(element.getClass(), new ArrayList<>());
        }

        if (element instanceof Edge) {
            if (!((Edge)element).begin().isOwnerDiagram(this) || !((Edge)element).end().isOwnerDiagram(this)) {
                throw new RuntimeException("ERROR :: attempt to add connection between elements that is added to different diagram.");
            }
        }

        this._innerElements.get(element.getClass()).add(element);

        return element;
    }

    protected void _removeElement(DiagramElement element) {
        if (!element.isOwnerDiagram(this)) {
            throw new RuntimeException("ERROR :: attempt to remove diagram element from the diagram when it is not in the diagram.");
        }

        element.unsetOwnerDiagram();
        this._innerElements.getOrDefault(element.getClass(), new ArrayList<>()).remove(element);
    }

    private void _removeNotConnectedRolesSequence() {
        if (this._innerElements.containsKey(RolesSequence.class)) {
            ArrayList<RolesSequence> rolesSequences = this.getElements(RolesSequence.class)
                    .filter(e -> !e.isUnique())
                    .filter(e -> !e.hasIncidentElements(DiagramElement.class))
                    .collect(Collectors.toCollection(ArrayList::new));

            for (var roleSequence : rolesSequences) {
                this._removeElement(roleSequence);
            }
        }
    }

    // ------------------ events ------------------
    public void addActionListener(@NotNull ActionListener actionListener) {
        this._actionManager._actionListeners.add(actionListener);
    }

    // ================= SUBTYPES =================
    abstract public class AddNodeAction<T extends Node> extends Action {
        protected final T _node;

        private AddNodeAction(Diagram diagram, @NotNull T node) {
            super(diagram);
            this._node = node;
        }

        public T node() { return this._node; }

        @Override public void _execute() { this._diagram._addElement(this._node); }
        @Override public void _undo()    { this._diagram._removeElement(this._node); }
    }

    public class AddEntityTypeAction extends AddNodeAction<EntityType> {
        private AddEntityTypeAction(Diagram diagram, @NotNull EntityType node) {
            super(diagram, node);
            this._emergedLogicErrors.add(new EntityTypeWithNoneRefModeLogicError(node));
        }
    }

    public class AddValueTypeAction extends AddNodeAction<ValueType> {
        private AddValueTypeAction(Diagram diagram, @NotNull ValueType node) { super(diagram, node); }
    }

    public class AddPredicateAction extends AddNodeAction<Predicate> {
        private AddPredicateAction(Diagram diagram, @NotNull Predicate node) {
            super(diagram, node);
            this._node.roles().forEach(e -> this._emergedLogicErrors.add(new RoleHasNoTextSetLogicError(e)));
        }

        @Override
        public void _execute() {
            super._execute();
            this._node.roles().forEach(e -> this._diagram._addElement(e));
        }

        @Override
        public void _undo() {
            super._undo();
            this._node.roles().forEach(Diagram.this::_removeElement);
        }
    }

    public class AddObjectifiedPredicateAction extends AddNodeAction<ObjectifiedPredicate> {
        final private boolean _isPredicateAlreadyAdded;

        private AddObjectifiedPredicateAction(Diagram diagram, @NotNull ObjectifiedPredicate node) {
            super(diagram, node);

            this._isPredicateAlreadyAdded = node.innerPredicate().hasOwnerDiagram();
        }

        @Override
        public void _execute() {
            super._execute();

            if (!this._isPredicateAlreadyAdded) {
                this._diagram._addElement(this._node.innerPredicate());
                this._node.innerPredicate().roles().forEach(e -> {
                    this._diagram._addElement(e);
                    this._emergedLogicErrors.add(new RoleHasNoTextSetLogicError(e));
                });
            }
        }

        @Override
        public void _undo() {
            super._undo();

            if (!this._isPredicateAlreadyAdded) {
                this._diagram._removeElement(this._node.innerPredicate());
                this._node.innerPredicate().roles().forEach(Diagram.this::_removeElement);
            }
        }
    }

    public class AddConstraintAction<G extends Constraint> extends AddNodeAction<G> {
        public AddConstraintAction(Diagram diagram, @NotNull G node) {
            super(diagram, node);
            this._emergedLogicErrors.add(new ConstraintHasNotEnoughConnectsLogicError(node, new ArrayList<>()));
        }
    }

    public abstract class RemoveNodeAction<T extends Node> extends Action {
        protected final T               _node;
        protected final ArrayList<Edge> _incidentEdges;

        private RemoveNodeAction(Diagram diagram, @NotNull T node) {
            super(diagram);

            this._node          = node;
            this._incidentEdges = node.getIncidentElements(Edge.class).collect(Collectors.toCollection(ArrayList::new));

            // TODO - @check :: Does we always need to remove logic errors which has removed diagram elements as error participants?
            this._solvedLogicErrors.addAll(diagram.getLogicErrorsFor(node).collect(Collectors.toCollection(ArrayList::new)));
        }

        public T node() { return this._node; }
        public Stream<Edge> incidentEdges() { return this._incidentEdges.stream(); }

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

    public class RemoveEntityTypeAction extends RemoveNodeAction<EntityType> {
        private RemoveEntityTypeAction(Diagram diagram, @NotNull EntityType node) { super(diagram, node); }
    }

    public class RemoveValueTypeAction extends RemoveNodeAction<ValueType> {
        private RemoveValueTypeAction(Diagram diagram, @NotNull ValueType node) { super(diagram, node); }
    }

    public class RemovePredicateAction extends RemoveNodeAction<Predicate> {
        private RemovePredicateAction(Diagram diagram, @NotNull Predicate node) {
            super(diagram, node);

            if (node.hasOwnerObjectifiedPredicate() && node.ownerObjectifiedPredicate().hasOwnerDiagram()) {
                throw new RuntimeException("ERROR :: Try to remove predicate which has non-removed owner on the diagram.");
            }
        }

        @Override
        public void _execute() {
            super._execute();
            this._node.roles().forEach(e -> this._diagram._removeElement(e));
        }

        @Override
        public void _undo() {
            super._undo();
            this._node.roles().forEach(Diagram.this::_addElement);
        }
    }

    public class RemoveObjectifiedPredicateAction extends RemoveNodeAction<ObjectifiedPredicate> {
        private RemoveObjectifiedPredicateAction(Diagram diagram, @NotNull ObjectifiedPredicate node) { super(diagram, node); }

        @Override
        public void _execute() {
            super._execute();
            this._diagram._removeElement(this._node.innerPredicate());
            this._node.innerPredicate().roles().forEach(e -> {
                this._solvedLogicErrors.addAll(e.getLogicErrors(RoleHasNoTextSetLogicError.class).collect(Collectors.toCollection(ArrayList::new)));
                this._diagram._removeElement(e);
            });
        }

        @Override
        public void _undo() {
            super._undo();
            this._diagram._addElement(this._node.innerPredicate());
            this._node.innerPredicate().roles().forEach(Diagram.this::_addElement);
        }
    }

    public class RemoveConstraintAction<G extends Constraint> extends RemoveNodeAction<G> {
        private RemoveConstraintAction(Diagram diagram, @NotNull G node) { super(diagram, node); }
    }

    abstract public class ConnectAction<T extends DiagramElement, G extends DiagramElement> extends Action {
        protected final Edge<T, G> _edge;

        private ConnectAction(@NotNull Diagram diagram, @NotNull Edge<T, G> edge) {
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

        public Edge<T, G> edge() { return this._edge; }

        @Override public void _execute() { this._diagram._addElement(this._edge); }
        @Override public void _undo()    { this._diagram._removeElement(this._edge); }
    }

    public class ConnectBySubtypingRelationAction extends ConnectAction<EntityType, EntityType> {
        private ConnectBySubtypingRelationAction(@NotNull Diagram diagram, @NotNull SubtypingRelationEdge edge) {
            super(diagram, edge);
            this._postValidators.add(new ConnectBySubtypingRelationPostValidator(diagram, this, edge.beginAnchorPoint().owner()));
        }
    }

    public class ConnectByRoleRelationAction extends ConnectAction<Role, RoleParticipant> {
        private ConnectByRoleRelationAction(@NotNull Diagram diagram, @NotNull RoleRelationEdge edge) {
            super(diagram, edge);

            if (edge.end() instanceof ObjectifiedPredicate edgeEndConverted && edge.begin().ownerPredicate() == edgeEndConverted.innerPredicate()) {
                this._throwActionError(new ObjectifiedPredicateIsConnectedToItsInnerPredicateActionError((ObjectifiedPredicate)edge.end()));
            }
        }
    }

    public class ConnectBySubtypingConstraintRelationAction extends ConnectAction<Constraint, SubtypingRelationEdge> {
        private ConnectBySubtypingConstraintRelationAction(@NotNull Diagram diagram, @NotNull SubtypingConstraintRelationEdge edge) {
            super(diagram, edge);

            this._postValidators.add(new ConnectByGlobalConstraintRelationPostValidator(diagram, this, edge.begin(), edge));
            this._postValidators.add(new ConnectBySubtypingConstraintRelationPostValidator(diagram, this, edge.begin(), edge));
        }
    }

    public class ConnectByRoleConstraintRelationAction extends ConnectAction<Constraint, RolesSequence> {
        private ConnectByRoleConstraintRelationAction(@NotNull Diagram diagram, @NotNull RoleConstraintRelationEdge edge) {
            super(diagram, edge);

            this._postValidators.add(new ConnectByGlobalConstraintRelationPostValidator(diagram, this, edge.begin(), edge));
            this._postValidators.add(new ConnectByRoleConstraintRelationPostValidator(diagram, this, edge.begin(), edge));
        }
    }

    abstract public class DisconnectAction<T extends DiagramElement, G extends DiagramElement> extends Action {
        final protected Edge<T, G> _edge;

        private DisconnectAction(@NotNull Diagram diagram, @NotNull Edge<T, G> edge) {
            super(diagram);
            this._edge = edge;

            // TODO - @check :: Does we always need to remove logic errors which has removed diagram elements as error participants?
            this._solvedLogicErrors.addAll(diagram.getLogicErrorsFor(edge).collect(Collectors.toCollection(ArrayList::new)));
        }

        public Edge<T, G> edge() { return this._edge; }

        @Override public void _execute() { this._diagram._removeElement(this._edge); }
        @Override public void _undo()    { this._diagram._addElement(this._edge); }
    }

    public class DisconnectSubtypingRelationAction extends DisconnectAction<EntityType, EntityType> {
        final private ArrayList<Edge> _connectedEdges;

        private DisconnectSubtypingRelationAction(@NotNull Diagram diagram, @NotNull EntityType begin, @NotNull EntityType end) {
            super(diagram, diagram.getConnectBySubtypingRelation(begin, end));
            this._connectedEdges = this._edge.getIncidentElements(SubtypingConstraintRelationEdge.class).collect(Collectors.toCollection(ArrayList::new));

            this._postValidators.add(new DisconnectSubtypingRelationPostValidator(diagram, this, (SubtypingRelationEdge)this._edge));
        }

        @Override
        public void _execute() {
            super._execute();
            for (Edge edge : this._connectedEdges) { this._diagram._removeElement(edge); }
        }

        @Override
        public void _undo() {
            super._undo();
            for (Edge edge : this._connectedEdges) { this._diagram._addElement(edge); }
        }
    }

    public class DisconnectRoleRelationAction extends DisconnectAction<Role, RoleParticipant> {
        private DisconnectRoleRelationAction(@NotNull Diagram diagram, @NotNull Role role, @NotNull RoleParticipant roleParticipant) {
            super(diagram, diagram.getConnectByRoleRelation(role, roleParticipant));
        }
    }

    public class DisconnectSubtypingConstraintRelationAction extends DisconnectAction<Constraint, SubtypingRelationEdge> {
        private DisconnectSubtypingConstraintRelationAction(@NotNull Diagram diagram, @NotNull Constraint constraint, @NotNull SubtypingRelationEdge edge) {
            super(diagram, diagram.getConnectBySubtypingConstraintRelation(constraint, edge));
            this._postValidators.add(new DisconnectGlobalConstraintRelationPostValidator(diagram, this, constraint, this._edge));
        }
    }

    public class DisconnectRoleConstraintRelationAction extends DisconnectAction<Constraint, RolesSequence> {
        private ArrayList<RoleConstraintRelationEdge> _subsetConstraintEdges                = null;
        private ArrayList<Boolean>                    _subsetConstraintEdgesOldIsEndingEdge = null;

        private DisconnectRoleConstraintRelationAction(@NotNull Diagram diagram, @NotNull Constraint constraint, @NotNull RolesSequence rolesSequence) {
            super(diagram, diagram.getConnectByRoleConstraintRelation(constraint, rolesSequence));
            this._postValidators.add(new DisconnectGlobalConstraintRelationPostValidator(diagram, this, constraint, this._edge));

            if (constraint instanceof SubsetConstraint) {
                this._subsetConstraintEdges = constraint.getIncidentElements(RoleConstraintRelationEdge.class).filter(e -> e != this._edge).collect(Collectors.toCollection(ArrayList::new));
                this._subsetConstraintEdges.add((RoleConstraintRelationEdge)this._edge);

                this._subsetConstraintEdgesOldIsEndingEdge = this._subsetConstraintEdges.stream().map(e -> e.isEndingEdge()).collect(Collectors.toCollection(ArrayList::new));
            }
        }

        @Override
        public void _execute() {
            super._execute();
        }

        @Override
        public void _undo() {
            super._undo();

            if (this._subsetConstraintEdges != null) {
                for (int i=0; i<this._subsetConstraintEdges.size(); i++) { this._subsetConstraintEdges.get(i)._setIsEndingEdge(_subsetConstraintEdgesOldIsEndingEdge.get(i)); }
            }
        }
    }

    abstract public class ReconnectAction<T extends DiagramElement, G extends DiagramElement> extends Action {
        final protected AnchorPoint<T> _newBeginAnchorPoint;
        final protected AnchorPoint<G> _newEndAnchorPoint;
        final protected AnchorPoint<T> _oldBeginAnchorPoint;
        final protected AnchorPoint<G> _oldEndAnchorPoint;
        final protected Edge<T, G>     _edge;

        private ReconnectAction(@NotNull Diagram diagram, @NotNull AnchorPoint<T> beginAnchorPoint, @NotNull Edge<T, G> edge) {
            super(diagram);
            this._edge                = edge;
            this._newBeginAnchorPoint = beginAnchorPoint;
            this._newEndAnchorPoint   = null;
            this._oldBeginAnchorPoint = edge.beginAnchorPoint();
            this._oldEndAnchorPoint   = edge.endAnchorPoint();

            this._solvedLogicErrors.addAll(diagram.getLogicErrorsFor(edge).collect(Collectors.toCollection(ArrayList::new)));

            // Check if edge connects diagram element with itself
            if (this._edge.end() == this._newBeginAnchorPoint.owner()) {
                this._throwActionError(new DiagramElementSelfConnectedActionError(this._edge.beginAnchorPoint().owner()));
            }

            // Check if edge connects diagram elements twice
            var existEdges = this._diagram.getElements(Edge.class)
                    .filter(e -> e.begin() == this._newBeginAnchorPoint.owner() && e.end() == this._edge.end() ||
                            e.begin() == this._edge.end() && e.end() == this._newBeginAnchorPoint.owner())
                    .collect(Collectors.toCollection(ArrayList::new));

            if (!existEdges.isEmpty() && !(existEdges.get(0) instanceof SubtypingRelationEdge && existEdges.get(0).isOppositeTo(this._edge))) {
                this._throwActionError(new DoubleConnectionActionError(this._edge.beginAnchorPoint().owner(), this._edge.endAnchorPoint().owner(), existEdges.get(0)));
            }
        }

        private ReconnectAction(@NotNull Diagram diagram, @NotNull Edge<T, G> edge, @NotNull AnchorPoint<G> endAnchorPoint) {
            super(diagram);
            this._edge                = edge;
            this._newBeginAnchorPoint = null;
            this._newEndAnchorPoint   = endAnchorPoint;
            this._oldBeginAnchorPoint = edge.beginAnchorPoint();
            this._oldEndAnchorPoint   = edge.endAnchorPoint();

            this._solvedLogicErrors.addAll(diagram.getLogicErrorsFor(edge).collect(Collectors.toCollection(ArrayList::new)));

            // Check if edge connects diagram element with itself
            if (this._edge.begin() == this._newEndAnchorPoint.owner()) {
                this._throwActionError(new DiagramElementSelfConnectedActionError(this._edge.beginAnchorPoint().owner()));
            }

            // Check if edge connects diagram elements twice
            var existEdges = this._diagram.getElements(Edge.class)
                    .filter(e -> e.begin() == this._edge.begin() && e.end() == this._newEndAnchorPoint.owner() ||
                        e.begin() == this._newEndAnchorPoint.owner() && e.end() == this._edge.begin())
                    .collect(Collectors.toCollection(ArrayList::new));

            if (!existEdges.isEmpty() && !(existEdges.get(0) instanceof SubtypingRelationEdge && existEdges.get(0).isOppositeTo(this._edge))) {
                this._throwActionError(new DoubleConnectionActionError(this._edge.beginAnchorPoint().owner(), this._edge.endAnchorPoint().owner(), existEdges.get(0)));
            }
        }

        public Edge<T, G> edge() { return this._edge; }

        @Override public void _execute() {
            if (this._newBeginAnchorPoint != null) {
                this._edge._setBeginAnchorPoint(this._newBeginAnchorPoint);
            }
            else if (this._newEndAnchorPoint != null) {
                this._edge._setEndAnchorPoint(this._newEndAnchorPoint);
            }
        }

        @Override public void _undo() {
            if (this._newBeginAnchorPoint != null) {
                this._edge._setBeginAnchorPoint(this._oldBeginAnchorPoint);
            }
            else if (this._newEndAnchorPoint != null) {
                this._edge._setEndAnchorPoint(this._oldEndAnchorPoint);
            }
        }
    }

    public class ReconnectBySubtypingRelationAction extends ReconnectAction<EntityType, EntityType> {
        private ReconnectBySubtypingRelationAction(@NotNull Diagram diagram, @NotNull AnchorPoint<EntityType> beginEntityTypeAnchorPoint, @NotNull SubtypingRelationEdge edge) { super(diagram, beginEntityTypeAnchorPoint, edge); }
        private ReconnectBySubtypingRelationAction(@NotNull Diagram diagram, @NotNull SubtypingRelationEdge edge, @NotNull AnchorPoint<EntityType> endEntityTypeAnchorPoint)   { super(diagram, edge, endEntityTypeAnchorPoint); }
    }

    public class ReconnectByRoleRelationAction extends ReconnectAction<Role, RoleParticipant> {
        private ReconnectByRoleRelationAction(@NotNull Diagram diagram, @NotNull AnchorPoint<Role> roleAnchorPoint, @NotNull RoleRelationEdge edge)                       { super(diagram, roleAnchorPoint, edge); }
        private ReconnectByRoleRelationAction(@NotNull Diagram diagram, @NotNull RoleRelationEdge edge, @NotNull AnchorPoint<RoleParticipant> roleParticipantAnchorPoint) { super(diagram, edge, roleParticipantAnchorPoint); }
    }

    public class ReconnectBySubtypingConstraintRelationAction extends ReconnectAction<Constraint, SubtypingRelationEdge> {
        private ReconnectBySubtypingConstraintRelationAction(@NotNull Diagram diagram, @NotNull AnchorPoint<Constraint> constraintAnchorPoint, @NotNull SubtypingConstraintRelationEdge edge)                       { super(diagram, constraintAnchorPoint, edge); }
        private ReconnectBySubtypingConstraintRelationAction(@NotNull Diagram diagram, @NotNull SubtypingConstraintRelationEdge edge, @NotNull AnchorPoint<SubtypingRelationEdge> subtypingRelationEdgeAnchorPoint) { super(diagram, edge, subtypingRelationEdgeAnchorPoint); }
    }

    public class ReconnectByRoleConstraintRelationAction extends ReconnectAction<Constraint, RolesSequence> {
        private ReconnectByRoleConstraintRelationAction(@NotNull Diagram diagram, @NotNull AnchorPoint<Constraint> constraintAnchorPoint, @NotNull RoleConstraintRelationEdge edge) {
            super(diagram, constraintAnchorPoint, edge);
        }

        private ReconnectByRoleConstraintRelationAction(@NotNull Diagram diagram, @NotNull RoleConstraintRelationEdge edge, @NotNull AnchorPoint<RolesSequence> rolesSequenceAnchorPoint) {
            super(diagram, edge, rolesSequenceAnchorPoint);
        }
    }

    abstract static public class DiagramElementAttributeChangeAction extends Action {
        final protected DiagramElement _diagramElement;
        final protected Object         _oldAttributeValue;
        final protected Object         _newAttributeValue;

        protected DiagramElementAttributeChangeAction(@NotNull Diagram diagram, @NotNull DiagramElement diagramElement, @NotNull Object oldAttributeValue, @NotNull Object newAttributeValue) {
            super(diagram);

            this._diagramElement    = diagramElement;
            this._oldAttributeValue = oldAttributeValue;
            this._newAttributeValue = newAttributeValue;

            // Check if object type's old name is the same as new name
            if (this._oldAttributeValue.equals(this._newAttributeValue)) { this._becomeInvalid(); }
        }
    }
}

/*

    X1	Уникальность имён среди узлов                                                               Узлы типа «Entity Type», «Value Type», «Objectified Predicate» должны иметь уникальные имена

    2	Идентификаторы Entity Type                                                                  Узлы типа «Entity Type» должны иметь идентифицирующее значение, либо заданное через
                                                                                                    Expand Ref Mode или Ref Mode, либо полученное от другого «Entity Type» узла путем соединения
                                                                                                    к ним через дугу «Subtype Connector»

    3	Тип данных Value Type                                                                       Узлы типа «Value Type» должны иметь типы данных

    4	Ограничения на значение у Entity Type                                                       У узлов типа «Entity Type» и «Value Type» ограничения на возможные значения, если они заданы,
                                                                                                    должны соответствовать выбранному типу данных возможных значений

    5	Задание предикатов                                                                          Узлы типа «N-nary Predicate» должны содержать описание предиката

    6	Наследование идентифицирующего значения Entity Type только у одного Entity Type             Если задано несколько потомков у узла «Entity Type» через дугу «Subtype Connector»,
                                                                                                    через которые есть данный узел может получить несколько типов идентифицирующего
                                                                                                    значения, то должна быть только одна дуга из этих дуг, у которой выставлен признак «Is Identification Path»

    7	Ограничения на последовательности Role узлов                                                По средством Constraint узлов можно соединять последовательности Role узлов только одинаковой длины, если их
                                                                                                    несколько. Также каждая последовательность должна состоять из Role узлов, принадлежащих только одному Predicate узлу

    X8	Ограничение на уникальность                                                                 Уникальность последовательности Role узлов должна быть задана либо через «External Uniqueness Constraint»,
                                                                                                    либо через указания у Predicate узла

    9	Наследование только зависимых Entity Type                                                   Только узлы «Entity Type», у которых не выставлен признак «Is Independent» могут быть подтипом другого «Entity Type»
                                                                                                    посредством соединения с помощью дуги «Subtype Connector»

    10	Взаимоисключающие друг друга потомки                                                        Узел «Entity Type» не может наследоваться от взаимоисключающих друг друга узлов «Entity Type». Иными словами,
                                                                                                    результирующее множество объектов данного «Entity Type» не может быть пустым

    11	Одновременное непосредственное наследование и опосредованное наследование Entity Type       Узел «Entity Type» наследоваться от другого узла «Entity Type» и непосредственно, и опосредованно
                                                                                                    через другие узлы «Entity Type»

    12	Связь Role узла с Entity Type, Value Type, Objectified Predicate                            Role узел должен иметь связь и только одну связь с узлом «Entity Type», «Value Type» или «Objectified Predicate»

    13	Ограничения на обязательность и исключение противоречат друг другу                          Не должно быть противоречий между ограничением у последовательности Role узлов на обязательность наличия и ограничением на исключение данной последовательности

    14	Ограничения на вложенность и исключение противоречат друг другу                             Не должно быть противоречий между ограничением у последовательности Role узлов на вложенность другой последовательности и ограничением на взаимное исключение данных последовательностей

    15	Ограничения на равенство и исключение противоречат друг другу                               Не должно быть противоречий между ограничением у последовательности Role узлов на равенство другой последовательности и ограничением на взаимное исключение данных последовательностей

 */
