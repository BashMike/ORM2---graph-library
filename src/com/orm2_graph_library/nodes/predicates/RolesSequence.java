package com.orm2_graph_library.nodes.predicates;

import com.orm2_graph_library.core.AnchorPoint;
import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.DiagramElement;
import com.orm2_graph_library.core.GeometryApproximation;

import java.awt.*;
import java.util.ArrayList;

public class RolesSequence extends DiagramElement {
    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    @Override
    protected void _initSelf(Diagram owner) {}

    // ---------------- attributes ----------------
    public AnchorPoint<RolesSequence> anchorPoint() { return null; }

    @Override
    public GeometryApproximation geometryApproximation() { return null; }

    // ----------------- contract -----------------
    @Override
    public <T extends DiagramElement> ArrayList<T> getIncidentElements(Class<T> elementType) { return new ArrayList<>(); }
}
