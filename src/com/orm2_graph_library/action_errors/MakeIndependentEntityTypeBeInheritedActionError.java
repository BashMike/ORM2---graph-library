package com.orm2_graph_library.action_errors;

import com.orm2_graph_library.core.ActionError;
import com.orm2_graph_library.nodes.common.EntityType;
import org.jetbrains.annotations.NotNull;

public class MakeIndependentEntityTypeBeInheritedActionError extends ActionError {
    final private EntityType _entityType;

    public MakeIndependentEntityTypeBeInheritedActionError(@NotNull EntityType entityType) {
        this._entityType = entityType;
    }

    public EntityType entityType() { return this._entityType; }

    public String description() { return "Independent entity type \"" + this._entityType + "\" cannot inherit other entity type."; }

    @Override
    public boolean equals(Object other) {
        if (other instanceof MakeIndependentEntityTypeBeInheritedActionError otherConverted) {
            return (this._entityType == otherConverted._entityType);
        }
        else {
            return false;
        }
    }

    @Override
    public int hashCode() { return this._entityType.hashCode(); }
}
