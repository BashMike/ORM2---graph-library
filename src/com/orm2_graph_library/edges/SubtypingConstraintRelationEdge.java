package com.orm2_graph_library.edges;

import com.orm2_graph_library.core.AnchorPoint;
import com.orm2_graph_library.core.DiagramElement;
import com.orm2_graph_library.core.Edge;
import com.orm2_graph_library.nodes.constraints.Constraint;

import java.util.stream.Stream;

public class SubtypingConstraintRelationEdge extends Edge<Constraint, SubtypingRelationEdge> {
    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public SubtypingConstraintRelationEdge(AnchorPoint<Constraint> beginAnchorPoint, AnchorPoint<SubtypingRelationEdge> endAnchorPoint) { super(beginAnchorPoint, endAnchorPoint); }

    // ---------------- connection ----------------
    @Override protected void _initSelf() {}
    @Override protected void _finalizeSelf() {}

    // ---------------- attributes ----------------
    // * Anchor points
    @Override public Stream<AnchorPoint> anchorPoints() { return Stream.of(); };
}
