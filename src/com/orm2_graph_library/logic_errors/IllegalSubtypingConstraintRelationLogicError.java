package com.orm2_graph_library.logic_errors;

import com.orm2_graph_library.core.LogicError;
import com.orm2_graph_library.edges.SubtypingConstraintRelationEdge;
import org.jetbrains.annotations.NotNull;

public class IllegalSubtypingConstraintRelationLogicError extends LogicError {
    final private SubtypingConstraintRelationEdge _edge;

    public IllegalSubtypingConstraintRelationLogicError(@NotNull SubtypingConstraintRelationEdge edge) {
        this._edge = edge;
        this._errorParticipants.add(this._edge);
    }

    @Override
    public String description() {
        return "Constraint \"" + this._edge.end().getClass() + "\" cannot be connected with \"" + this._edge.getClass() + "\" edge";
    }
}
