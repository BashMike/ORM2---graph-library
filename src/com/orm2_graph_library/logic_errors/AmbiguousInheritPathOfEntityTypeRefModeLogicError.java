package com.orm2_graph_library.logic_errors;

import com.orm2_graph_library.core.LogicError;
import com.orm2_graph_library.edges.SubtypingRelationEdge;
import com.orm2_graph_library.nodes.common.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.stream.Stream;

public class AmbiguousInheritPathOfEntityTypeRefModeLogicError extends LogicError {
    final private EntityType                       _entityType;
    final private ArrayList<SubtypingRelationEdge> _inheritEdgesCandidates = new ArrayList<>();

    public AmbiguousInheritPathOfEntityTypeRefModeLogicError(@NotNull EntityType entityType, @NotNull ArrayList<SubtypingRelationEdge> inheritEdgesCandidates) {
        this._entityType = entityType;
        this._inheritEdgesCandidates.addAll(inheritEdgesCandidates);

        this._errorParticipants.add(this._entityType);
        this._errorParticipants.addAll(this._inheritEdgesCandidates);
    }

    public EntityType entityType() { return this._entityType; }

    public Stream<SubtypingRelationEdge> inheritEdgesCandidates() { return this._inheritEdgesCandidates.stream(); }

    public String description() {
        String result = "Entity type \"" + this._entityType + "\" can inherit reference mode from several subtyping relation edges: subtypingRelationEdge@" + this._inheritEdgesCandidates.get(0);
        for (int i=1; i<this._inheritEdgesCandidates.size(); i++) {
            result += ", subtypingRelationEdge@" + this._inheritEdgesCandidates.get(i);
        }

        return result;
    }
}
