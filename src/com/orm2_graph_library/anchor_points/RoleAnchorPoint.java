package com.orm2_graph_library.anchor_points;

import com.orm2_graph_library.core.DiagramElement;
import com.orm2_graph_library.nodes.predicates.Role;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class RoleAnchorPoint extends NodeAnchorPoint<Role> {
    private final DiagramElement.Orientation _initialOrientation;

    public RoleAnchorPoint(Role role, AnchorPosition anchorPosition) {
        super(role, anchorPosition);
        this._initialOrientation = role.ownerPredicate().orientation();
    }

    @Override @NotNull
    public Point position() {
        if (this._initialOrientation == this._owner.ownerPredicate().orientation()) {
            return super.position();
        }
        else {
            if (this._initialOrientation == DiagramElement.Orientation.HORIZONTAL) {
                switch (this._anchorPosition) {
                    case UP:    return (new NodeAnchorPoint<>(this._owner, AnchorPosition.RIGHT)).position();
                    case RIGHT: return (new NodeAnchorPoint<>(this._owner, AnchorPosition.DOWN)) .position();
                    case DOWN:  return (new NodeAnchorPoint<>(this._owner, AnchorPosition.LEFT)) .position();
                    case LEFT:  return (new NodeAnchorPoint<>(this._owner, AnchorPosition.UP))   .position();
                }
            }
            else if (this._initialOrientation == DiagramElement.Orientation.VERTICAL) {
                switch (this._anchorPosition) {
                    case UP:    return (new NodeAnchorPoint<>(this._owner, AnchorPosition.LEFT)) .position();
                    case RIGHT: return (new NodeAnchorPoint<>(this._owner, AnchorPosition.UP))   .position();
                    case DOWN:  return (new NodeAnchorPoint<>(this._owner, AnchorPosition.RIGHT)).position();
                    case LEFT:  return (new NodeAnchorPoint<>(this._owner, AnchorPosition.DOWN)) .position();
                }
            }
        }

        assert false : "ASSERT :: Getting position with anchor point with impossible anchor position.";
        return new Point(0, 0);
    }
}
