package com.orm2_graph_library.anchor_points;

import com.orm2_graph_library.core.AnchorPoint;
import com.orm2_graph_library.core.Node;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class NodeAnchorPoint<G extends Node> extends AnchorPoint<G> {
    public NodeAnchorPoint(G node, AnchorPosition anchorPosition) {
        super(node, anchorPosition);
    }

    @Override @NotNull
    public Point position() {
        Point result = (Point)this._owner.borderLeftTop().clone();

        switch (this._anchorPosition) {
            case UP    -> result.translate(this._owner.borderWidth()/2, 0);
            case DOWN  -> result.translate(this._owner.borderWidth()/2, this._owner.borderHeight());
            case RIGHT -> result.translate(this._owner.borderWidth(), this._owner.borderHeight()/2);
            case LEFT  -> result.translate(0, this._owner.borderHeight()/2);

            case CENTER -> result.translate(this._owner.borderWidth()/2, this._owner.borderHeight()/2);
        }

        return result;
    }
}
