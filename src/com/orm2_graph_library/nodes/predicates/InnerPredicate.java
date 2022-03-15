package com.orm2_graph_library.nodes.predicates;

import com.orm2_graph_library.core.Diagram;

import java.awt.*;
import java.util.ArrayList;

public class InnerPredicate extends Predicate {
    // ================ ATTRIBUTES ================
    private ObjectifiedPredicate _ownerObjectifiedPredicate;

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    InnerPredicate(ObjectifiedPredicate objectifiedPredicate, int arity) {
        super(arity);
        this._ownerObjectifiedPredicate = objectifiedPredicate;
    }

    InnerPredicate(StandalonePredicate standalonePredicate) { super(standalonePredicate._roles); }

    // ---------------- connection ----------------
    @Override
    protected void _initSelf(Diagram owner) {}

    // ---------------- attributes ----------------
    @Override
    public Point borderLeftTop() {
        int x = this._ownerObjectifiedPredicate.borderLeftTop().x + ObjectifiedPredicate.horizontalEmptyGap();
        int y = this._ownerObjectifiedPredicate.borderLeftTop().y + ObjectifiedPredicate.verticalEmptyGap();

        return new Point(x, y);
    }
}
