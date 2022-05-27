package com.orm2_graph_library.action_errors;

import com.orm2_graph_library.core.ActionError;
import com.orm2_graph_library.nodes.common.EntityType;
import org.jetbrains.annotations.NotNull;

public class MakeChildEntityTypeIndependentActionError extends ActionError {
    final private EntityType _entityType;

    public MakeChildEntityTypeIndependentActionError(@NotNull EntityType entityType) {
        this._entityType = entityType;
    }

    public EntityType entityType() { return this._entityType; }

    public String description() { return "Entity type \"" + this._entityType + "\" cannot be independent because it has parents entity types."; }
}
