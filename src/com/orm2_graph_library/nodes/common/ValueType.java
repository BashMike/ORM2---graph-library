package com.orm2_graph_library.nodes.common;

import com.orm2_graph_library.anchor_points.AnchorPosition;
import com.orm2_graph_library.anchor_points.NodeAnchorPoint;
import com.orm2_graph_library.core.AnchorPoint;
import com.orm2_graph_library.nodes.predicates.Role;

public class ValueType extends ObjectType {
    // ================ OPERATIONS ================
    // ---------------- attributes ----------------
    @Override
    public String basicName() { return "Value Type"; }

    public AnchorPoint<ValueType> centerAnchorPoint() { return new NodeAnchorPoint<>(this, AnchorPosition.CENTER); }
}
