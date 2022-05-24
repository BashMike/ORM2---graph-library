package com.orm2_graph_library.core;

import com.orm2_graph_library.anchor_points.AnchorPosition;
import com.orm2_graph_library.utils.Point2D;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public abstract class AnchorPoint<T extends DiagramElement> {
    protected final T        _owner;
    protected AnchorPosition _anchorPosition;

    protected AnchorPoint(T owner, AnchorPosition anchorPosition) {
        this._owner          = owner;
        this._anchorPosition = anchorPosition;
    }

    public T owner() { return this._owner; }
    abstract public Point2D position();

    @Override
    public boolean equals(@NotNull Object other) {
        if (this.getClass() == other.getClass()) {
            return (this._owner == ((AnchorPoint)other)._owner && this._anchorPosition.equals(((AnchorPoint)other)._anchorPosition));
        }
        else {
            return false;
        }
    }
}
