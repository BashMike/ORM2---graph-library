package com.orm2_graph_library.edges;

import com.orm2_graph_library.core.AnchorPoint;
import com.orm2_graph_library.core.Edge;
import com.orm2_graph_library.nodes.constraints.Constraint;
import com.orm2_graph_library.nodes.constraints.SubsetConstraint;
import com.orm2_graph_library.nodes.predicates.RolesSequence;

public class RoleConstraintRelationEdge extends Edge<RolesSequence, Constraint> {
    private boolean _isEndingEdge;

    public RoleConstraintRelationEdge(AnchorPoint<RolesSequence> beginAnchorPoint, AnchorPoint<Constraint> endAnchorPoint) { super(beginAnchorPoint, endAnchorPoint); }

    @Override
    protected void _initSelf() {
        this._isEndingEdge = this.end().getIncidentElements(RolesSequence.class).stream().anyMatch(e -> e != this.begin());
    }

    public boolean isEndingEdge() {
        if (this.end() instanceof SubsetConstraint) {
            return this._isEndingEdge;
        }
        else {
            throw new RuntimeException("ERROR :: Try to check if given edge is the end of subset constraint relation when it doesn't connect subset constraint.");
        }
    }
}
