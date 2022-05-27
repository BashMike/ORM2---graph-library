package com.orm2_graph_library.core;

import com.orm2_graph_library.anchor_points.AnchorPosition;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
    @NotNull protected ActionManager _ownerDiagramActionManager() { return this._ownerDiagram._actionManager; }

    void setOwnerDiagram(Diagram ownerDiagram) {
        this._ownerDiagram = ownerDiagram;
        if (this._ownerDiagram._initDiagramElementsFlag) { this._initSelf(); }
    }

    void unsetOwnerDiagram() {
        if (this._ownerDiagram._initDiagramElementsFlag) { this._finalizeSelf(); }
        this._ownerDiagram = null;
    }

    abstract protected void _initSelf();
    abstract protected void _finalizeSelf();

    // TODO :: @check - Not stopping recording actions.
    protected void _stopDiagramRecordingActions()  { this._ownerDiagram._actionManager.stopRecordingActions(); }
    protected void _startDiagramRecordingActions() { this._ownerDiagram._actionManager.startRecordingActions(); }

    protected <T extends DiagramElement> T    _addDiagramElementToOwnerDiagram     (@NotNull T diagramElement) { return this._ownerDiagram._addElement(diagramElement); }
    protected <T extends DiagramElement> void _removeDiagramElementFromOwnerDiagram(@NotNull T diagramElement) { this._ownerDiagram._removeElement(diagramElement); }
    protected <T extends DiagramElement> T    _addDiagramElementToDiagram     (@NotNull Diagram diagram, @NotNull T diagramElement) { return diagram._addElement(diagramElement); }
    protected <T extends DiagramElement> void _removeDiagramElementFromDiagram(@NotNull Diagram diagram, @NotNull T diagramElement) { diagram._removeElement(diagramElement); }

    // ---------------- attributes ----------------
    // * Anchor points
    abstract public Stream<AnchorPoint> anchorPoints();

    // * Logic errors
    public<T extends LogicError> Stream<T> getLogicErrors(@NotNull Class<T> logicErrorsType) { return this._ownerDiagram.getLogicErrorsFor(this).filter(e -> logicErrorsType.isAssignableFrom(e.getClass())).map(e -> (T)e); }
    public<T extends LogicError> boolean hasLogicErrors(@NotNull Class<T> logicErrorsType) { return this._ownerDiagram.getLogicErrorsFor(this).anyMatch(e -> logicErrorsType.isAssignableFrom(e.getClass())); }

    // * Geometry
    abstract public GeometryApproximation geometryApproximation();

    // ----------------- contract -----------------
    abstract public <T extends DiagramElement> Stream<T> getIncidentElements(Class<T> elementType);

    public <T extends DiagramElement> boolean hasIncidentElements(Class<T> elementType) { return (this.getIncidentElements(elementType).count() != 0); }
    public <T extends DiagramElement> boolean hasOnlyIncidentElementsWith(Class<T> elementType) {
        return this.getIncidentElements(elementType).anyMatch(e -> elementType.isAssignableFrom(elementType));
    }

    public boolean isIncidentElement(DiagramElement diagramElement) { return this.getIncidentElements(diagramElement.getClass()).anyMatch(e -> e == diagramElement); }
}
