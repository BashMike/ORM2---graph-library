package com.orm2_graph_library.edges;

import com.orm2_graph_library.core.AnchorPoint;
import com.orm2_graph_library.core.Edge;
import com.orm2_graph_library.nodes.constraints.Constraint;
import com.orm2_graph_library.nodes.constraints.SubsetConstraint;
import com.orm2_graph_library.nodes.predicates.RolesSequence;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RoleConstraintRelationEdge extends Edge<Constraint, RolesSequence> {
    // ================ ATTRIBUTES ================
    private boolean _isEndingEdge = false;

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public RoleConstraintRelationEdge(AnchorPoint<Constraint> beginAnchorPoint, AnchorPoint<RolesSequence> endAnchorPoint) {
        super(beginAnchorPoint, endAnchorPoint);
    }

    // ---------------- connection ----------------
    @Override
    protected void _initSelf() {
        if (this.begin() instanceof SubsetConstraint) {
            ArrayList<RoleConstraintRelationEdge> roleConstraintRelationEdges = this.begin().getIncidentElements(RoleConstraintRelationEdge.class).filter(e -> e != this).collect(Collectors.toCollection(ArrayList::new));
            if (roleConstraintRelationEdges.size() > 1) {
                throw new RuntimeException("ERROR :: " + roleConstraintRelationEdges.size() + " role constraint relation edges connected to subset constraint.");
            }

            this._isEndingEdge = (roleConstraintRelationEdges.size() == 1);
            if (roleConstraintRelationEdges.size() == 1) { roleConstraintRelationEdges.get(0)._isEndingEdge = false; }
        }
    }

    @Override protected void _finalizeSelf() {}

    // ---------------- attributes ----------------
    // * "Is ending edge" flag
    public boolean isEndingEdge() {
        if (this.begin() instanceof SubsetConstraint) {
            return this._isEndingEdge;
        }
        else {
            throw new RuntimeException("ERROR :: Try to check if given edge is the end of subset constraint relation when it doesn't connect subset constraint.");
        }
    }

    public void _setIsEndingEdge(boolean isEndingEdge) {
        if (this.begin() instanceof SubsetConstraint) {
            this._isEndingEdge = isEndingEdge;
        }
        else {
            throw new RuntimeException("ERROR :: Try to check if given edge is the end of subset constraint relation when it doesn't connect subset constraint.");
        }
    }

    // * Anchor points
    @Override public Stream<AnchorPoint> anchorPoints() { return Stream.of(); };
}
