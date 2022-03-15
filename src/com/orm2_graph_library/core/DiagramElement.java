package com.orm2_graph_library.core;

import java.util.ArrayList;

public abstract class DiagramElement {
    // ================== STATIC ==================
    public enum Orientation { HORIZONTAL, VERTICAL }

    // ================ ATTRIBUTES ================
    // ---------------- connection ----------------
    protected Diagram _owner = null;

    // ================ OPERATIONS ================
    // ---------------- connection ----------------
    public boolean hasOwner() { return (this._owner != null); }
    public boolean isOwner(Diagram diagram) { return (this._owner != null && this._owner == diagram); }

    void setOwner(Diagram owner) {
        this._owner = owner;
        this._initSelf(this._owner);
    }

    void unsetOwner() { this._owner = null; }

    abstract protected void _initSelf(Diagram owner);

    // ---------------- attributes ----------------
    abstract public GeometryApproximation geometryApproximation();

    // ----------------- contract -----------------
    abstract public <T extends DiagramElement> ArrayList<T> getIncidentElements(Class<T> elementType);
}
