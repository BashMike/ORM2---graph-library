package com.orm2_graph_library.nodes.common;

import com.orm2_graph_library.anchor_points.NodeAnchorPoint;
import com.orm2_graph_library.core.AnchorPoint;
import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.anchor_points.AnchorPosition;
import com.orm2_graph_library.nodes.common.ref_modes.NoneRefMode;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public class EntityType extends ObjectType {
    // ================= SUBTYPES =================
    // TODO - @check & @add :: Enlarge list of reference modes.
    // TODO - @check        :: Connection between reference modes and data types.
    // ================ ATTRIBUTES ================
    private RefMode _refMode = new NoneRefMode();

    // ================ OPERATIONS ================
    // ---------------- attributes ----------------
    // * Name
    @Override public String basicName() { return "Entity Type"; }

    // * Reference mode
    public RefMode refMode() { return this._refMode; }
    public void setRefMode(RefMode refMode) { this._ownerDiagramActionManager().executeAction(new EntityTypeRefModeChangeAction(this._ownerDiagram, this, this._refMode, refMode)); }

    // * Anchor points
    public AnchorPoint<EntityType> centerAnchorPoint() { return new NodeAnchorPoint<>(this, AnchorPosition.CENTER); }

    @Override public Stream<AnchorPoint> anchorPoints() { return Stream.of(this.centerAnchorPoint()); }

    // ================= SUBTYPES =================
    // TODO - @add :: Validation on setting reference mode and data type for it.
    public class EntityTypeRefModeChangeAction extends Diagram.DiagramElementAttributeChangeAction {
        private EntityTypeRefModeChangeAction(@NotNull Diagram diagram, @NotNull EntityType node, @NotNull RefMode oldRefMode, @NotNull RefMode newRefMode) { super(diagram, node, oldRefMode, newRefMode); }

        @Override public void _execute() {
            ((RefMode)this._newAttributeValue)._setOwnerEntityType((EntityType)this._diagramElement);
            ((EntityType)this._diagramElement)._refMode = (RefMode)this._newAttributeValue;
        }

        @Override public void _undo() { ((EntityType)this._diagramElement)._refMode = (RefMode)this._oldAttributeValue; }
    }
}
