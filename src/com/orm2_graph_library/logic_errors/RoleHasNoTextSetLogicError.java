package com.orm2_graph_library.logic_errors;

import com.orm2_graph_library.core.LogicError;
import com.orm2_graph_library.nodes.predicates.Predicate;
import com.orm2_graph_library.nodes.predicates.Role;
import org.jetbrains.annotations.NotNull;

public class RoleHasNoTextSetLogicError extends LogicError {
    final private Predicate _predicate;
    final private Role      _role;

    public RoleHasNoTextSetLogicError(@NotNull Role role) {
        super();
        this._predicate = role.ownerPredicate();
        this._role      = role;

        this._errorParticipants.add(this._predicate);
        this._errorParticipants.add(this._role);
    }

    public Predicate predicate() { return this._predicate; }
    public Role role()           { return this._role; }

    @Override public String description() { return ("Role " + this._role + " of predicate " + this._predicate + " has no text set."); }

    @Override public int hashCode() { return this._errorParticipants.size(); }
}
