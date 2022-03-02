package com.orm2_graph_library.core;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public abstract class MovableNode extends Node {
    // ================ OPERATIONS ================
    // ---------------- attributes ----------------
    public void moveTo(int x, int y) {
        this._owner._actionManager.executeAction(new MoveNodeAction(this._owner, this, (Point)this._geometry._area.leftTop().clone(), new Point(x, y)));
    }

    public void moveBy(int shiftX, int shiftY) {
        Point newPos = new Point(this._geometry._area.leftTop().x + shiftX, this._geometry._area.leftTop().y + shiftY);
        this._owner._actionManager.executeAction(new MoveNodeAction(this._owner, this, (Point)this._geometry._area.leftTop().clone(), newPos));
    }

    // ================= SUBTYPES =================
    private class MoveNodeAction extends Action {
        private final MovableNode _node;
        private final Point       _oldPos;
        private final Point       _newPos;

        public MoveNodeAction(Diagram diagram, @NotNull MovableNode node, Point oldPos, Point newPos) {
            super(diagram);

            this._node   = node;
            this._oldPos = oldPos;
            this._newPos = newPos;
        }

        @Override
        public void _execute() { this._node._geometry.moveTo(this._newPos.x, this._newPos.y); }
        @Override
        public void _undo() { this._node._geometry.moveTo(this._oldPos.x, this._oldPos.y); }
    }
}
