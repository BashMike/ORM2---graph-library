package com.orm2_graph_library.nodes.common;

import com.orm2_graph_library.nodes_geometry.EllipseGeometry;

public class EntityType extends ObjectType {
    // ================ ATTRIBUTES ================
    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public EntityType() {
        super();
        this._geometry = new EllipseGeometry();
    }

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
