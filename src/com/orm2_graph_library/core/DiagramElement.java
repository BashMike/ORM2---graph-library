package com.orm2_graph_library.core;

import java.util.ArrayList;
import java.util.stream.Stream;

public abstract class DiagramElement {
    // ================ ATTRIBUTES ================
    protected AttributeManager _attributes = new AttributeManager();

    // ---------------- connection ----------------
    private Diagram _owner = null;

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
    public Stream<? extends DiagramElement> getIncidentElements(Class<? extends DiagramElement> elementType) {
        return null;
    }
}
