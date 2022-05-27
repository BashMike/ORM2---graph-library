package com.orm2_graph_library.nodes.common;

import com.orm2_graph_library.action_errors.MakeChildEntityTypeIndependentActionError;
import com.orm2_graph_library.action_errors.MakeIndependentEntityTypeBeInheritedActionError;
import com.orm2_graph_library.anchor_points.NodeAnchorPoint;
import com.orm2_graph_library.core.AnchorPoint;
import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.anchor_points.AnchorPosition;
import com.orm2_graph_library.edges.SubtypingRelationEdge;
import com.orm2_graph_library.logic_errors.EntityTypeWithNoneRefModeLogicError;
import com.orm2_graph_library.nodes.common.ref_modes.NoneRefMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntityType extends ObjectType {
    // ================ ATTRIBUTES ================
    private RefMode _refMode = new NoneRefMode();

    // ================ OPERATIONS ================
    // ---------------- attributes ----------------
    // * Name
    @Override public String basicName() { return "Entity Type"; }

    // * Reference mode
    public RefMode refMode() {
        if (!this._refMode.equals(new NoneRefMode())) {
            return this._refMode;
        }
        else {
            ArrayList<SubtypingRelationEdge> inheritPathEdges = this.getIncidentElements(SubtypingRelationEdge.class).filter(e -> e.begin() == this && e.isInheritPathForRefMode()).collect(Collectors.toCollection(ArrayList::new));

            if (!inheritPathEdges.isEmpty()) {
                while (inheritPathEdges.get(0).end()._refMode.equals(new NoneRefMode())) {
                    ArrayList<SubtypingRelationEdge> newInheritPathEdges = inheritPathEdges.get(0).end().getIncidentElements(SubtypingRelationEdge.class).filter(e -> e.begin() == inheritPathEdges.get(0).end() && e.isInheritPathForRefMode()).collect(Collectors.toCollection(ArrayList::new));

                    if (!newInheritPathEdges.isEmpty()) {
                        inheritPathEdges.set(0, newInheritPathEdges.get(0));
                    }
                    else {
                        break;
                    }
                }

                return inheritPathEdges.get(0).end()._refMode;
            }
            else {
                return this._refMode;
            }
        }
    }

    public boolean hasRefMode() { return !this.refMode().equals(new NoneRefMode()); }
    public boolean isRefModeSet() { return !this._refMode.equals(new NoneRefMode()); }

    public void setRefMode(RefMode refMode) { this._ownerDiagramActionManager().executeAction(new EntityTypeRefModeChangeAction(this._ownerDiagram, this, this._refMode, refMode)); }

    // * Anchor points
    public AnchorPoint<EntityType> centerAnchorPoint() { return new NodeAnchorPoint<>(this, AnchorPosition.CENTER); }

    @Override public Stream<AnchorPoint> anchorPoints() { return Stream.of(this.centerAnchorPoint()); }

    // ================= SUBTYPES =================
    // TODO - @add :: Validation on setting reference mode and data type for it.
    public class EntityTypeRefModeChangeAction extends Diagram.DiagramElementAttributeChangeAction {
        private EntityTypeRefModeChangeAction(@NotNull Diagram diagram, @NotNull EntityType node, @NotNull RefMode oldRefMode, @NotNull RefMode newRefMode) {
            super(diagram, node, oldRefMode, newRefMode);

            if (oldRefMode.equals(new NoneRefMode()) && !newRefMode.equals(new NoneRefMode())) {
                if (node.getIncidentElements(SubtypingRelationEdge.class).anyMatch(e -> e.begin() == node)) {
                    this._throwActionError(new MakeChildEntityTypeIndependentActionError(node));
                }

                this._solvedLogicErrors.addAll(node.getLogicErrors(EntityTypeWithNoneRefModeLogicError.class).collect(Collectors.toCollection(ArrayList::new)));
            }
            else if (!oldRefMode.equals(new NoneRefMode()) && newRefMode.equals(new NoneRefMode())) {
                this._emergedLogicErrors.add(new EntityTypeWithNoneRefModeLogicError(node));
            }
        }

        @Override public void _execute() { ((EntityType)this._diagramElement)._refMode = (RefMode)this._newAttributeValue; }
        @Override public void _undo()    { ((EntityType)this._diagramElement)._refMode = (RefMode)this._oldAttributeValue; }
    }
}
