package com.orm2_graph_library.core;

import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Node extends DiagramElement {
    // ================ ATTRIBUTES ================
    protected Geometry _geometry = null;

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public Node() {}

    // ---------------- connection ----------------
    @Override
    void setOwner(Diagram owner) {
        super.setOwner(owner);
    }

    // ---------------- attributes ----------------
    public Geometry geometry() { return this._geometry; }

    // ----------------- contract -----------------
    @Override
    public <T extends DiagramElement> ArrayList<T> getIncidentElements(Class<T> elementType) {
        ArrayList<T> result = new ArrayList<>();

        if (Edge.class.isAssignableFrom(elementType)) {
            for (Edge edge : this._owner.getElements(Edge.class).collect(Collectors.toCollection(ArrayList::new))) {
                if (elementType.isAssignableFrom(edge.getClass()) && (edge.begin() == this || edge.end() == this)) {
                    result.add((T)edge);
                }
            }
        }
        else if (Node.class.isAssignableFrom(elementType)) {
            for (Edge edge : this._owner.getElements(Edge.class).collect(Collectors.toCollection(ArrayList::new))) {
                if (edge.begin() == this && elementType.isAssignableFrom(edge.end().getClass())) {
                    result.add((T)edge.end());
                }
                if (edge.end() == this && elementType.isAssignableFrom(edge.begin().getClass())) {
                    result.add((T)edge.begin());
                }
            }
        }

        return result;
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
    abstract class DiagramElement {}
    abstract class Node -> DiagramElement {}
    abstract class MovableNode -> DiagramElement {
        moveTo(x, y);
        moveBy(shiftX, shiftY);
    }

    class ObjectType -> MovableNode {}
    class ObjectifiedNode -> MovableNode {}
    class ConstraintNode -> MovableNode {}
    abstract class Predicate -> MovableNode {}
    class StandalonePredicate -> Predicate {}
    class InnerPredicate -> Predicate {}
    class Role {}
    class RolesSequence {}
 */
