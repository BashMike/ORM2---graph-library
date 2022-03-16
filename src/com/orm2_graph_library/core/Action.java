package com.orm2_graph_library.core;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public abstract class Action {
    // ================ ATTRIBUTES ================
    protected Diagram _diagram;

    protected ArrayList<PostValidator> _postValidators     = new ArrayList<>();

    protected ArrayList<LogicError>    _solvedLogicErrors  = new ArrayList<>();
    protected ArrayList<LogicError>    _emergedLogicErrors = new ArrayList<>();

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public Action(@NotNull Diagram diagram) { this._diagram = diagram; }

    // ----------------- contract -----------------
    public final void execute() {
        this._diagram._actionManager.stopRecordingActions();
        this._execute();
        this._diagram._actionManager.startRecordingActions();

        if (this._emergedLogicErrors.isEmpty()) {
            for (PostValidator postValidator : this._postValidators) { postValidator.validate(); }
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

    protected void _throwActionError(@NotNull ActionError actionError) {
        this._diagram._actionManager._popLastAction();
        for (ActionErrorListener actionErrorListener : this._diagram._actionErrorListeners) {
            actionErrorListener.handle(actionError);
        }
    }
}
