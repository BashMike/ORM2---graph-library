package com.orm2_graph_library.edges;

import com.orm2_graph_library.core.AnchorPoint;
import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.Edge;
import com.orm2_graph_library.nodes.predicates.Role;
import com.orm2_graph_library.nodes.predicates.RoleParticipant;

public class RoleRelationEdge extends Edge<Role, RoleParticipant> {
    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public RoleRelationEdge(AnchorPoint<Role> beginAnchorPoint, AnchorPoint<RoleParticipant> endAnchorPoint) { super(beginAnchorPoint, endAnchorPoint); }

    // ---------------- connection ----------------
    @Override
    protected void _initSelf() {}
}
