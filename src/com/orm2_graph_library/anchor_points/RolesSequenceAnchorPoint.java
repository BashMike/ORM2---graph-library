package com.orm2_graph_library.anchor_points;

import com.orm2_graph_library.core.AnchorPoint;
import com.orm2_graph_library.nodes.predicates.Role;
import com.orm2_graph_library.nodes.predicates.RolesSequence;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class RolesSequenceAnchorPoint extends AnchorPoint<RolesSequence> {
    public RolesSequenceAnchorPoint(@NotNull RolesSequence rolesSequence) {
        super(rolesSequence, null);
    }

    // TODO - @add :: Calculate position depending on chosen roles sequence and position of other connected element.
    @Override
    public Point position() {
        Point result = new Point(0, 0);

        for (Role role : this._owner.roles()) { result.x += role.borderLeftTop().x; }
        result.x /= this._owner.roles().size();

        result.y = this._owner.roles().get(0).borderLeftTop().y + this._owner.roles().get(0).borderHeight();

        return result;
    }
}
