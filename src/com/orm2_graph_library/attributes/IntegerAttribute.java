package com.orm2_graph_library.attributes;

import com.orm2_graph_library.core.Attribute;
import com.orm2_graph_library.core.DiagramElement;

public class IntegerAttribute extends Attribute<Integer> {
    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public IntegerAttribute(DiagramElement diagramElement, String name, Integer value) { super(diagramElement, name, value); }
}
