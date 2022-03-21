package com.orm2_graph_library.action_errors;

import com.orm2_graph_library.core.ActionError;
import com.orm2_graph_library.core.DiagramElement;
import com.orm2_graph_library.core.Node;
import org.jetbrains.annotations.NotNull;

public class DiagramElementSelfConnectedActionError<T extends DiagramElement> extends ActionError {
    // ================ ATTRIBUTES ================
    private final T _diagramElement;

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public DiagramElementSelfConnectedActionError(@NotNull T diagramElement) { this._diagramElement = diagramElement; }

    // ---------------- attributes ----------------
    public T diagramElement() { return this._diagramElement; }

    // ----------------- contract -----------------
    @Override
    public String description() { return "Diagram element \"" + this._diagramElement + "\" is self connected."; }
}
