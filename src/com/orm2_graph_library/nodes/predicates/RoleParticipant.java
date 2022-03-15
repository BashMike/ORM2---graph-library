package com.orm2_graph_library.nodes.predicates;

import com.orm2_graph_library.core.Action;
import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.Movable;
import com.orm2_graph_library.core.Node;
import com.orm2_graph_library.nodes.common.ObjectType;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public abstract class RoleParticipant extends Node implements Movable {
    // ================ OPERATIONS ================
    // ---------------- attributes ----------------
    @Override
    public void moveTo(Point leftTop) {
        this._owner._actionManager().executeAction(new MoveRoleParticipantAction(this._owner, this, this._leftTop, leftTop));
    }

    @Override
    public void moveBy(int shiftX, int shiftY) {
        Point newLeftTop = new Point(this._leftTop);
        newLeftTop.translate(shiftX, shiftY);

        this._owner._actionManager().executeAction(new MoveRoleParticipantAction(this._owner, this, this._leftTop, newLeftTop));
    }

    // ================= SUBTYPES =================
    private class MoveRoleParticipantAction extends Action {
        private final RoleParticipant _node;
        private final Point           _oldLeftTop;
        private final Point           _newLeftTop;

        public MoveRoleParticipantAction(@NotNull Diagram diagram, @NotNull RoleParticipant node, @NotNull Point oldLeftTop, @NotNull Point newLeftTop) {
            super(diagram);

            this._node       = node;
            this._oldLeftTop = new Point(oldLeftTop.x, oldLeftTop.y);
            this._newLeftTop = new Point(newLeftTop.x, newLeftTop.y);
        }

        @Override
        public void _execute() { this._node._leftTop.move(this._newLeftTop.x, this._newLeftTop.y); }
        @Override
        public void _undo() { this._node._leftTop.move(this._oldLeftTop.x, this._oldLeftTop.y); }
    }
}
