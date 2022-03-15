package com.orm2_graph_library.edges;

import com.orm2_graph_library.core.AnchorPoint;
import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.Edge;
import com.orm2_graph_library.nodes.common.EntityType;

public class SubtypingRelationEdge extends Edge<EntityType, EntityType> {
    public SubtypingRelationEdge(AnchorPoint<EntityType> beginAnchorPoint, AnchorPoint<EntityType> endAnchorPoint) {
        super(beginAnchorPoint, endAnchorPoint);
    }

    @Override
    protected void _initSelf(Diagram owner) {}
}
