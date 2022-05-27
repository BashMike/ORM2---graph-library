package com.orm2_graph_library.nodes.common;

import com.orm2_graph_library.anchor_points.AnchorPosition;
import com.orm2_graph_library.anchor_points.NodeAnchorPoint;
import com.orm2_graph_library.core.AnchorPoint;
import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.logic_errors.EntityTypeWithNoneRefModeLogicError;
import com.orm2_graph_library.logic_errors.ValueTypeWithNoneDataTypeLogicError;
import com.orm2_graph_library.nodes.common.data_types.NoneDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ValueType extends ObjectType {
    // ================ ATTRIBUTES ================
    private DataType _dataType = new NoneDataType();

    // ================ OPERATIONS ================
    // ---------------- attributes ----------------
    // * Name
    @Override public String basicName() { return "Value Type"; }

    // * Data type
    public DataType dataType() { return this._dataType; }
    public boolean hasDataType() { return !(this._dataType instanceof NoneDataType); }

    public void setDataType(DataType dataType) { this._ownerDiagramActionManager().executeAction(new ValueTypeDataTypeChangeAction(this._ownerDiagram, this, this._dataType, dataType)); }

    // * Anchor points
    public AnchorPoint<ValueType> centerAnchorPoint() { return new NodeAnchorPoint<>(this, AnchorPosition.CENTER); }

    @Override public Stream<AnchorPoint> anchorPoints() { return Stream.of(this.centerAnchorPoint()); }

    // ================= SUBTYPES =================
    // TODO - @add :: Validation on setting reference mode and data type for it.
    public class ValueTypeDataTypeChangeAction extends Diagram.DiagramElementAttributeChangeAction {
        private ValueTypeDataTypeChangeAction(@NotNull Diagram diagram, @NotNull ValueType node, @NotNull DataType oldDataType, @NotNull DataType newDataType) {
            super(diagram, node, oldDataType, newDataType);

            if (oldDataType.equals(new NoneDataType()) && !newDataType.equals(new NoneDataType())) {
                this._solvedLogicErrors.addAll(node.getLogicErrors(ValueTypeWithNoneDataTypeLogicError.class).collect(Collectors.toCollection(ArrayList::new)));
            }
            else if (!oldDataType.equals(new NoneDataType()) && newDataType.equals(new NoneDataType())) {
                this._emergedLogicErrors.add(new ValueTypeWithNoneDataTypeLogicError(node));
            }
        }

        @Override public void _execute() { ((ValueType)this._diagramElement)._dataType = (DataType)this._newAttributeValue; }
        @Override public void _undo()    { ((ValueType)this._diagramElement)._dataType = (DataType)this._oldAttributeValue; }
    }
}
