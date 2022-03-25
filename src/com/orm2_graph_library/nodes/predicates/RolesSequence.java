package com.orm2_graph_library.nodes.predicates;

import com.orm2_graph_library.anchor_points.RolesSequenceAnchorPoint;
import com.orm2_graph_library.core.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RolesSequence extends DiagramElement {
    // ================ ATTRIBUTES ================
    private ArrayList<Role> _roles = new ArrayList<>();

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    RolesSequence(ArrayList<Role> roles) {
        if (roles.isEmpty()) {
            throw new RuntimeException("ERROR :: Roles sequence must have at least one role in it.");
        }

        Predicate ownerPredicate = roles.get(0).ownerPredicate();
        for (Role role : roles) {
            if (role.ownerPredicate() != ownerPredicate) {
                throw new RuntimeException("ERROR :: Roles sequence must contain roles with the same owner.");
            }
        }

        this._roles.addAll(roles);
    }

    @Override
    protected void _initSelf(Diagram owner) {}

    // ---------------- connection ----------------
    public Predicate ownerPredicate() { return this._roles.get(0).ownerPredicate(); }

    // ---------------- attributes ----------------
    public ArrayList<Role> roles() { return new ArrayList<>(this._roles); }

    public AnchorPoint<RolesSequence> anchorPoint() { return new RolesSequenceAnchorPoint(this); }

    @Override
    public GeometryApproximation geometryApproximation() { return null; }

    // ----------------- contract -----------------
    @Override
    public <T extends DiagramElement> ArrayList<T> getIncidentElements(Class<T> elementType) {
        ArrayList<T> result = new ArrayList<>();

        if (Edge.class.isAssignableFrom(elementType)) {
            result = this._ownerDiagram.getElements(elementType)
                    .filter(e -> ((Edge)e).begin() == this || ((Edge)e).end() == this)
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        else if (Node.class.isAssignableFrom(elementType)) {
            for (Edge edge : this._ownerDiagram.getElements(Edge.class).collect(Collectors.toCollection(ArrayList::new))) {
                if (edge.begin() == this && elementType.isAssignableFrom(edge.endAnchorPoint().owner().getClass())) {
                    result.add((T)edge.end());
                }
                if (edge.endAnchorPoint().owner() == this && elementType.isAssignableFrom(edge.beginAnchorPoint().getClass())) {
                    result.add((T)edge.begin());
                }
            }
        }

        return result;
    }

    // ---------------- comparison ----------------
    @Override
    public boolean equals(@NotNull Object other) {
        if (other instanceof RolesSequence) {
            Set<Role> roles0 = new HashSet<>(this._roles);
            Set<Role> roles1 = new HashSet<>(((RolesSequence)other)._roles);

            return roles0.equals(roles1);
        }
        else {
            return false;
        }
    }
}
