package com.orm2_graph_library.post_validators;

import com.orm2_graph_library.core.Action;
import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.LogicError;
import com.orm2_graph_library.core.PostValidator;
import com.orm2_graph_library.edges.SubtypingConstraintRelationEdge;
import com.orm2_graph_library.logic_errors.IllegalSubtypingConstraintRelationLogicError;
import com.orm2_graph_library.nodes.constraints.Constraint;
import com.orm2_graph_library.nodes.constraints.EqualityConstraint;
import com.orm2_graph_library.nodes.constraints.SubsetConstraint;
import com.orm2_graph_library.nodes.constraints.UniquenessConstraint;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class DisconnectSubtypingConstraintRelationPostValidator extends PostValidator {
    final private Constraint                      _constraint;
    final private SubtypingConstraintRelationEdge _edge;

    public DisconnectSubtypingConstraintRelationPostValidator(@NotNull Diagram diagram,
                                                              @NotNull Action action,
                                                              @NotNull Constraint constraint,
                                                              @NotNull SubtypingConstraintRelationEdge edge)
    {
        super(diagram, action);
        this._constraint = constraint;
        this._edge       = edge;
    }

    @Override
    protected void validate() {
        // Checking illegal connection with non-subtyping constraint
        if (this._constraint instanceof EqualityConstraint ||
            this._constraint instanceof SubsetConstraint ||
            this._constraint instanceof UniquenessConstraint)
        {
            ArrayList<LogicError> oldLogicErrors = this._diagram.getLogicErrorsFor(this._edge).filter(e -> e instanceof IllegalSubtypingConstraintRelationLogicError).collect(Collectors.toCollection(ArrayList::new));
            for (LogicError logicError : oldLogicErrors) { this._removeLogicErrorFromDiagram(logicError); }
        }
    }
}
