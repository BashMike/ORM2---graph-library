package com.orm2_graph_library.core;

import com.orm2_graph_library.action_errors.DiagramElementSelfConnectedActionError;
import com.orm2_graph_library.action_errors.DoubleConnectionActionError;
import com.orm2_graph_library.edges.RoleConstraintRelationEdge;
import com.orm2_graph_library.edges.RoleRelationEdge;
import com.orm2_graph_library.edges.SubtypingConstraintRelationEdge;
import com.orm2_graph_library.edges.SubtypingRelationEdge;
import com.orm2_graph_library.nodes.common.EntityType;
import com.orm2_graph_library.nodes.constraints.Constraint;
import com.orm2_graph_library.nodes.predicates.Role;
import com.orm2_graph_library.nodes.predicates.RoleParticipant;
import com.orm2_graph_library.nodes.predicates.RolesSequence;
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
    public ArrayList<LogicError> logicErrors() { return new ArrayList<>(this._logicErrors); }

    void _addLogicError(@NotNull LogicError logicError) { this._logicErrors.add(logicError); }
    void _removeLogicError(@NotNull LogicError logicError) { this._logicErrors.remove(logicError); }

    public void addActionErrorListener(ActionErrorListener actionErrorListener) { this._actionErrorListeners.add(actionErrorListener); }

    // ----------------- contract -----------------
    public <T extends Node> T addNode(T node) {
        this._actionManager.executeAction(new AddNodeAction(this, node));

        return node;
    }

    public void removeNode(Node node) { this._actionManager.executeAction(new RemoveNodeAction(this, node)); }

    public <T extends EntityType, G extends EntityType> SubtypingRelationEdge connectBySubtypingRelation(AnchorPoint<T> begin, AnchorPoint<G> end) {
        SubtypingRelationEdge edge = new SubtypingRelationEdge((AnchorPoint<EntityType>)begin, (AnchorPoint<EntityType>)end);
        this._actionManager.executeAction(new ConnectBySubtypeRelationAction(this, edge));

        return edge;
    }

    public <T extends Role, G extends RoleParticipant> RoleRelationEdge connectByRoleRelation(AnchorPoint<T> begin, AnchorPoint<G> end) {
        RoleRelationEdge edge = new RoleRelationEdge((AnchorPoint<Role>)begin, (AnchorPoint<RoleParticipant>)end);
        this._actionManager.executeAction(new ConnectByRoleRelationAction(this, edge));

        return edge;
    }

    public <T extends Constraint> RoleConstraintRelationEdge connectByRoleConstraintRelation(RolesSequence begin, AnchorPoint<T> end) {
        RoleConstraintRelationEdge edge = new RoleConstraintRelationEdge(begin.anchorPoint(), (AnchorPoint<Constraint>)end);
        this._actionManager.executeAction(new ConnectByRoleConstraintRelationAction(this, edge));

        return edge;
    }

    public <T extends SubtypingRelationEdge, G extends Constraint> SubtypingConstraintRelationEdge connectBySubtypingConstraintRelation(AnchorPoint<T> begin, AnchorPoint<G> end) {
        SubtypingConstraintRelationEdge edge = new SubtypingConstraintRelationEdge((AnchorPoint<SubtypingRelationEdge>)begin, (AnchorPoint<Constraint>)end);
        this._actionManager.executeAction(new ConnectBySubtypingConstraintRelationAction(this, edge));

        return edge;
    }

    public <T extends DiagramElement> Stream<T> getElements(Class<T> elementType) {
        return (Stream<T>)this._innerElements.stream().filter(elem -> elementType.isAssignableFrom(elem.getClass()));
    }

    // Undo & redo state
    public boolean canUndoState() { return this._actionManager.canUndo(); }
    public void undoState()       { this._actionManager.undo(); }
    public boolean canRedoState() { return this._actionManager.canRedo(); }
    public void redoState()       { this._actionManager.redo(); }

    // TODO - @structure :: Add non-public connection to the action manager (object type uses it to disable recording action of setting its name).
    public ActionManager _actionManager() { return this._actionManager; }

    // -------------- sub-operations --------------
    private <T extends DiagramElement> T _addElement(T element) {
        element.setOwner(this);
        this._innerElements.add(element);

        return element;
    }

    private void _removeElement(DiagramElement element) {
        element.unsetOwner();
        this._innerElements.remove(element);
    }

    // ================= SUBTYPES =================
    private class AddNodeAction extends Action {
        private final Node _node;

        public AddNodeAction(Diagram diagram, @NotNull Node node) {
            super(diagram);
            this._node = node;
        }

        @Override
        public void _execute() { this._diagram._addElement(this._node); }
        @Override
        public void _undo() { this._diagram._removeElement(this._node); }
    }

    private class RemoveNodeAction extends Action {
        private final Node            _node;
        private final ArrayList<Edge> _incidentEdges;

        public RemoveNodeAction(Diagram diagram, @NotNull Node node) {
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

    private abstract class ConnectAction<T extends DiagramElement, G extends DiagramElement> extends Action {
        protected final Edge<T, G> _edge;

        public ConnectAction(@NotNull Diagram diagram, @NotNull Edge<T, G> edge) {
            super(diagram);
            this._edge = edge;
        }

        @Override
        public void _execute() {
            // Check if edge connects diagram element with itself
            if (this._edge.beginAnchorPoint().owner() == this._edge.endAnchorPoint().owner()) {
                this._throwActionError(new DiagramElementSelfConnectedActionError(this._edge.beginAnchorPoint().owner()));
                return;
            }

            // Check if edge connects diagram elements twice
            var existEdges = this._diagram.getElements(Edge.class).filter(e -> e.isSameTo(this._edge) || e.isOppositeTo(this._edge)).collect(Collectors.toCollection(ArrayList::new));

            if (!existEdges.isEmpty() && !(existEdges.get(0) instanceof SubtypingRelationEdge && existEdges.get(0).isOppositeTo(this._edge))) {
                this._throwActionError(new DoubleConnectionActionError(this._edge.beginAnchorPoint().owner(), this._edge.endAnchorPoint().owner(), existEdges.get(0)));
                return;
            }

            this._diagram._addElement(this._edge);
        }

        @Override
        public void _undo() { this._diagram._removeElement(this._edge); }
    }

    private class ConnectBySubtypeRelationAction extends ConnectAction {
        public ConnectBySubtypeRelationAction(@NotNull Diagram diagram, @NotNull SubtypingRelationEdge edge) {
            super(diagram, edge);
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
