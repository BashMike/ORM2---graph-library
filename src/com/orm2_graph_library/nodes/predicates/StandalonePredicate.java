package com.orm2_graph_library.nodes.predicates;

import com.orm2_graph_library.core.Action;
import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.Movable;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class StandalonePredicate extends Predicate implements Movable {
    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public StandalonePredicate(int arity) { super(arity); }
    StandalonePredicate(InnerPredicate innerPredicate) { super(innerPredicate._roles); }

    public ObjectifiedPredicate becomeObjectified() { return new ObjectifiedPredicate(new InnerPredicate(this)); }

    // ---------------- attributes ----------------
    public void moveTo(Point leftTop) {
        this._ownerDiagram._actionManager().executeAction(new MoveStandalonePredicateAction(this._ownerDiagram, this, this._leftTop, leftTop));
    }

    public void moveBy(int shiftX, int shiftY) {
        Point newLeftTop = new Point(this._leftTop);
        newLeftTop.translate(shiftX, shiftY);

        this._ownerDiagram._actionManager().executeAction(new MoveStandalonePredicateAction(this._ownerDiagram, this, this._leftTop, newLeftTop));
    }

    // ================= SUBTYPES =================
    private class MoveStandalonePredicateAction extends Action {
        private final StandalonePredicate _node;
        private final Point               _oldNodeLeftTop;
        private final Point               _newNodeLeftTop;

        public MoveStandalonePredicateAction(@NotNull Diagram diagram, @NotNull StandalonePredicate node, @NotNull Point oldNodeLeftTop, @NotNull Point newNodeLeftTop) {
            super(diagram);

            this._node           = node;
            this._oldNodeLeftTop = oldNodeLeftTop;
            this._newNodeLeftTop = newNodeLeftTop;
        }

        @Override
        public void _execute() {
            this._node._leftTop.move(this._newNodeLeftTop.x, this._newNodeLeftTop.y);

            int shiftX = this._newNodeLeftTop.x - this._oldNodeLeftTop.x;
            int shiftY = this._newNodeLeftTop.y - this._oldNodeLeftTop.y;

            for (Role role : this._node._roles) {
                role._moveBy(shiftX, shiftY);
            }
        }

        @Override
        public void _undo() {
            this._node._leftTop.move(this._oldNodeLeftTop.x, this._oldNodeLeftTop.y);

            int shiftX = this._oldNodeLeftTop.x - this._newNodeLeftTop.x;
            int shiftY = this._oldNodeLeftTop.y - this._newNodeLeftTop.y;

            for (Role role : this._node._roles) {
                role._moveBy(shiftX, shiftY);
            }
        }
    }
}
