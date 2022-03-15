package com.orm2_graph_library.core;

import com.orm2_graph_library.edges.RoleConstraintRelationEdge;
import com.orm2_graph_library.edges.RoleRelationEdge;
import com.orm2_graph_library.edges.SubtypingRelationEdge;
import com.orm2_graph_library.nodes.common.EntityType;
import com.orm2_graph_library.nodes.predicates.Role;
import com.orm2_graph_library.nodes.predicates.RoleParticipant;
import com.orm2_graph_library.nodes.predicates.RolesSequence;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
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
    private final ArrayList<DiagramElement> _innerElements = new ArrayList<>();

    protected final ActionManager           _actionManager = new ActionManager();
    protected ArrayList<LogicError>         _logicErrors   = new ArrayList<>();

    // ================ OPERATIONS ================
    // ---------------- attributes ----------------
    public ArrayList<LogicError> logicErrors() { return new ArrayList<>(this._logicErrors); }

    void _addLogicError(@NotNull LogicError logicError) { this._logicErrors.add(logicError); }
    void _removeLogicError(@NotNull LogicError logicError) { this._logicErrors.remove(logicError); }

    // ----------------- contract -----------------
    public <T extends Node> T addNode(T node) {
        this._actionManager.executeAction(new AddNodeAction(this, node));

        return node;
    }

    public void removeNode(Node node) { this._actionManager.executeAction(new RemoveNodeAction(this, node)); }

    public <T extends EntityType, G extends EntityType> SubtypingRelationEdge connectBySubtypeRelation(AnchorPoint<T> beginAnchorPoint, AnchorPoint<G> endAnchorPoint) {
        SubtypingRelationEdge edge = new SubtypingRelationEdge((AnchorPoint<EntityType>)beginAnchorPoint, (AnchorPoint<EntityType>)endAnchorPoint);
        this._actionManager.executeAction(new ConnectBySubtypeRelationAction(this, edge));

        return edge;
    }

    public <T extends Role, G extends RoleParticipant> RoleRelationEdge connectByRoleRelation(AnchorPoint<T> begin, AnchorPoint<G> end) {
        return null;
    }

    public <T extends RolesSequence, G extends Role> RoleConstraintRelationEdge connectByRoleConstraintRelation(AnchorPoint<T> begin, AnchorPoint<G> end) {
        return null;
    }

    public <T extends RolesSequence, G extends Role> RoleConstraintRelationEdge connectBySubtypingConstraintRelation(AnchorPoint<T> begin, AnchorPoint<G> end) {
        return null;
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

    private abstract class ConnectAction extends Action {
        protected final Edge _edge;

        public ConnectAction(@NotNull Diagram diagram, @NotNull Edge edge) {
            super(diagram);
            this._edge = edge;
        }

        @Override
        public void _execute() { this._diagram._addElement(this._edge); }
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

    private class ConnectByConstraintRelationAction extends ConnectAction {
        public ConnectByConstraintRelationAction(@NotNull Diagram diagram, @NotNull RoleConstraintRelationEdge edge) {
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

    private class DisconnectSubtypeAction extends DisconnectAction {
        public DisconnectSubtypeAction(@NotNull Diagram diagram, @NotNull SubtypingRelationEdge edge) {
            super(diagram, edge);
        }
    }
}

/*

    TODO - @technical_task :: Установить соответствующий доступ к геометрии у каждого типа узла в иерархии узлов.
    [
        Requirements:
         - Пользователям классов должны быть:
           1) доступен весь необходимый функционал достаточный для взаимодействия с геометрией у каждого типа узлов;
           2) недоступен лишний функционал взаимодействия с геометрией у каждого типа узлов;
           3) недоступен не предусмотрено опасный функционал взаимодействия с геометрией у каждого типа узлов (например, доступ к
              менеджеру действий, из которого можно удалить записанные действия).
    ]

    TODO - @technical_task :: Хранение и формирование информации о дугах, которые могут поддерживать соединение узла и дуги и разных определенных типов узлов.
    [
        Requirements:
         - Поддержка всех соединений;
         - Пользователям должны быть доступен только необходимый и достаточный функционал для взаимодействия с дугами;
         - Соблюдение безопасности по типу.
    ]

    ----------------------------------------------------------------
    GEOMETRY FUNCTIONALITY FOR NODES
    ----------------------------------------------------------------
    1. Object types;
        [self.x, self.y, self.width, self.height, self.shape]

        moveTo(x, y)                [ move itself to location ]
        moveBy(shiftX, shiftY)      [ move itself by shift value ]

    2. Objectified Predicate;
        [self.x, self.y, Predicate.width, Predicate.height, self.shape]

        moveTo(x, y)                [ move itself and predicate to location;    inner predicate cannot be moved ]
        moveBy(shiftX, shiftY)      [ move itself and predicate by shift value; inner predicate cannot be moved ]

    3. Constraint nodes;
        [self.x, self.y, self.width, self.height, self.shape]

        moveTo(x, y)                [ move itself to location ]
        moveBy(shiftX, shiftY)      [ move itself by shift value ]

    4. Predicate;
        [self.x, self.y, sum(Role.width), sum(Role.height), self.shape]

        moveTo(x, y)                [ move itself and self roles to location ]
        moveBy(shiftX, shiftY)      [ move itself and self roles by shift value ]

    5. Role;
        [Predicate.x, Predicate.y, self.width, self.height, self.shape]

    6. Roles sequence.
        [no geometry attributes and operations]

    ----------------------------------------------------------------
    POSSIBLE CLASSES HIERARCHY
    ----------------------------------------------------------------
    abstract class DiagramElement;
    abstract class Node             -> DiagramElement;
    interface Movable;

    class ObjectType                -> Node # Movable;
    class ValueType                 -> ObjectType;
    class EntityType                -> ObjectType;

    class Predicate                 -> Node;
    class StandalonePredicate       -> Predicate # Movable;
    class InnerPredicate            -> Predicate;
    class Role                      -> Node;
    class RolesSequence             -> DiagramElement;

    ----------------------------------------------------------------
    POSSIBLE CLASSES CONTENTS
    ----------------------------------------------------------------
    abstract class DiagramElement {
        protected Diagram _owner :: @init fromPackagedMethod;
    }

    abstract class Node {
        protected Geometry _geometry :: @init fromConstructor;
    }

    interface Movable {
        public void moveTo(int x, int y);
        public void moveBy(int shiftX, int shiftY);
    }

    class ObjectType {
        public void moveTo(int x, int y) {
            this._geometry.moveTo(x, y);
        }

        public void moveBy(int shiftX, int shiftY) {
            this._geometry.moveTo(x, y);
        }
    }

    class ValueType  {}
    class EntityType {}

    class Predicate {
        protected ArrayList<Role> _roles :: @init fromConstructor
    }

    class StandalonePredicate {
        public void moveTo(int x, int y) {
            this._geometry.moveTo(x, y);

            for (Role role : this._roles) {
                role._moveTo(x, y);
            }
        }
    }

    class InnerPredicate {
    }

    class Role {
    }

    class RolesSequence {
    }
 */

