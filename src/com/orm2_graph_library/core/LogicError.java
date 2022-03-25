package com.orm2_graph_library.core;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Stream;

public abstract class LogicError extends Error {
    // ================ ATTRIBUTES ================
    protected final ArrayList<DiagramElement> _errorParticipants = new ArrayList<>();

    // ================ OPERATIONS ================
    // ----------------- contract -----------------
    public boolean isErrorParticipant(DiagramElement diagramElement) { return this._errorParticipants.contains(diagramElement); }

    public<T extends DiagramElement> Stream<T> errorParticipants(Class<T> elementType) {
        return (Stream<T>)this._errorParticipants.stream().filter(e -> elementType.isAssignableFrom(e.getClass()));
    }

    // ---------------- comparison ----------------
    @Override
    public boolean equals(@NotNull Object other) {
        boolean isSameType = (this.getClass().isAssignableFrom(other.getClass()) && other.getClass().isAssignableFrom(this.getClass()));
        boolean areSameErrorParticipants = (new HashSet<>(this._errorParticipants)).equals(new HashSet<>(((LogicError)other)._errorParticipants));

        return isSameType && areSameErrorParticipants;
    }
}
