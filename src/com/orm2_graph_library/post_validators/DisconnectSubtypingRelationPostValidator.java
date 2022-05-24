package com.orm2_graph_library.post_validators;

import com.orm2_graph_library.core.Action;
import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.LogicError;
import com.orm2_graph_library.core.PostValidator;
import com.orm2_graph_library.edges.SubtypingRelationEdge;
import com.orm2_graph_library.logic_errors.SubtypingCycleLogicError;
import com.orm2_graph_library.nodes.common.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DisconnectSubtypingRelationPostValidator extends PostValidator {
    final private SubtypingRelationEdge _edge;

    // TODO - @modify :: Store edge which probably starts subtyping cycle.
    public DisconnectSubtypingRelationPostValidator(@NotNull Diagram diagram, @NotNull Action action, @NotNull SubtypingRelationEdge edge) {
        super(diagram, action);
        this._edge = edge;
    }

    @Override
    protected void validate() {
        ArrayList<LogicError> oldLogicErrors = this._diagram.getLogicErrorsFor(this._edge).filter(e -> e instanceof SubtypingCycleLogicError).collect(Collectors.toCollection(ArrayList::new));
        for (LogicError logicError : oldLogicErrors) { this._removeLogicErrorFromDiagram(logicError); }
    }
}
