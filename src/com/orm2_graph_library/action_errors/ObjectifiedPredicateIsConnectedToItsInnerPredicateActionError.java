package com.orm2_graph_library.action_errors;

import com.orm2_graph_library.core.ActionError;
import com.orm2_graph_library.nodes.predicates.ObjectifiedPredicate;
import org.jetbrains.annotations.NotNull;

public class ObjectifiedPredicateIsConnectedToItsInnerPredicateActionError extends ActionError {
    // ================ ATTRIBUTES ================
    final private ObjectifiedPredicate _node;

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public ObjectifiedPredicateIsConnectedToItsInnerPredicateActionError(@NotNull ObjectifiedPredicate node) { this._node = node; }

    // ---------------- attributes ----------------
    public ObjectifiedPredicate node() { return this._node; }

    // ----------------- contract -----------------
    @Override
    public String description() { return "Objectified Prediccate is connected with its inner predicate."; }

    // ---------------- comparison ----------------
    @Override
    public boolean equals(Object other) {
        if (other instanceof ObjectifiedPredicateIsConnectedToItsInnerPredicateActionError otherConverted) {
            return (this._node == otherConverted._node);
        }
        else {
            return false;
        }
    }

    @Override
    public int hashCode() { return this._node.hashCode(); }
}
