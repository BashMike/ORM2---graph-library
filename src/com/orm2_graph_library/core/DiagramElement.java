package com.orm2_graph_library.core;

import org.jetbrains.annotations.NotNull;

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
    public boolean hasOwnerDiagram() { return (this._ownerDiagram != null); }
    public boolean isOwnerDiagram(Diagram diagram) { return (this._ownerDiagram != null && this._ownerDiagram == diagram); }
    @NotNull public Diagram ownerDiagram() { return this._ownerDiagram; }

    void setOwnerDiagram(Diagram ownerDiagram) {
        this._ownerDiagram = ownerDiagram;
        this._initSelf(this._ownerDiagram);
    }

    void unsetOwnerDiagram() { this._ownerDiagram = null; }

    abstract protected void _initSelf(Diagram owner);

    protected void _stopDiagramRecordingActions()  { this._ownerDiagram._actionManager.stopRecordingActions(); }
    protected void _startDiagramRecordingActions() { this._ownerDiagram._actionManager.startRecordingActions(); }

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
