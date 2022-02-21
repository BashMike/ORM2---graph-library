package com.orm2_graph_library.core;

import java.util.ArrayList;

public abstract class DiagramElement {
    // ================ ATTRIBUTES ================
    protected AttributeManager _attributes = new AttributeManager();

    // ---------------- connection ----------------
    protected Diagram _owner = null;

    // ================ OPERATIONS ================
    // ---------------- attributes ----------------
    public AttributeManager attributes() { return this._attributes; }

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
