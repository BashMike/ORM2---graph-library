package com.orm2_graph_library.nodes.common;

import com.orm2_graph_library.nodes.common.ref_modes.NoneRefMode;

import java.util.ArrayList;

public abstract class RefMode {
    private EntityType            _ownerEntityType     = null;
    protected DataType            _currentDataType     = null;
    protected ArrayList<DataType> _acceptableDataTypes = new ArrayList<>();

    public ArrayList<DataType> acceptableDataTypes() { return new ArrayList<>(this._acceptableDataTypes); }

    public DataType dataType() { return this._currentDataType; }
    // public void setDataType(DataType dataType) { this._currentDataType = (DataType)dataType.clone(); }
    public void setDataType(DataType dataType) { this._currentDataType = dataType; }

    void _setOwnerEntityType(EntityType entityType) {
        this._ownerEntityType = entityType;
    }
}
