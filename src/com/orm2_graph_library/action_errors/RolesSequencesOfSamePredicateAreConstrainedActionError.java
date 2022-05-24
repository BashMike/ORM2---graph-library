package com.orm2_graph_library.action_errors;

import com.orm2_graph_library.core.Edge;
import com.orm2_graph_library.core.LogicError;
import com.orm2_graph_library.nodes.constraints.Constraint;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.stream.Stream;

public class RolesSequencesOfSamePredicateAreConstrainedActionError extends LogicError {
    final private Constraint      _constraint;
    final private ArrayList<Edge> _connectedConstraintRelationEdges = new ArrayList<>();

    public RolesSequencesOfSamePredicateAreConstrainedActionError(@NotNull Constraint constraint, @NotNull ArrayList<Edge> connectedRoleConstraintRelationEdges) {
        this._constraint = constraint;
        this._connectedConstraintRelationEdges.addAll(connectedRoleConstraintRelationEdges);

        this._errorParticipants.add(constraint);
    }

    public Constraint constraint() { return this._constraint; }
    public Stream<Edge> connectedRoleConstraintRelationEdges() { return this._connectedConstraintRelationEdges.stream(); }

    public String description() { return "Constraint \"" + this._constraint.getClass() + "\" connects " + this._connectedConstraintRelationEdges.size() + " roles sequences of the same predicate."; }
}
