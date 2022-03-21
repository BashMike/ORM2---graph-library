package com.orm2_graph_library.anchor_points;

import com.orm2_graph_library.core.AnchorPoint;
import com.orm2_graph_library.core.Edge;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class EdgeAnchorPoint<G extends Edge> extends AnchorPoint<G> {
    public EdgeAnchorPoint(G edge) { super(edge, null); }

    @Override @NotNull
    public Point position() {
        int x = (this._owner.beginAnchorPoint().position().x - this._owner.endAnchorPoint().position().x) / 2;
        int y = (this._owner.beginAnchorPoint().position().y - this._owner.endAnchorPoint().position().y) / 2;

        return new Point(x, y);
    }
}
