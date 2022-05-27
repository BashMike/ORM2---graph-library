package com.orm2_graph_library.logic_errors;

import com.orm2_graph_library.core.LogicError;
import com.orm2_graph_library.nodes.constraints.Constraint;
import com.orm2_graph_library.nodes.predicates.RolesSequence;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.stream.Stream;

public class RoleConstraintsConflictLogicError extends LogicError {
    final private ArrayList<Constraint> _conflictedConstraints = new ArrayList<>();
    final ArrayList<RolesSequence>      _rolesSequences        = new ArrayList<>();

    public RoleConstraintsConflictLogicError(@NotNull ArrayList<Constraint> conflictedConstraints, @NotNull ArrayList<RolesSequence> rolesSequences) {
        super();

        if (conflictedConstraints.size() <= 1) {
            throw new RuntimeException("ERROR :: two or more constraints can be conflicted to each other.");
        }
        if (rolesSequences.size() != 2) {
            throw new RuntimeException("ERROR :: role constraints conflict can be only between 2 roles sequences which are connected with those constraints.");
        }

        this._conflictedConstraints.addAll(conflictedConstraints);
        this._rolesSequences.addAll(rolesSequences);

        this._errorParticipants.addAll(this._conflictedConstraints);
        this._errorParticipants.addAll(this._rolesSequences);
    }

    public Stream<Constraint> conflictedConstraints() { return this._conflictedConstraints.stream(); }
    public Stream<RolesSequence> rolesSequences() { return this._rolesSequences.stream(); }

    @Override public String description() {
        String result = "Role constraints between roleSequence@" + this._rolesSequences.get(0) + " and rolesSequence@" + this._rolesSequences.get(1) + " has conflicts: " + this._conflictedConstraints.get(0).getClass().getSimpleName() + "@" + this._conflictedConstraints.get(0);
        for (int i=1; i<this._conflictedConstraints.size(); i++) { result += ", " + this._conflictedConstraints.get(i).getClass().getSimpleName() + "@" + this._conflictedConstraints.get(i); }

        return result;
    }

    @Override public int hashCode() { return this._errorParticipants.size(); }
}
