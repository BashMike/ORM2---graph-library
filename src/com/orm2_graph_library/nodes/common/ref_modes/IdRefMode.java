package com.orm2_graph_library.nodes.common.ref_modes;

import com.orm2_graph_library.nodes.common.DataType;
import com.orm2_graph_library.nodes.common.RefMode;
import com.orm2_graph_library.nodes.common.data_types.IntegerDataType;
import com.orm2_graph_library.nodes.common.data_types.TextDataType;

public class IdRefMode extends RefMode {
    public IdRefMode() {
        // TODO - @check :: What data types can be set to ID ref mode.
        this._acceptableDataTypes.add(new IntegerDataType());
        this._acceptableDataTypes.add(new TextDataType());
    }
}
