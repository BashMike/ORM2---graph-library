package com.orm2_graph_library.core;

import com.orm2_graph_library.edges.SubtypeConnectorEdge;
import com.orm2_graph_library.nodes.ObjectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Diagram {
    // ================ ATTRIBUTES ================
    private final ArrayList<DiagramElement> _innerElements = new ArrayList<>();

    protected final ActionManager _actionManager = new ActionManager();
    // TODO - @add :: Make post-validation process after executing some action, undo and redo state of the diagram.

    // ================ OPERATIONS ================
    // ----------------- contract -----------------
    public <T extends Node> T addNode(T node) {
        this._actionManager.addAction(new AddNodeAction(this, node));

        this._addElement(node);
        return node;
    }
    public void removeNode(Node node) {
        this._actionManager.addAction(new RemoveNodeAction(this, node));
        this._removeElement(node);
    }

    public <T extends ObjectType, G extends ObjectType> SubtypeConnectorEdge connectWithSubtypeConnector(T begin, G end) {
        SubtypeConnectorEdge edge = new SubtypeConnectorEdge(begin, end);
        ((Edge)edge).setOwner(this);
        this._innerElements.add(edge);

        return edge;
    }

    public void connectWithRoleConnector(Node begin, Node end) {
    }

    public void connectWithConstraintConnector(Node begin, Node end) {
    }

    public <T extends DiagramElement> ArrayList<T> getElements(Class<T> elementType) {
        Stream<T>    stream = (Stream<T>)this._innerElements.stream().filter(elem -> elementType.isAssignableFrom(elem.getClass()));
        ArrayList<T> result = new ArrayList<>( stream.collect(Collectors.toList()) );

        return result;
    }

    // Undo & redo state
    public boolean canUndoState() { return this._actionManager.canUndo(); }
    public void undoState()       { this._actionManager.undo(); }
    public boolean canRedoState() { return this._actionManager.canRedo(); }
    public void redoState()       { this._actionManager.redo(); }

    // TODO - @robust :: Add non-public connection to the action manager (object type uses it to disable recording action of setting its name).
    public ActionManager _actionManager() { return this._actionManager; }

    // -------------- sub-operations --------------
    <T extends DiagramElement> T _addElement(T element) {
        element.setOwner(this);
        this._innerElements.add(element);

        return element;
    }

    public void _removeElement(DiagramElement element) {
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

    private abstract class ConnectAction extends Action {
        private final Edge _edge;

        public ConnectAction(Diagram diagram, @NotNull Edge edge) {
            super(diagram);
            this._edge = edge;
        }

        @Override
        public void _execute() { this._diagram._removeElement(this._edge); }
        @Override
        public void _undo() { this._diagram._addElement(this._edge); }
    }

    private class ConnectSubtypeAction extends ConnectAction {
        public ConnectSubtypeAction(Diagram diagram, @NotNull ObjectType begin, @NotNull ObjectType end) {
            super(diagram, new SubtypeConnectorEdge(begin, end));
        }
    }
}
