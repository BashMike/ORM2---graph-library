package com.orm2_graph_library.nodes.common;

import com.orm2_graph_library.anchor_points.NodeAnchorPoint;
import com.orm2_graph_library.core.AnchorPoint;
import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.anchor_points.AnchorPosition;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;

public class EntityType extends ObjectType {
    // ================= SUBTYPES =================
    // TODO - @check & @add :: Enlarge list of reference modes.
    // TODO - @check        :: Connection between reference modes and data types.
    // TODO - @check        :: How to set external reference mode.
    public enum RefMode {
        NONE,
        ID,
        NAME;

        public boolean isAcceptableFor(DataType dataType) {
            boolean result = false;

            switch(this) {
                case NONE:
                    if (dataType.equals(DataType.NONE)) { result = true; }
                    break;

                case ID:
                    break;

                case NAME:
                    break;
            }

            return result;
        }
    }

    // TODO - @check & @add :: Enlarge list of data types.
    public enum DataType {
        NONE,
        LOGICAL,
        INTEGER,
        FLOAT, // TODO - @check :: data types can be float.
        TIME,
        DATE,
        TEXT
    }

    // ================ ATTRIBUTES ================
    private RefMode  _refMode  = RefMode.NONE;
    private DataType _dataType = DataType.NONE;

    // ================ OPERATIONS ================
    // ---------------- connection ----------------
    @Override
    protected void _initSelf(Diagram owner) { super._initSelf(owner); }

    // ---------------- attributes ----------------
    @Override
    public String basicName() { return "Entity Type"; }

    public AnchorPoint<EntityType> centerAnchorPoint()            { return new NodeAnchorPoint<>(this, AnchorPosition.CENTER); }
    public AnchorPoint<EntityType> upAnchorPoint()                { return new NodeAnchorPoint<>(this, AnchorPosition.UP); }
    public AnchorPoint<EntityType> downAnchorPoint()              { return new NodeAnchorPoint<>(this, AnchorPosition.DOWN); }
    public AnchorPoint<EntityType> rightAnchorPoint()             { return new NodeAnchorPoint<>(this, AnchorPosition.RIGHT); }
    public AnchorPoint<EntityType> leftAnchorPoint()              { return new NodeAnchorPoint<>(this, AnchorPosition.LEFT); }

    public RefMode refMode() { return this._refMode; }
    public void setRefMode(RefMode refMode) { this._owner._actionManager().executeAction(new EntityTypeRefModeChangeAction(this._owner, this, this._refMode, refMode)); }

    public DataType dataType() { return this._dataType; }
    public void setDataType(DataType dataType) { this._owner._actionManager().executeAction(new EntityTypeDataTypeChangeAction(this._owner, this, this._dataType, dataType)); }

    // ================= SUBTYPES =================
    // TODO - @add :: Validation on setting reference mode and data type for it.
    private class EntityTypeRefModeChangeAction extends ObjectType.ObjectTypeAttributeChangeAction {
        public EntityTypeRefModeChangeAction(@NotNull Diagram diagram, @NotNull EntityType node, @NotNull RefMode oldRefMode, @NotNull RefMode newRefMode) { super(diagram, node, oldRefMode, newRefMode); }

        @Override
        public void _execute() { ((EntityType)this._node)._refMode = (RefMode)this._newAttributeValue; }
        @Override
        public void _undo() { ((EntityType)this._node)._refMode = (RefMode)this._oldAttributeValue; }
    }

    private class EntityTypeDataTypeChangeAction extends ObjectType.ObjectTypeAttributeChangeAction {
        public EntityTypeDataTypeChangeAction(@NotNull Diagram diagram, @NotNull EntityType node, @NotNull DataType oldDataType, @NotNull DataType newDataType) { super(diagram, node, oldDataType, newDataType); }

        @Override
        public void _execute() { ((EntityType)this._node)._dataType = (DataType)this._newAttributeValue; }
        @Override
        public void _undo() { ((EntityType)this._node)._dataType = (DataType)this._oldAttributeValue; }
    }
}
