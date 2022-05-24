package com.orm2_graph_library.logic_errors;

import com.orm2_graph_library.core.Edge;
import com.orm2_graph_library.core.LogicError;
import com.orm2_graph_library.nodes.constraints.Constraint;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.stream.Stream;

public class ConstraintHasExtraConnectsLogicError extends LogicError {
    final private Constraint _constraint;
    final private ArrayList<Edge> _connectedConstraintRelationEdges = new ArrayList<>();

    public ConstraintHasExtraConnectsLogicError(@NotNull Constraint constraint,
                                                @NotNull ArrayList<Edge> connectedRoleConstraintRelationEdges)
    {
        this._constraint = constraint;
        this._connectedConstraintRelationEdges.addAll(connectedRoleConstraintRelationEdges);

        this._errorParticipants.add(constraint);
    }

    public Constraint constraint() { return this._constraint; }

    public Stream<Edge> connectedRoleConstraintRelationEdges() { return this._connectedConstraintRelationEdges.stream(); }

    public String description() {
        String result = "Constraint \"" + this._constraint.getClass() + "\" has ";

        if (this._connectedConstraintRelationEdges.size() == 1) { result += "1 constraint connection.\n"; }
        else                                                    { result += this._connectedConstraintRelationEdges.size() + " constraint connections.\n"; }

        result += "Must have count of constraint connections in [";
        result += this._constraint.minRequiredConnectionsCount() + ", ";

        if (this._constraint.maxRequiredConnectionsCount() == -1) { result += "+inf]."; }
        else                                                      { result += this._constraint.maxRequiredConnectionsCount() + "]."; }

        return result;
    }
}
