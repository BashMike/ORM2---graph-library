package com.orm2_graph_library.nodes.constraints;

public class ExclusiveOrConstraint extends Constraint {
    // Exclusive Or constraint must connect with 2 or more elements
    @Override public int minRequiredConnectionsCount() { return 2; }
    @Override public int maxRequiredConnectionsCount() { return -1; }
}
