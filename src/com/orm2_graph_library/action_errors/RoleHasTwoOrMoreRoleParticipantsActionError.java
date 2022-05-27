package com.orm2_graph_library.action_errors;

import com.orm2_graph_library.core.ActionError;
import com.orm2_graph_library.nodes.predicates.Role;
import org.jetbrains.annotations.NotNull;

public class RoleHasTwoOrMoreRoleParticipantsActionError extends ActionError {
    final private Role _role;

    public RoleHasTwoOrMoreRoleParticipantsActionError(@NotNull Role role) {
        this._role = role;
    }

    public Role role() { return this._role; }

    public String description() { return "Role \"" + this._role + "\" is connected with some role relation."; }

    @Override
    public boolean equals(Object other) {
        if (other instanceof RoleHasTwoOrMoreRoleParticipantsActionError otherConverted) {
            return (this._role == otherConverted._role);
        }
        else {
            return false;
        }
    }

    @Override
    public int hashCode() { return this._role.hashCode(); }
}
