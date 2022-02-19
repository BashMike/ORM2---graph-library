package com.orm2_graph_library.core;

import org.jetbrains.annotations.NotNull;

public abstract class Edge <B extends DiagramElement, E extends DiagramElement> extends DiagramElement {
    // ================ ATTRIBUTES ================
    private B _begin;
    private E _end;

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public Edge(@NotNull B begin, @NotNull E end) {
        this._begin = begin;
        this._end   = end;
    }

    // ---------------- attributes ----------------
    public B begin()    { return this._begin; }
    public E end()      { return this._end; }
}
