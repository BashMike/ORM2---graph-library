package com.orm2_graph_library.edges;

import com.orm2_graph_library.core.AnchorPoint;
import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.Edge;
import com.orm2_graph_library.core.Node;
import com.orm2_graph_library.nodes.constraints.Constraint;
import com.orm2_graph_library.nodes.predicates.RolesSequence;

public class RoleConstraintRelationEdge extends Edge<RolesSequence, Constraint> {
    public RoleConstraintRelationEdge(AnchorPoint<RolesSequence> beginAnchorPoint, AnchorPoint<Constraint> endAnchorPoint) { super(beginAnchorPoint, endAnchorPoint); }

    @Override
    protected void _initSelf(Diagram owner) {}
}
