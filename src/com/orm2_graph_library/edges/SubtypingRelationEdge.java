package com.orm2_graph_library.edges;

import com.orm2_graph_library.core.Action;
import com.orm2_graph_library.core.AnchorPoint;
import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.Edge;
import com.orm2_graph_library.nodes.common.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class SubtypingRelationEdge extends Edge<EntityType, EntityType> {
    // ================ ATTRIBUTES ================
    private boolean _isInheritPathForRefMode = false;

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public SubtypingRelationEdge(AnchorPoint<EntityType> beginAnchorPoint, AnchorPoint<EntityType> endAnchorPoint) {
        super(beginAnchorPoint, endAnchorPoint);
    }

    // ---------------- connection ---------------
    @Override
    protected void _initSelf() {
        // TODO - @add :: Changing sign when deleting other subtyping relations (or calculating each time when getting the sign).
        this._isInheritPathForRefMode = false;
    }

    // ---------------- attributes ----------------
    public boolean isInheritPathForRefMode() { return this._isInheritPathForRefMode; }

    public void beInheritPathForRefMode() { this._ownerDiagramActionManager().executeAction(new BecomeInheritPathForRefModeAction(this._ownerDiagram, this)); }

    // ================= SUBTYPES =================
    private class BecomeInheritPathForRefModeAction extends Action {
        final private SubtypingRelationEdge            _edge;
        private SubtypingRelationEdge                  _oldInheritPath = null;
        final private ArrayList<SubtypingRelationEdge> _allEdges;

        public BecomeInheritPathForRefModeAction(Diagram diagram, @NotNull SubtypingRelationEdge edge) {
            super(diagram);
            this._edge = edge;
            this._allEdges = this._edge.begin().getIncidentElements(SubtypingRelationEdge.class).stream().filter(e -> e.begin() == this._edge.begin()).collect(Collectors.toCollection(ArrayList::new));

            for (SubtypingRelationEdge someEdge : this._allEdges) {
                if (someEdge.isInheritPathForRefMode()) { this._oldInheritPath = someEdge; }
            }
        }

        @Override
        public void _execute() {
            this._allEdges.forEach(e -> e._isInheritPathForRefMode = false);
            this._edge._isInheritPathForRefMode = true;
        }

        @Override
        public void _undo() {
            this._allEdges.forEach(e -> e._isInheritPathForRefMode = false);
            if (this._oldInheritPath != null) { this._oldInheritPath._isInheritPathForRefMode = false; }
        }
    }
}
