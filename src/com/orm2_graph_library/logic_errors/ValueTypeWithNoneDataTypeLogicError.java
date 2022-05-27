package com.orm2_graph_library.logic_errors;

import com.orm2_graph_library.core.LogicError;
import com.orm2_graph_library.nodes.common.ValueType;

public class ValueTypeWithNoneDataTypeLogicError extends LogicError {
    private final ValueType _valueType;

    public ValueTypeWithNoneDataTypeLogicError(ValueType valueType) {
        super();
        this._valueType = valueType;
        this._errorParticipants.add(this._valueType);
    }

    public ValueType valueType() { return this._valueType; }

    @Override
    public String description() {
        return "Value type \"" + this._valueType.name() + "\" with NONE data type.";
    }

    @Override
    public int hashCode() { return this._valueType.name().length(); }
}
