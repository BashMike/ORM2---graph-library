package com.orm2_graph_library.core;

public abstract class Action {
    protected Diagram _diagram;

    public Action(Diagram diagram) { this._diagram = diagram; }

    public abstract void execute();
    public abstract void undo();
}
