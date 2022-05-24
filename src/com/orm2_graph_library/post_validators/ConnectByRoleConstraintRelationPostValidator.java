package com.orm2_graph_library.post_validators;

import com.orm2_graph_library.core.Action;
import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.PostValidator;
import com.orm2_graph_library.edges.RoleConstraintRelationEdge;
import com.orm2_graph_library.nodes.constraints.*;
import org.jetbrains.annotations.NotNull;

public class ConnectByRoleConstraintRelationPostValidator extends PostValidator {
    final private Constraint                 _constraint;
    final private RoleConstraintRelationEdge _edge;

    public ConnectByRoleConstraintRelationPostValidator(@NotNull Diagram diagram,
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
        // Check that constraint doesn't connect role sequences of the same predicate
        /*
        ArrayList<LogicError> oldLogicErrors = this._diagram.getLogicErrorsFor(this._edge.end()).collect(Collectors.toCollection(ArrayList::new));
        for (LogicError logicError : oldLogicErrors) { this._removeLogicErrorFromDiagram(logicError); }

        if (this._edge.begin().ownerPredicate() == this._edge.end().ownerPredicate()) {

        }
         */
    }
}
