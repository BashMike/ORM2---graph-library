package com.orm2_graph_library.nodes.constraints;

public class SubsetConstraint extends Constraint {
    // Subset constraint must connect with only 2 elements
    @Override public int minRequiredConnectionsCount() { return 2; }
    @Override public int maxRequiredConnectionsCount() { return 2; }
}
