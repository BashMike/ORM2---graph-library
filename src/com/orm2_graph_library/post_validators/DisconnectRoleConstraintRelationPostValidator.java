package com.orm2_graph_library.post_validators;

import com.orm2_graph_library.core.Action;
import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.LogicError;
import com.orm2_graph_library.core.PostValidator;
import com.orm2_graph_library.edges.RoleConstraintRelationEdge;
import com.orm2_graph_library.logic_errors.RoleConstraintsConflictLogicError;
import com.orm2_graph_library.logic_errors.RolesSequencesOfSamePredicateAreConstrainedLogicError;
import com.orm2_graph_library.logic_errors.TwoOrMoreRolesSequencesWithIncompatibleSizeAreConnectedLogicError;
import com.orm2_graph_library.nodes.constraints.*;
import com.orm2_graph_library.nodes.predicates.Predicate;
import com.orm2_graph_library.nodes.predicates.RolesSequence;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DisconnectRoleConstraintRelationPostValidator extends PostValidator {
    final private Constraint _constraint;
    final private RoleConstraintRelationEdge _edge;

    public DisconnectRoleConstraintRelationPostValidator(@NotNull Diagram diagram,
                                                         @NotNull Action action,
                                                         @NotNull Constraint constraint,
                                                         @NotNull RoleConstraintRelationEdge edge)
    {
        super(diagram, action);
        this._constraint = constraint;
        this._edge       = edge;
    }

    @Override
    protected void validate() {
        // TODO - @add :: Getting logic error when edge connects roles sequences of the same predicate.
        ArrayList<RoleConstraintRelationEdge> connectedEdges = this._constraint.getIncidentElements(RoleConstraintRelationEdge.class).filter(e -> e != this._edge).collect(Collectors.toCollection(ArrayList::new));

        // Remove old logic errors
        ArrayList<LogicError> oldLogicErrors = this._constraint.getLogicErrors(TwoOrMoreRolesSequencesWithIncompatibleSizeAreConnectedLogicError.class).collect(Collectors.toCollection(ArrayList::new));
        oldLogicErrors.addAll(this._constraint.getLogicErrors(TwoOrMoreRolesSequencesWithIncompatibleSizeAreConnectedLogicError.class).collect(Collectors.toCollection(ArrayList::new)));
        oldLogicErrors.addAll(this._constraint.getLogicErrors(RolesSequencesOfSamePredicateAreConstrainedLogicError.class).collect(Collectors.toCollection(ArrayList::new)));
        for (LogicError logicError : oldLogicErrors) { this._removeLogicErrorFromDiagram(logicError); }

        if (!connectedEdges.isEmpty()) {
            // Find logic errors of connecting roles sequences of the same predicates
            if (!(this._constraint instanceof UniquenessConstraint)) {
                Predicate firstOwnerPredicate = connectedEdges.get(0).end().ownerPredicate();

                for (int i=1; i<connectedEdges.size(); i++) {
                    if (connectedEdges.get(i).end().ownerPredicate() == firstOwnerPredicate) {
                        this._addLogicErrorToDiagram(new RolesSequencesOfSamePredicateAreConstrainedLogicError(this._constraint, connectedEdges));
                        break;
                    }
                }
            }

            // Find logic errors of incompatible size of connected roles sequences
            int firstRolesSequenceSize = (int)connectedEdges.get(0).end().roles().count();

            for (int i=1; i<connectedEdges.size(); i++) {
                if ((int)connectedEdges.get(i).end().roles().count() != firstRolesSequenceSize) {
                    this._addLogicErrorToDiagram(new TwoOrMoreRolesSequencesWithIncompatibleSizeAreConnectedLogicError(this._constraint, connectedEdges));
                    break;
                }
            }
        }

        // Find logic errors of role constraints conflicts
        ArrayList<RolesSequence> rolesSequences = this._constraint.getIncidentElements(RolesSequence.class).filter(e -> e != this._edge.end()).collect(Collectors.toCollection(ArrayList::new));

        for (RolesSequence rolesSequence : rolesSequences) {
            ArrayList<Constraint> constraints = rolesSequence.getIncidentElements(Constraint.class)
                    .filter(e -> e.isIncidentElement(rolesSequence) && e.isIncidentElement(this._edge.end()))
                    .collect(Collectors.toCollection(ArrayList::new));

            ArrayList<Constraint> conflictedConstraints = constraints.stream().filter(e -> e instanceof ExclusionConstraint).collect(Collectors.toCollection(ArrayList::new));
            conflictedConstraints.addAll(constraints.stream().filter(e -> e instanceof SubsetConstraint).collect(Collectors.toCollection(ArrayList::new)));
            conflictedConstraints.addAll(constraints.stream().filter(e -> e instanceof EqualityConstraint).collect(Collectors.toCollection(ArrayList::new)));

            if (conflictedConstraints.stream().anyMatch(e -> e instanceof ExclusionConstraint)) {
                if (conflictedConstraints.stream().anyMatch(e -> e instanceof SubsetConstraint) ||
                    conflictedConstraints.stream().anyMatch(e -> e instanceof EqualityConstraint))
                {
                    this._addLogicErrorToDiagram(new RoleConstraintsConflictLogicError(conflictedConstraints, new ArrayList<>(List.of(this._edge.end(), rolesSequence))));
                }
            }
        }
    }
}
