package com.orm2_graph_library.core;

import org.jetbrains.annotations.NotNull;

public interface ActionListener {
    <T extends Action> void handleAction(@NotNull T action);
    <T extends Action> void handleUndoOfAction(@NotNull T action);
}
