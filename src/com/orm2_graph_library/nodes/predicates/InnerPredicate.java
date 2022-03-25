package com.orm2_graph_library.nodes.predicates;

import java.awt.*;

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
    public ObjectifiedPredicate ownerObjectifiedPredicate() { return this._ownerObjectifiedPredicate; }

    // ---------------- attributes ----------------
    @Override
    public Point borderLeftTop() {
        int x = this._ownerObjectifiedPredicate.borderLeftTop().x + (this._ownerObjectifiedPredicate.borderWidth() - this.borderWidth()) / 2;
        int y = this._ownerObjectifiedPredicate.borderLeftTop().y + (this._ownerObjectifiedPredicate.borderHeight() - this.borderHeight()) / 2;

        return new Point(x, y);
    }
}
