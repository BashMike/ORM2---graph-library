package com.orm2_graph_library.core;

public abstract class Action {
    // ================ ATTRIBUTES ================
    protected Diagram _diagram;

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public Action(Diagram diagram) { this._diagram = diagram; }

    // ----------------- contract -----------------
    public final void execute() {
        this._diagram._actionManager.stopRecordingActions();
        this._execute();
        this._diagram._actionManager.startRecordingActions();
    }

    public final void undo() {
        this._diagram._actionManager.stopRecordingActions();
        this._undo();
        this._diagram._actionManager.startRecordingActions();
    }

    public abstract void _execute();
    public abstract void _undo();
}
