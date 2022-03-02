package com.orm2_graph_library.core;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public abstract class Action {
    // ================ ATTRIBUTES ================
    protected Diagram _diagram;

    protected ArrayList<LogicError> _solvedLogicErrors  = new ArrayList<>();
    protected ArrayList<LogicError> _emergedLogicErrors = new ArrayList<>();

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public Action(@NotNull Diagram diagram) { this._diagram = diagram; }

    // ----------------- contract -----------------
    public final void execute() {
        this._diagram._actionManager.stopRecordingActions();
        this._execute();
        this._diagram._actionManager.startRecordingActions();

        if (this._emergedLogicErrors.isEmpty()) {
            this._validate();
        }
        else {
            for (LogicError logicError : this._emergedLogicErrors) { this._diagram._addLogicError(logicError); }
            for (LogicError logicError : this._solvedLogicErrors)  { this._diagram._removeLogicError(logicError); }
        }
    }

    public final void undo() {
        this._diagram._actionManager.stopRecordingActions();
        this._undo();
        this._diagram._actionManager.startRecordingActions();

        for (LogicError logicError : this._emergedLogicErrors) { this._diagram._removeLogicError(logicError); }
        for (LogicError logicError : this._solvedLogicErrors)  { this._diagram._addLogicError(logicError); }
    }

    public abstract void _execute();
    public abstract void _undo();
    public abstract void _validate();

    protected void _addLogicErrorToDiagram(LogicError logicError) {
        this._diagram._addLogicError(logicError);
        this._emergedLogicErrors.add(logicError);
    }

    protected void _removeLogicErrorFromDiagram(LogicError logicError) {
        this._diagram._addLogicError(logicError);
        this._solvedLogicErrors.add(logicError);
    }
}
