package com.orm2_graph_library.post_validators;

import com.orm2_graph_library.core.Action;
import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.PostValidator;
import com.orm2_graph_library.edges.SubtypingConstraintRelationEdge;
import com.orm2_graph_library.logic_errors.IllegalSubtypingConstraintRelationLogicError;
import com.orm2_graph_library.nodes.constraints.*;
import org.jetbrains.annotations.NotNull;

public class ConnectBySubtypingConstraintRelationPostValidator extends PostValidator {
    final private Constraint                      _constraint;
    final private SubtypingConstraintRelationEdge _edge;

    public ConnectBySubtypingConstraintRelationPostValidator(@NotNull Diagram diagram,
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
            this._addLogicErrorToDiagram(new IllegalSubtypingConstraintRelationLogicError(this._edge));
        }
    }
}
