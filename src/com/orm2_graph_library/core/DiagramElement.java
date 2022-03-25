package com.orm2_graph_library.core;

import java.util.ArrayList;
import java.util.stream.Stream;

public abstract class DiagramElement {
    // ================== STATIC ==================
    public enum Orientation { HORIZONTAL, VERTICAL }

    // ================ ATTRIBUTES ================
    // ---------------- connection ----------------
    protected Diagram _ownerDiagram = null;

    // ================ OPERATIONS ================
    // ---------------- connection ----------------
    public boolean hasOwner() { return (this._ownerDiagram != null); }
    public boolean isOwner(Diagram diagram) { return (this._ownerDiagram != null && this._ownerDiagram == diagram); }

    void setOwner(Diagram owner) {
        this._ownerDiagram = owner;
        this._initSelf(this._ownerDiagram);
    }

    void unsetOwner() { this._ownerDiagram = null; }

    abstract protected void _initSelf(Diagram owner);

    // ---------------- attributes ----------------
    abstract public GeometryApproximation geometryApproximation();

    // ----------------- contract -----------------
    abstract public <T extends DiagramElement> ArrayList<T> getIncidentElements(Class<T> elementType);

    public <T extends DiagramElement> boolean hasIncidentElements(Class<T> elementType) { return !this.getIncidentElements(elementType).isEmpty(); }
    public <T extends DiagramElement> boolean hasOnlyIncidentElementsWith(Class<T> elementType) {
        return this.getIncidentElements(elementType).stream().anyMatch(e -> elementType.isAssignableFrom(elementType));
    }

    public boolean isIncidentElement(DiagramElement diagramElement) { return this.getIncidentElements(diagramElement.getClass()).contains(diagramElement); }
}
