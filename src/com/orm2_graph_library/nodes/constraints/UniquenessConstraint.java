package com.orm2_graph_library.nodes.constraints;

public class UniquenessConstraint extends Constraint {
    // Uniqueness constraint must connect with 1 or more elements
    @Override public int minRequiredConnectionsCount() { return 1; }
    @Override public int maxRequiredConnectionsCount() { return -1; }
}
