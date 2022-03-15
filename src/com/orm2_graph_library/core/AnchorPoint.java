package com.orm2_graph_library.core;

import java.awt.*;

public class AnchorPoint<T extends DiagramElement> {
    private final T _owner;
    private Point _position;

    public AnchorPoint(T owner, Point position) {
        this._owner    = owner;
        this._position = position;
    }

    public T owner() { return this._owner; }
    public Point position() { return (Point)this._position.clone(); }

    void _moveTo(Point position) { this._position.move(position.x, position.y); }
}
