package com.orm2_graph_library.nodes.common;

import com.orm2_graph_library.core.AnchorPoint;
import com.orm2_graph_library.core.Diagram;

import java.awt.*;

public class EntityType extends ObjectType {
    // ================ ATTRIBUTES ================
    private AnchorPoint<EntityType> _centerAnchorPoint;

    // ================ OPERATIONS ================
    // ---------------- connection ----------------
    @Override
    protected void _initSelf(Diagram owner) {
        super._initSelf(owner);
        this._centerAnchorPoint = new AnchorPoint(this, new Point(this._leftTop.x + this.borderWidth()/2, this._leftTop.y + this.borderHeight()/2));
    }

    // ---------------- attributes ----------------
    @Override
    public String basicName() { return "Entity Type"; }

    public AnchorPoint<EntityType> centerAnchorPoint() { return this._centerAnchorPoint; }
}
