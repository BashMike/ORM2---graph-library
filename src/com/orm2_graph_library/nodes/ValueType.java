package com.orm2_graph_library.nodes;

import com.orm2_graph_library.attributes.StringAttribute;
import com.orm2_graph_library.core.Diagram;

public class ValueType extends ObjectType {
    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public ValueType() { super(); }

    // ---------------- connection ----------------
    @Override
    public String basicName() { return "Value Type"; }
}
