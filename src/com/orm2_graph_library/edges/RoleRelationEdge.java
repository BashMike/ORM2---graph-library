package com.orm2_graph_library.edges;

import com.orm2_graph_library.core.AnchorPoint;
import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.Edge;
import com.orm2_graph_library.nodes.predicates.ObjectifiedPredicate;
import com.orm2_graph_library.nodes.predicates.Role;
import com.orm2_graph_library.nodes.predicates.RoleParticipant;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public class RoleRelationEdge extends Edge<Role, RoleParticipant> {
    // ================ ATTRIBUTES ================
    private boolean _isMandatory;

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public RoleRelationEdge(AnchorPoint<Role> beginAnchorPoint, AnchorPoint<RoleParticipant> endAnchorPoint) { super(beginAnchorPoint, endAnchorPoint); }

    // ---------------- connection ----------------
    @Override protected void _initSelf() {}
    @Override protected void _finalizeSelf() {}

    // ---------------- attributes ----------------
    // * "Is mandatory" flag
    public boolean isMandatory() { return this._isMandatory; }
    public void setIsMandatory(boolean isMandatory) {
    }

    // * Anchor points
    @Override public Stream<AnchorPoint> anchorPoints() { return Stream.of(); }

    // ================= SUBTYPES =================
    public class RoleRelationIsMandatoryFlagChangeAction extends Diagram.DiagramElementAttributeChangeAction {
        private RoleRelationIsMandatoryFlagChangeAction(@NotNull Diagram diagram, @NotNull RoleRelationEdge edge, @NotNull boolean oldIsMandatory, @NotNull boolean newIsMandatory) {
            super(diagram, edge, oldIsMandatory, newIsMandatory);

            // TODO - @check :: Objectified predicate and object type have same names.
            // this._postValidators.add(new ObjectTypesNameDuplicationPostValidator(diagram, this, node, oldName, newName));
        }

        public RoleRelationEdge edge()    { return (RoleRelationEdge)this._diagramElement; }
        public boolean newIsIndependent() { return (Boolean)this._newAttributeValue; }
        public boolean oldIsIndependent() { return (Boolean)this._oldAttributeValue; }

        @Override public void _execute() { ((RoleRelationEdge)this._diagramElement)._isMandatory = (Boolean)this._newAttributeValue; }
        @Override public void _undo()    { ((RoleRelationEdge)this._diagramElement)._isMandatory = (Boolean)this._oldAttributeValue; }
    }
}
