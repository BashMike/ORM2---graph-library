package com.orm2_graph_library.logic_errors;

import com.orm2_graph_library.core.LogicError;
import com.orm2_graph_library.nodes.common.EntityType;

public class EntityTypeWithNoneRefModeLogicError extends LogicError {
    private final EntityType _entityType;

    public EntityTypeWithNoneRefModeLogicError(EntityType entityType) {
        super();
        this._entityType = entityType;
        this._errorParticipants.add(this._entityType);
    }

    public EntityType entityType() { return this._entityType; }

    @Override
    public String description() {
        return "Entity type \"" + this._entityType.name() + "\" with NONE reference mode set.";
    }

    @Override
    public int hashCode() { return this._entityType.name().length(); }
}
