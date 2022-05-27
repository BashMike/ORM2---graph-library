package com.orm2_graph_library.logic_errors;

import com.orm2_graph_library.core.LogicError;
import com.orm2_graph_library.edges.RoleConstraintRelationEdge;
import com.orm2_graph_library.nodes.constraints.Constraint;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.stream.Stream;

public class TwoOrMoreRolesSequencesWithIncompatibleSizeAreConnectedLogicError extends LogicError {
    final private Constraint _constraint;
    final private ArrayList<RoleConstraintRelationEdge> _connectedEdges = new ArrayList<>();

    public TwoOrMoreRolesSequencesWithIncompatibleSizeAreConnectedLogicError(@NotNull Constraint constraint, @NotNull ArrayList<RoleConstraintRelationEdge> connectedEdges) {
        this._constraint = constraint;
        this._connectedEdges.addAll(connectedEdges);

        this._errorParticipants.add(this._constraint);
        this._errorParticipants.addAll(this._connectedEdges);
    }

    public Constraint constraint() { return this._constraint; }
    public Stream<RoleConstraintRelationEdge> connectedEdges() { return this._connectedEdges.stream(); }

    public String description() {
    String result = "Constraint \"" + this._constraint.getClass() + "\" connects several role sequences with incompatible arity: rolesSequence@" + this._connectedEdges.get(0).end();
        for (int i=1; i<this._connectedEdges.size(); i++) {
            result += ", rolesSequence@" + this._connectedEdges.get(i).end();
        }

        return result;
    }
}
