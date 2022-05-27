package com.orm2_graph_library.logic_errors;

import com.orm2_graph_library.core.LogicError;
import com.orm2_graph_library.edges.RoleConstraintRelationEdge;
import com.orm2_graph_library.nodes.constraints.Constraint;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.stream.Stream;

public class RolesSequencesOfSamePredicateAreConstrainedLogicError extends LogicError {
    final private Constraint                            _constraint;
    final private ArrayList<RoleConstraintRelationEdge> _connectedEdges = new ArrayList<>();

    public RolesSequencesOfSamePredicateAreConstrainedLogicError(@NotNull Constraint constraint, @NotNull ArrayList<RoleConstraintRelationEdge> connectedEdges) {
        this._constraint = constraint;
        this._connectedEdges.addAll(connectedEdges);

        this._errorParticipants.add(constraint);
        this._errorParticipants.addAll(connectedEdges);
    }

    public Constraint constraint() { return this._constraint; }
    public Stream<RoleConstraintRelationEdge> connectedEdges() { return this._connectedEdges.stream(); }

    public String description() { return "Constraint \"" + this._constraint.getClass() + "\" connects " + this._connectedEdges.size() + " roles sequences of the same predicate."; }
}
