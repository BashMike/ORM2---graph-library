package com.orm2_graph_library.post_validators;

import com.orm2_graph_library.core.*;
import com.orm2_graph_library.edges.RoleConstraintRelationEdge;
import com.orm2_graph_library.edges.SubtypingConstraintRelationEdge;
import com.orm2_graph_library.logic_errors.ConstraintHasExtraConnectsLogicError;
import com.orm2_graph_library.logic_errors.ConstraintHasNotEnoughConnectsLogicError;
import com.orm2_graph_library.logic_errors.ConstraintIsRoleAndSubtypingConstraintLogicError;
import com.orm2_graph_library.nodes.constraints.*;
import com.orm2_graph_library.nodes.predicates.Role;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ConnectByGlobalConstraintRelationPostValidator extends PostValidator {
    final private Constraint _constraint;
    final private Edge       _edge;

    public ConnectByGlobalConstraintRelationPostValidator(@NotNull Diagram diagram,
                                                          @NotNull Action action,
                                                          @NotNull Constraint constraint,
                                                          @NotNull Edge edge)
    {
        super(diagram, action);
        this._constraint = constraint;
        this._edge       = edge;
    }

    @Override
    protected void validate() {
        // Checking node types uniformity of connections
        ArrayList<Edge> connectedRoleConstraintRelationEdges = this._constraint
                .getIncidentElements(RoleConstraintRelationEdge.class)
                .collect(Collectors.toCollection(ArrayList::new));

        ArrayList<Edge> connectedSubtypingConstraintRelationEdges = this._constraint
                .getIncidentElements(SubtypingConstraintRelationEdge.class)
                .collect(Collectors.toCollection(ArrayList::new));

        ArrayList<Edge> allConnectedConstraintRelationEdges = new ArrayList<>(connectedRoleConstraintRelationEdges);
        allConnectedConstraintRelationEdges.addAll(connectedSubtypingConstraintRelationEdges);

        if (!connectedRoleConstraintRelationEdges.isEmpty() && !connectedSubtypingConstraintRelationEdges.isEmpty()) {
            // TODO - @add :: Remove logic error if some of connections is removed.
            ArrayList<LogicError> oldLogicErrors = this._diagram.getLogicErrorsFor(this._constraint).filter(e -> e instanceof ConstraintIsRoleAndSubtypingConstraintLogicError).collect(Collectors.toCollection(ArrayList::new));
            for (LogicError logicError : oldLogicErrors) { this._removeLogicErrorFromDiagram(logicError); }

            this._addLogicErrorToDiagram(new ConstraintIsRoleAndSubtypingConstraintLogicError(this._constraint, allConnectedConstraintRelationEdges));
        }

        // Update or remove logic error of connections count
        LogicError existentNotEnoughConnectsLogicError = null;
        LogicError existentExtraConnectsLogicError     = null;

        if (this._diagram.getLogicErrorsFor(this._constraint)
                .anyMatch(e -> e instanceof ConstraintHasNotEnoughConnectsLogicError))
        {
            existentNotEnoughConnectsLogicError = this._diagram.getLogicErrorsFor(this._constraint)
                .filter(e -> e instanceof ConstraintHasNotEnoughConnectsLogicError)
                .findFirst().get();
        }

        if (this._diagram.getLogicErrorsFor(this._constraint)
                .anyMatch(e -> e instanceof ConstraintHasExtraConnectsLogicError))
        {
            existentExtraConnectsLogicError = this._diagram.getLogicErrorsFor(this._constraint)
                    .filter(e -> e instanceof ConstraintHasExtraConnectsLogicError)
                    .findFirst().get();
        }

        if (existentNotEnoughConnectsLogicError != null) { this._removeLogicErrorFromDiagram(existentNotEnoughConnectsLogicError); }
        if (existentExtraConnectsLogicError != null)     { this._removeLogicErrorFromDiagram(existentExtraConnectsLogicError); }

        if (allConnectedConstraintRelationEdges.size() < this._constraint.minRequiredConnectionsCount()) {
            this._addLogicErrorToDiagram(new ConstraintHasNotEnoughConnectsLogicError(this._constraint, allConnectedConstraintRelationEdges));
        }
        else if (allConnectedConstraintRelationEdges.size() > this._constraint.maxRequiredConnectionsCount() && this._constraint.maxRequiredConnectionsCount() != -1) {
            this._addLogicErrorToDiagram(new ConstraintHasExtraConnectsLogicError(this._constraint, allConnectedConstraintRelationEdges));
        }
    }
}
