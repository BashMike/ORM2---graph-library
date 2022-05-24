package com.orm2_graph_library.core;

import org.jetbrains.annotations.NotNull;

public interface ActionListener {
    // Handle actions
    void handleAddEntityTypeAction(@NotNull Diagram.AddEntityTypeAction action);
    void handleAddValueTypeAction(@NotNull Diagram.AddValueTypeAction action);
    void handleAddConstraintAction(@NotNull Diagram.AddConstraintAction action);
    void handleAddPredicateAction(@NotNull Diagram.AddPredicateAction action);
    void handleAddObjectifiedPredicateAction(@NotNull Diagram.AddObjectifiedPredicateAction action);

    void handleRemoveEntityTypeAction(@NotNull Diagram.RemoveEntityTypeAction action);
    void handleRemoveValueTypeAction(@NotNull Diagram.RemoveValueTypeAction action);
    void handleRemoveConstraintAction(@NotNull Diagram.RemoveConstraintAction action);
    void handleRemovePredicateAction(@NotNull Diagram.RemovePredicateAction action);
    void handleRemoveObjectifiedPredicateAction(@NotNull Diagram.RemoveObjectifiedPredicateAction action);

    void handleConnectBySubtypingRelationAction(@NotNull Diagram.ConnectBySubtypingRelationAction action);
    void handleConnectByRoleRelationAction(@NotNull Diagram.ConnectByRoleRelationAction action);
    void handleConnectBySubtypingConstraintRelationAction(@NotNull Diagram.ConnectBySubtypingConstraintRelationAction action);
    void handleConnectByRoleConstraintRelationAction(@NotNull Diagram.ConnectByRoleConstraintRelationAction action);

    void handleDisconnectSubtypingRelationAction(@NotNull Diagram.DisconnectSubtypingRelationAction action);
    void handleDisconnectRoleRelationAction(@NotNull Diagram.DisconnectRoleRelationAction action);
    void handleDisconnectSubtypingConstraintRelationAction(@NotNull Diagram.DisconnectSubtypingConstraintRelationAction action);
    void handleDisconnectRoleConstraintRelationAction(@NotNull Diagram.DisconnectRoleConstraintRelationAction action);

    // Handle UNDO of actions
    void handleUndoAddEntityTypeAction(@NotNull Diagram.AddEntityTypeAction action);
    void handleUndoAddValueTypeAction(@NotNull Diagram.AddValueTypeAction action);
    void handleUndoAddConstraintAction(@NotNull Diagram.AddConstraintAction action);
    void handleUndoAddPredicateAction(@NotNull Diagram.AddPredicateAction action);
    void handleUndoAddObjectifiedPredicateAction(@NotNull Diagram.AddObjectifiedPredicateAction action);

    void handleUndoRemoveEntityTypeAction(@NotNull Diagram.RemoveEntityTypeAction action);
    void handleUndoRemoveValueTypeAction(@NotNull Diagram.RemoveValueTypeAction action);
    void handleUndoRemoveConstraintAction(@NotNull Diagram.RemoveConstraintAction action);
    void handleUndoRemovePredicateAction(@NotNull Diagram.RemovePredicateAction action);
    void handleUndoRemoveObjectifiedPredicateAction(@NotNull Diagram.RemoveObjectifiedPredicateAction action);

    void handleUndoConnectBySubtypingRelationAction(@NotNull Diagram.ConnectBySubtypingRelationAction action);
    void handleUndoConnectByRoleRelationAction(@NotNull Diagram.ConnectByRoleRelationAction action);
    void handleUndoConnectBySubtypingConstraintRelationAction(@NotNull Diagram.ConnectBySubtypingConstraintRelationAction action);
    void handleUndoConnectByRoleConstraintRelationAction(@NotNull Diagram.ConnectByRoleConstraintRelationAction action);

    void handleUndoDisconnectSubtypingRelationAction(@NotNull Diagram.DisconnectSubtypingRelationAction action);
    void handleUndoDisconnectRoleRelationAction(@NotNull Diagram.DisconnectRoleRelationAction action);
    void handleUndoDisconnectSubtypingConstraintRelationAction(@NotNull Diagram.DisconnectSubtypingConstraintRelationAction action);
    void handleUndoDisconnectRoleConstraintRelationAction(@NotNull Diagram.DisconnectRoleConstraintRelationAction action);
}
