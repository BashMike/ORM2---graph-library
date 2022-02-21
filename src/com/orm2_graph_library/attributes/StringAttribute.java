package com.orm2_graph_library.attributes;

import com.orm2_graph_library.core.Attribute;
import com.orm2_graph_library.core.DiagramElement;

public class StringAttribute extends Attribute<String> {
    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public StringAttribute(DiagramElement diagramElement, String name, String value) { super(diagramElement, name, value); }
}
