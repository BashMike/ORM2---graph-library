package com.orm2_graph_library.core;

import org.jetbrains.annotations.NotNull;

public abstract class PostValidator {
    // ================ ATTRIBUTES ================
    protected Diagram _diagram;
    private Action    _action;

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public PostValidator(@NotNull Diagram diagram, @NotNull Action action) {
        this._diagram = diagram;
        this._action  = action;
    }

    // ----------------- contract -----------------
    public abstract void validate();

    protected void _addLogicErrorToDiagram(LogicError logicError) {
        this._diagram._addLogicError(logicError);
        this._action._emergedLogicErrors.add(logicError);
    }

    protected void _removeLogicErrorFromDiagram(LogicError logicError) {
        this._diagram._addLogicError(logicError);
        this._action._solvedLogicErrors.add(logicError);
    }
}
