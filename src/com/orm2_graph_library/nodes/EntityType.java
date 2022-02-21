package com.orm2_graph_library.nodes;

import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Stream;

import com.orm2_graph_library.attributes.StringAttribute;
import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.DiagramElement;
import com.orm2_graph_library.edges.SubtypeConnectorEdge;

public class EntityType extends ObjectType {
    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public EntityType() { super(); }

    // ---------------- attributes ----------------
    // ---------------- connection ----------------
    @Override
    public String basicName() { return "Entity Type"; }

    // ------------------ events ------------------
    /*
        1. NAME CHANGED :: Created and added to graph;
        2. NAME CHANGED :: Set new name when added in graph.
     */
}
