package com.orm2_graph_library.core;

import com.orm2_graph_library.edges.SubtypeConnectorEdge;
import com.orm2_graph_library.nodes.ObjectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Diagram {
    // ================ ATTRIBUTES ================
    private ArrayList<DiagramElement> _innerElements = new ArrayList<>();

    private ActionManager _actionManager = new ActionManager();
    // TODO(@add) :: Make undo and redo work.
    // TODO(@add) :: Make post-validation process after executing some action, undo and redo state of the diagram.

    /*

       Command pattern entities:
       --------------------------------
        1. Client    :: creates commands and linking them with invokers;
        2. Invoker   :: executes commands;
        3. Receivier :: contains operations which will be executed by commands;
        4. Command;
        5. Custom command.

       Roles of entities in program:
       --------------------------------
       1. Diagram        :: Receiver, Client;
       2. Action Manager :: Invoker.

     */

    // ================ OPERATIONS ================
    // ----------------- contract -----------------
    public <T extends Node> T addNode(T node) { this._actionManager.addAction(new AddNodeAction   (this, node)).execute(); return node; }
    public void removeNode(Node node)         { this._actionManager.addAction(new RemoveNodeAction(this, node)).execute(); }

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

    // -------------- sub-operations --------------
    <T extends Node> T _addNode(T node) {
        node.setOwner(this);
        this._innerElements.add(node);

        return node;
    }

    public void _removeNode(Node node) {
        node.unsetOwner();
        this._innerElements.remove(node);
    }

    // ================= SUBTYPES =================
    class AddNodeAction extends Action {
        private Node _node;

        public AddNodeAction(Diagram diagram, @NotNull Node node) {
            super(diagram);
            this._node = node;
        }

        public void execute()   { this._diagram._addNode(this._node); }
        public void undo()      { this._diagram._removeNode(this._node); }
    }

    class RemoveNodeAction extends Action {
        private Node _node;

        public RemoveNodeAction(Diagram diagram, @NotNull Node node) {
            super(diagram);
            this._node = node;
        }

        public void execute()   { this._diagram._removeNode(this._node); }
        public void undo()      { this._diagram._addNode(this._node); }
    }
}
