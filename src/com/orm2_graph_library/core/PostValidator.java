package com.orm2_graph_library.core;

public abstract class PostValidator<ActionType extends Action> {
    public abstract void validate(Diagram diagram, ActionType action);
}
