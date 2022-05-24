package com.orm2_graph_library.nodes.predicates;

import com.orm2_graph_library.core.*;
import com.orm2_graph_library.nodes.common.ObjectType;
import com.orm2_graph_library.utils.Point2D;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;

public abstract class RoleParticipant extends Node implements Movable {
    // ================ OPERATIONS ================
    // ---------------- attributes ----------------
    @Override
    public void moveTo(Point2D leftTop) {
        this._ownerDiagramActionManager().executeAction(new MoveRoleParticipantAction(this._ownerDiagram, this, this._leftTop, leftTop));
    }

    @Override
    public void moveBy(int shiftX, int shiftY) {
        Point2D newLeftTop = new Point2D(this._leftTop);
        newLeftTop.translate(shiftX, shiftY);

        this._ownerDiagramActionManager().executeAction(new MoveRoleParticipantAction(this._ownerDiagram, this, this._leftTop, newLeftTop));
    }

    // ================= SUBTYPES =================
    public class MoveRoleParticipantAction extends Action {
        private final RoleParticipant _node;
        private final Point2D         _oldLeftTop;
        private final Point2D         _newLeftTop;

        private MoveRoleParticipantAction(@NotNull Diagram diagram, @NotNull RoleParticipant node, @NotNull Point2D oldLeftTop, @NotNull Point2D newLeftTop) {
            super(diagram);

            this._node       = node;
            this._oldLeftTop = new Point2D(oldLeftTop);
            this._newLeftTop = new Point2D(newLeftTop);
        }

        public RoleParticipant node() { return this._node; }
        public Point2D oldLeftTop() { return this._oldLeftTop; }
        public Point2D newLeftTop() { return this._newLeftTop; }

        @Override
        public void _execute() { this._node._leftTop.move(this._newLeftTop.x, this._newLeftTop.y); }
        @Override
        public void _undo() { this._node._leftTop.move(this._oldLeftTop.x, this._oldLeftTop.y); }
    }
}
