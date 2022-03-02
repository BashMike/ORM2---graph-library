package com.orm2_graph_library.core;

import java.util.ArrayList;

public abstract class DiagramElement {
    // ================ ATTRIBUTES ================
    // ---------------- connection ----------------
    protected Diagram _owner = null;

    // ================ OPERATIONS ================
    // ---------------- connection ----------------
    public boolean hasOwner()       { return (this._owner != null); }
    void setOwner(Diagram owner) {
        this._owner = owner;
        this._initSelf(this._owner);
    }
    void unsetOwner() { this._owner = null; }

    protected abstract void _initSelf(Diagram owner);

    // ----------------- contract -----------------
    abstract public <T extends DiagramElement> ArrayList<T> getIncidentElements(Class<T> elementType);
}
