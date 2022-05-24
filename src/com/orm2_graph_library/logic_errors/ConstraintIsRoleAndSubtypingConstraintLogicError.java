package com.orm2_graph_library.logic_errors;

import com.orm2_graph_library.core.Edge;
import com.orm2_graph_library.core.LogicError;
import com.orm2_graph_library.edges.RoleConstraintRelationEdge;
import com.orm2_graph_library.edges.SubtypingConstraintRelationEdge;
import com.orm2_graph_library.nodes.constraints.Constraint;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConstraintIsRoleAndSubtypingConstraintLogicError extends LogicError {
    final private Constraint                                 _constraint;
    final private ArrayList<RoleConstraintRelationEdge>      _connectedRoleConstraintRelationEdges      = new ArrayList<>();
    final private ArrayList<SubtypingConstraintRelationEdge> _connectedSubtypingConstraintRelationEdges = new ArrayList<>();

    public ConstraintIsRoleAndSubtypingConstraintLogicError(@NotNull Constraint constraint,
                                                            @NotNull ArrayList<Edge> connectedConstraintRelationEdges)
    {
        this._constraint = constraint;
        this._connectedRoleConstraintRelationEdges.addAll(connectedConstraintRelationEdges.stream()
                .filter(e -> e instanceof RoleConstraintRelationEdge)
                .map(RoleConstraintRelationEdge.class::cast)
                .collect(Collectors.toCollection(ArrayList::new)));

        this._connectedSubtypingConstraintRelationEdges.addAll(connectedConstraintRelationEdges.stream()
                .filter(e -> e instanceof SubtypingConstraintRelationEdge)
                .map(SubtypingConstraintRelationEdge.class::cast)
                .collect(Collectors.toCollection(ArrayList::new)));
    }

    public Constraint constraint() { return this._constraint; }

    public Stream<RoleConstraintRelationEdge> connectedRoleConstraintRelationEdges()           { return this._connectedRoleConstraintRelationEdges.stream(); }
    public Stream<SubtypingConstraintRelationEdge> connectedSubtypingConstraintRelationEdges() { return this._connectedSubtypingConstraintRelationEdges.stream(); }

    public String description() {
        String result = "Constraint \"" + this._constraint.getClass() + "\" is a role constraint and subtyping constraint. The constraint connected with ";
        result += this._connectedRoleConstraintRelationEdges.size() + " role constraint relation edges and ";
        result += this._connectedSubtypingConstraintRelationEdges.size() + " subtyping constraint relation edges.";

        return result;
    }
}
