package com.orm2_graph_library.edges;

import com.orm2_graph_library.anchor_points.AnchorPosition;
import com.orm2_graph_library.anchor_points.EdgeAnchorPoint;
import com.orm2_graph_library.core.*;
import com.orm2_graph_library.nodes.common.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Override protected void _finalizeSelf() {}

    // ---------------- attributes ----------------
    // * Inheritance path for entity type's reference mode
    public boolean isInheritPathForRefMode() { return this._isInheritPathForRefMode; }

    public void beInheritPathForRefMode() { this._ownerDiagramActionManager().executeAction(new BecomeInheritPathForRefModeAction(this._ownerDiagram, this)); }

    // * Anchor points
    public AnchorPoint<SubtypingRelationEdge> anchorPoint() { return new EdgeAnchorPoint<>(this); }

    @Override public Stream<AnchorPoint> anchorPoints() { return Stream.of(this.anchorPoint()); }

    // ----------------- contract -----------------
    @Override
    public <T extends DiagramElement> Stream<T> getIncidentElements(Class<T> elementType) {
        Stream<T> result = super.getIncidentElements(elementType);

        if (SubtypingConstraintRelationEdge.class.isAssignableFrom(elementType)) {
            result = Stream.concat(result, this._ownerDiagram.getElements(elementType).filter(e -> e != this).filter(e -> e.isIncidentElement(this)));
        }

        return result;
    }

    // ================= SUBTYPES =================
    public class BecomeInheritPathForRefModeAction extends Action {
        final private SubtypingRelationEdge            _edge;
        private SubtypingRelationEdge                  _oldInheritPath = null;
        final private ArrayList<SubtypingRelationEdge> _allEdges;

        private BecomeInheritPathForRefModeAction(Diagram diagram, @NotNull SubtypingRelationEdge edge) {
            super(diagram);
            this._edge = edge;
            this._allEdges = this._edge.begin().getIncidentElements(SubtypingRelationEdge.class)
                    .filter(e -> e.begin() == this._edge.begin()).collect(Collectors.toCollection(ArrayList::new));

            for (SubtypingRelationEdge someEdge : this._allEdges) {
                if (someEdge.isInheritPathForRefMode()) { this._oldInheritPath = someEdge; }
            }
        }

        public SubtypingRelationEdge edge() { return this._edge; }
        public SubtypingRelationEdge oldInheritPath() { return this._oldInheritPath; }
        public Stream<SubtypingRelationEdge> allEdges() { return this._allEdges.stream(); }

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
