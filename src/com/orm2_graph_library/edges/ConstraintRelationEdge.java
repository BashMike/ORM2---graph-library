package com.orm2_graph_library.edges;

import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.Edge;
import com.orm2_graph_library.core.Node;
import com.orm2_graph_library.nodes.constraints.Constraint;

public class ConstraintRelationEdge extends Edge {
    public ConstraintRelationEdge(Node begin, Constraint end) { super(begin, end); }

    @Override
    protected void _initSelf(Diagram owner) {}
}
