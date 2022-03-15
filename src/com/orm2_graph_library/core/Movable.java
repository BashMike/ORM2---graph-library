package com.orm2_graph_library.core;

import java.awt.*;

// TODO - @modify :: Change abstract class to interface.
// TODO - @modify :: Two interfaces for moving self: 1. Move with recording actions and 2. Move without recording actions!?
// TODO - @modify :: Record inner components movement (Different movement actions :: GOOD!).
public interface Movable {
    void moveTo(Point leftTop);
    void moveBy(int shiftX, int shiftY);
}

/*
    public void moveTo(Point leftTop) {
        this._owner._actionManager.executeAction(new MoveNodeAction(this._owner, this, (Point)this._geometry._area.leftTop().clone(), new Point(leftTop)));
    }

    public void moveBy(int shiftX, int shiftY) {
        Point newPos = new Point(this._geometry._area.leftTop().x + shiftX, this._geometry._area.leftTop().y + shiftY);
        this._owner._actionManager.executeAction(new MoveNodeAction(this._owner, this, (Point)this._geometry._area.leftTop().clone(), newPos));
    }

    // ================= SUBTYPES =================
    private class MoveNodeAction extends Action {
        private final Movable _node;
        private final Point   _oldPos;
        private final Point   _newPos;

        public MoveNodeAction(Diagram diagram, @NotNull Movable node, Point oldPos, Point newPos) {
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
 */
