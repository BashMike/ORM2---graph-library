package com.orm2_graph_library.edges;

import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.Edge;
import com.orm2_graph_library.nodes.ObjectType;

public class SubtypeConnectorEdge extends Edge {
    public SubtypeConnectorEdge(ObjectType beginNode, ObjectType endNode) {
        super(beginNode, endNode);
    }

    @Override
    protected void _initSelf(Diagram owner) {}
}
