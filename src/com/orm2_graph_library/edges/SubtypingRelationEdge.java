package com.orm2_graph_library.edges;

import com.orm2_graph_library.anchor_points.EdgeAnchorPoint;
import com.orm2_graph_library.core.AnchorPoint;
import com.orm2_graph_library.core.DiagramElement;
import com.orm2_graph_library.core.Edge;
import com.orm2_graph_library.nodes.common.EntityType;

import java.util.stream.Stream;

public class SubtypingRelationEdge extends Edge<EntityType, EntityType> {
    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public SubtypingRelationEdge(AnchorPoint<EntityType> beginAnchorPoint, AnchorPoint<EntityType> endAnchorPoint) {
        super(beginAnchorPoint, endAnchorPoint);
    }

    // ---------------- connection ---------------
    @Override protected void _initSelf() {}
    @Override protected void _finalizeSelf() {}

    // ---------------- attributes ----------------
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
}
