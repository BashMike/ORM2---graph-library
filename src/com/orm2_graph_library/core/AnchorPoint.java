package com.orm2_graph_library.core;

import com.orm2_graph_library.anchor_points.AnchorPosition;

import java.awt.*;

public abstract class AnchorPoint<T extends DiagramElement> {
    protected final T        _owner;
    protected AnchorPosition _anchorPosition;

    protected AnchorPoint(T owner, AnchorPosition anchorPosition) {
        this._owner          = owner;
        this._anchorPosition = anchorPosition;
    }

    public T owner() { return this._owner; }
    abstract public Point position();
}
