package com.orm2_graph_library.core;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public abstract class Edge <B extends DiagramElement, E extends DiagramElement> extends DiagramElement {
    // ================ ATTRIBUTES ================
    private final B _begin;
    private final E _end;

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public Edge(@NotNull B begin, @NotNull E end) {
        this._begin = begin;
        this._end   = end;
    }

    // ---------------- attributes ----------------
    public B begin() { return this._begin; }
    public E end()   { return this._end; }

    // ----------------- contract -----------------
    @Override
    public <T extends DiagramElement> ArrayList<T> getIncidentElements(Class<T> elementType) {
        ArrayList<T> result = new ArrayList<>();
        assert false : "TODO";
        return result;
    }
}
