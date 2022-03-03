package com.orm2_graph_library.edges;

import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.Edge;
import com.orm2_graph_library.core.Node;
import com.orm2_graph_library.nodes.constraints.Constraint;
import com.orm2_graph_library.nodes.predicates.Role;

public class RoleRelationEdge extends Edge {
    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public RoleRelationEdge(Node begin, Role end) { super(begin, end); }

    // ---------------- connection ----------------
    @Override
    protected void _initSelf(Diagram owner) {}
}
