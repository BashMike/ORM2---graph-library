package com.orm2_graph_library.nodes.predicates;

import com.orm2_graph_library.anchor_points.RolesSequenceAnchorPoint;
import com.orm2_graph_library.core.*;
import com.orm2_graph_library.nodes.constraints.UniquenessConstraint;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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

    @Override protected void _initSelf() {}
    @Override protected void _finalizeSelf() {}

    // ---------------- connection ----------------
    public Predicate ownerPredicate() { return this._roles.get(0).ownerPredicate(); }

    // ---------------- attributes ----------------
    // * Roles
    public Stream<Role> roles() { return this._roles.stream(); }

    // * Uniqueness
    public boolean isUnique() {
        if (this._roles.get(0).ownerPredicate().isRolesSequenceUnique(this)) {
            return true;
        }
        else {
            return this.hasIncidentElements(UniquenessConstraint.class);
        }
    }

    // * Anchor points
    public AnchorPoint<RolesSequence> anchorPoint() { return new RolesSequenceAnchorPoint(this); }

    @Override public Stream<AnchorPoint> anchorPoints() { return Stream.of(this.anchorPoint()); }

    // * Geometry
    @Override
    public GeometryApproximation geometryApproximation() { return null; }

    // ----------------- contract -----------------
    @Override
    public <T extends DiagramElement> Stream<T> getIncidentElements(Class<T> elementType) {
        Stream<T> result = Stream.of();

        if (Edge.class.isAssignableFrom(elementType) || elementType == DiagramElement.class) {
            if (elementType != DiagramElement.class) { result = this._ownerDiagram.getElements(elementType).filter(e -> ((Edge)e).begin() == this || ((Edge)e).end() == this); }
            else                                     { result = (Stream<T>)this._ownerDiagram.getElements(Edge.class).filter(e -> ((Edge)e).begin() == this || ((Edge)e).end() == this); }
        }

        if (Node.class.isAssignableFrom(elementType) || elementType == DiagramElement.class) {
            for (Edge edge : this._ownerDiagram.getElements(Edge.class).collect(Collectors.toCollection(ArrayList::new))) {
                if (edge.begin() == this && elementType.isAssignableFrom(edge.end().getClass())) { result = Stream.concat(result, Stream.of((T)edge.end())); }
                if (edge.end() == this && elementType.isAssignableFrom(edge.begin().getClass())) { result = Stream.concat(result, Stream.of((T)edge.begin())); }
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

    @Override
    public int hashCode() {
        int roleIndexesSum = 0;
        for (Role role : this._roles) { roleIndexesSum += role.indexInPredicate(); }

        return roleIndexesSum;
    }

    // ---------------- additional ----------------
    @Override
    public String toString() {
        String result = "predicate@" + this.ownerPredicate().hashCode() + ".roles{" + this._roles.get(0).indexInPredicate();
        for (int i=1; i<this._roles.size(); i++) { result += ", " + this._roles.get(i).indexInPredicate(); }
        result += "}";

        return result;
    }
}
