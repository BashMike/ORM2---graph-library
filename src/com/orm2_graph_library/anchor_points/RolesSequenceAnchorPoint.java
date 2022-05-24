package com.orm2_graph_library.anchor_points;

import com.orm2_graph_library.core.AnchorPoint;
import com.orm2_graph_library.nodes.predicates.Role;
import com.orm2_graph_library.nodes.predicates.RolesSequence;
import com.orm2_graph_library.utils.Point2D;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class RolesSequenceAnchorPoint extends AnchorPoint<RolesSequence> {
    public RolesSequenceAnchorPoint(@NotNull RolesSequence rolesSequence) {
        super(rolesSequence, null);
    }

    // TODO - @add :: Calculate position depending on chosen roles sequence and position of other connected element.
    @Override
    public Point2D position() {
        Point2D result = new Point2D(0, 0);

        this._owner.roles().forEach(e -> result.x += e.borderLeftTop().x);
        result.x /= this._owner.roles().count();

        result.y = this._owner.roles().findFirst().get().borderLeftTop().y + this._owner.roles().findFirst().get().borderHeight();

        return result;
    }
}
