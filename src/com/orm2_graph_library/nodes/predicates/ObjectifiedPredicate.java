package com.orm2_graph_library.nodes.predicates;

import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.Movable;
import com.orm2_graph_library.core.Node;
import com.orm2_graph_library.nodes_shapes.RoundedRectangleShape;

import java.awt.*;

public class ObjectifiedPredicate extends RoleParticipant {
    // ================== STATIC ==================
    static private int _horizontalEmptyGap = 20;
    static private int _verticalEmptyGap   = 20;
    static private int _borderRadius       = 20;

    static public int horizontalEmptyGap() { return ObjectifiedPredicate._horizontalEmptyGap; }
    static public int verticalEmptyGap()   { return ObjectifiedPredicate._verticalEmptyGap; }
    static public int borderRadius()       { return ObjectifiedPredicate._borderRadius; }

    static public void setHorizontalEmptyGap(int horizontalEmptyGap) { ObjectifiedPredicate._horizontalEmptyGap = horizontalEmptyGap; }
    static public void setVerticalEmptyGap(int verticalEmptyGap)     { ObjectifiedPredicate._verticalEmptyGap   = verticalEmptyGap; }
    static public void setBorderRadius(int borderRadius)             { ObjectifiedPredicate._borderRadius       = borderRadius; }

    // ================ ATTRIBUTES ================
    private final InnerPredicate _innerPredicate;

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public ObjectifiedPredicate(int arity) {
        this._innerPredicate = new InnerPredicate(this, arity);
        this._shape = new RoundedRectangleShape();
    }

    ObjectifiedPredicate(InnerPredicate innerPredicate) { this._innerPredicate = innerPredicate; }

    public StandalonePredicate becomeStandalone() {
        return new StandalonePredicate(this._innerPredicate);
    }

    // ---------------- connection ----------------
    @Override
    protected void _initSelf(Diagram owner) {
        this._owner._actionManager().stopRecordingActions();
        owner.addNode(this._innerPredicate);
        this._owner._actionManager().startRecordingActions();
    }

    // ---------------- attributes ----------------
    public InnerPredicate innerPredicate() { return this._innerPredicate; }

    @Override
    public int borderWidth() { return ObjectifiedPredicate.horizontalEmptyGap() * 2 + this._innerPredicate.borderWidth(); }
    @Override
    public int borderHeight() { return ObjectifiedPredicate.verticalEmptyGap() * 2 + this._innerPredicate.borderHeight(); }
}
