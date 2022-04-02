package com.orm2_graph_library.core;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public abstract class Action {
    // ================ ATTRIBUTES ================
    protected Diagram _diagram;
    protected boolean _isValid = true;
    private boolean   _wasValidated = false;

    protected ArrayList<PostValidator> _postValidators     = new ArrayList<>();

    protected ArrayList<LogicError>    _solvedLogicErrors  = new ArrayList<>();
    protected ArrayList<LogicError>    _emergedLogicErrors = new ArrayList<>();

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public Action(@NotNull Diagram diagram) { this._diagram = diagram; }

    // ----------------- contract -----------------
    final public void execute() {
        this._diagram._actionManager.stopRecordingActions();
        this._execute();
        this._diagram._actionManager.startRecordingActions();

        for (LogicError logicError : this._emergedLogicErrors) { this._diagram._addLogicError(logicError); }
        for (LogicError logicError : this._solvedLogicErrors)  { this._diagram._removeLogicError(logicError); }

        if (!this._wasValidated) {
            for (PostValidator postValidator : this._postValidators) { postValidator.validate(); }
            this._wasValidated = true;
        }
    }

    final public void undo() {
        this._diagram._actionManager.stopRecordingActions();
        this._undo();
        this._diagram._actionManager.startRecordingActions();

        for (LogicError logicError : this._emergedLogicErrors) { this._diagram._removeLogicError(logicError); }
        for (LogicError logicError : this._solvedLogicErrors)  { this._diagram._addLogicError(logicError); }
    }

    abstract protected void _execute();
    abstract protected void _undo();

    protected void _becomeInvalid() { this._isValid = false; }

    protected void _throwActionError(@NotNull ActionError actionError) {
        this._becomeInvalid();
        for (ActionErrorListener actionErrorListener : this._diagram._actionErrorListeners) {
            actionErrorListener.handle(actionError);
        }
    }
}
