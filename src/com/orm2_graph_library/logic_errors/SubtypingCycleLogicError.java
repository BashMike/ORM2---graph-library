package com.orm2_graph_library.logic_errors;

import com.orm2_graph_library.core.LogicError;
import com.orm2_graph_library.nodes.common.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SubtypingCycleLogicError extends LogicError {
    private ArrayList<EntityType> _entityTypeCycle = new ArrayList<>();

    public SubtypingCycleLogicError(@NotNull ArrayList<EntityType> entityTypeCycle) { this._entityTypeCycle.addAll(entityTypeCycle); }

    public ArrayList<EntityType> entityTypeCycle() { return this._entityTypeCycle; }

    @Override
    public String description() {
        String result = "Subtyping cycle: ";
        for (EntityType entityType : this._entityTypeCycle) {
            result += entityType + " -> ";
        }
        result += this._entityTypeCycle.get(0);

        return result;
    }
}
