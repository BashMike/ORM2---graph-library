package com.orm2_graph_library.edges;

import com.orm2_graph_library.core.AnchorPoint;
import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.Edge;
import com.orm2_graph_library.nodes.constraints.Constraint;

public class SubtypingConstraintRelationEdge extends Edge<SubtypingRelationEdge, Constraint> {
    public SubtypingConstraintRelationEdge(AnchorPoint<SubtypingRelationEdge> beginAnchorPoint, AnchorPoint<Constraint> endAnchorPoint) { super(beginAnchorPoint, endAnchorPoint); }

    @Override
    protected void _initSelf() {}
}
