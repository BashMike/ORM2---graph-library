package com.orm2_graph_library.nodes.common;

import com.orm2_graph_library.anchor_points.AnchorPosition;
import com.orm2_graph_library.anchor_points.NodeAnchorPoint;
import com.orm2_graph_library.core.AnchorPoint;

import java.util.stream.Stream;

public class ValueType extends ObjectType {
    // ================ OPERATIONS ================
    // ---------------- attributes ----------------
    // * Name
    @Override public String basicName() { return "Value Type"; }

    // * Anchor points
    public AnchorPoint<ValueType> centerAnchorPoint() { return new NodeAnchorPoint<>(this, AnchorPosition.CENTER); }

    @Override public Stream<AnchorPoint> anchorPoints() { return Stream.of(this.centerAnchorPoint()); }
}
