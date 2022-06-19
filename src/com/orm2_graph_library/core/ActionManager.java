package com.orm2_graph_library.core;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ActionManager {
    // ================ ATTRIBUTES ================
    final private Diagram _ownerDiagram;

    private ArrayList<Action> _actions           = new ArrayList<>();
    private int               _currActionIndex   = -1;

    private boolean           _recordActionsFlag = true;

    ArrayList<ActionListener> _actionListeners   = new ArrayList<>();

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    ActionManager(@NotNull Diagram diagram) {
        if (diagram._actionManager != null) {
            throw new RuntimeException("ERROR :: attempt to set action manager to diagram that already has an action manager.");
        }

        this._ownerDiagram = diagram;
    }

    ActionManager _copyWithoutListeners() {
        ActionManager result = new ActionManager(this._ownerDiagram);
        result._actions.addAll(this._actions);
        result._currActionIndex = this._currActionIndex;
        result._recordActionsFlag   = this._recordActionsFlag;

        return result;
    }

    // ----------------- contract -----------------
    boolean canUndo() { return (this._currActionIndex >=  0 && this._currActionIndex < this._actions.size()  ); }
    boolean canRedo() { return (this._currActionIndex >= -1 && this._currActionIndex < this._actions.size()-1); }

    void undo() {
        if (!this.canUndo()) {
            throw new RuntimeException("ERROR: No action to which undo operation can be performed.");
        }

        this._actions.get(this._currActionIndex).undo();

        this._fireUndoActionExecutedEvent(this._actions.get(this._currActionIndex));

        this._currActionIndex--;
    }

    void redo() {
        if (!this.canRedo()) {
            throw new RuntimeException("ERROR: No action to which redo operation can be performed.");
        }

        this._currActionIndex++;

        this._actions.get(this._currActionIndex).execute();

        this._fireActionExecutedEvent(this._actions.get(this._currActionIndex));
    }

    public <T extends Action> void executeAction(T action) {
        if (action._isValid) {
            action.execute();

            this._fireActionExecutedEvent(action);

            if (this._recordActionsFlag) {
                this._actions.add(action);
                this._actions = new ArrayList<>(this._actions.subList(0, ++this._currActionIndex + 1));
            }
        }
    }

    void _popLastAction() {
        if (!this._actions.isEmpty()) {
            this._actions.remove(this._actions.size() - 1);
            this._currActionIndex--;
        }
    }

    void _clearActionHistory() {
        this._actions         = new ArrayList<>();
        this._currActionIndex = -1;

        this._recordActionsFlag   = true;

        this._actionListeners = new ArrayList<>();
    }

    void startRecordingActions() { this._recordActionsFlag = true; }
    void stopRecordingActions()  { this._recordActionsFlag = false; }

    private void _fireActionExecutedEvent(@NotNull Action action) {
        if (action instanceof Diagram.AddEntityTypeAction)                { for (var actionListener : this._actionListeners) { actionListener.handleAction((Diagram.AddEntityTypeAction)action); } }
        else if (action instanceof Diagram.AddValueTypeAction)            { for (var actionListener : this._actionListeners) { actionListener.handleAction((Diagram.AddValueTypeAction)action); } }
        else if (action instanceof Diagram.AddConstraintAction)           { for (var actionListener : this._actionListeners) { actionListener.handleAction((Diagram.AddConstraintAction)action); } }
        else if (action instanceof Diagram.AddPredicateAction)            { for (var actionListener : this._actionListeners) { actionListener.handleAction((Diagram.AddPredicateAction)action); } }
        else if (action instanceof Diagram.AddObjectifiedPredicateAction) { for (var actionListener : this._actionListeners) { actionListener.handleAction((Diagram.AddObjectifiedPredicateAction)action); } }

        else if (action instanceof Diagram.RemoveEntityTypeAction)           { for (var actionListener : this._actionListeners) { actionListener.handleAction((Diagram.RemoveEntityTypeAction)action); } }
        else if (action instanceof Diagram.RemoveValueTypeAction)            { for (var actionListener : this._actionListeners) { actionListener.handleAction((Diagram.RemoveValueTypeAction)action); } }
        else if (action instanceof Diagram.RemoveConstraintAction)           { for (var actionListener : this._actionListeners) { actionListener.handleAction((Diagram.RemoveConstraintAction)action); } }
        else if (action instanceof Diagram.RemovePredicateAction)            { for (var actionListener : this._actionListeners) { actionListener.handleAction((Diagram.RemovePredicateAction)action); } }
        else if (action instanceof Diagram.RemoveObjectifiedPredicateAction) { for (var actionListener : this._actionListeners) { actionListener.handleAction((Diagram.RemoveObjectifiedPredicateAction)action); } }

        else if (action instanceof Diagram.ConnectBySubtypingRelationAction)           { for (var actionListener : this._actionListeners) { actionListener.handleAction((Diagram.ConnectBySubtypingRelationAction)action); } }
        else if (action instanceof Diagram.ConnectByRoleRelationAction)                { for (var actionListener : this._actionListeners) { actionListener.handleAction((Diagram.ConnectByRoleRelationAction)action); } }
        else if (action instanceof Diagram.ConnectBySubtypingConstraintRelationAction) { for (var actionListener : this._actionListeners) { actionListener.handleAction((Diagram.ConnectBySubtypingConstraintRelationAction)action); } }
        else if (action instanceof Diagram.ConnectByRoleConstraintRelationAction)      { for (var actionListener : this._actionListeners) { actionListener.handleAction((Diagram.ConnectByRoleConstraintRelationAction)action); } }

        else if (action instanceof Diagram.DisconnectSubtypingRelationAction)           { for (var actionListener : this._actionListeners) { actionListener.handleAction((Diagram.DisconnectSubtypingRelationAction)action); } }
        else if (action instanceof Diagram.DisconnectRoleRelationAction)                { for (var actionListener : this._actionListeners) { actionListener.handleAction((Diagram.DisconnectRoleRelationAction)action); } }
        else if (action instanceof Diagram.DisconnectSubtypingConstraintRelationAction) { for (var actionListener : this._actionListeners) { actionListener.handleAction((Diagram.DisconnectSubtypingConstraintRelationAction)action); } }
        else if (action instanceof Diagram.DisconnectRoleConstraintRelationAction)      { for (var actionListener : this._actionListeners) { actionListener.handleAction((Diagram.DisconnectRoleConstraintRelationAction)action); } }
    }

    private void _fireUndoActionExecutedEvent(@NotNull Action action) {
        if (action instanceof Diagram.AddEntityTypeAction)                { for (var actionListener : this._actionListeners) { actionListener.handleUndoOfAction((Diagram.AddEntityTypeAction)action); } }
        else if (action instanceof Diagram.AddValueTypeAction)            { for (var actionListener : this._actionListeners) { actionListener.handleUndoOfAction((Diagram.AddValueTypeAction)action); } }
        else if (action instanceof Diagram.AddConstraintAction)           { for (var actionListener : this._actionListeners) { actionListener.handleUndoOfAction((Diagram.AddConstraintAction)action); } }
        else if (action instanceof Diagram.AddPredicateAction)            { for (var actionListener : this._actionListeners) { actionListener.handleUndoOfAction((Diagram.AddPredicateAction)action); } }
        else if (action instanceof Diagram.AddObjectifiedPredicateAction) { for (var actionListener : this._actionListeners) { actionListener.handleUndoOfAction((Diagram.AddObjectifiedPredicateAction)action); } }

        else if (action instanceof Diagram.RemoveEntityTypeAction)           { for (var actionListener : this._actionListeners) { actionListener.handleUndoOfAction((Diagram.RemoveEntityTypeAction)action); } }
        else if (action instanceof Diagram.RemoveValueTypeAction)            { for (var actionListener : this._actionListeners) { actionListener.handleUndoOfAction((Diagram.RemoveValueTypeAction)action); } }
        else if (action instanceof Diagram.RemoveConstraintAction)           { for (var actionListener : this._actionListeners) { actionListener.handleUndoOfAction((Diagram.RemoveConstraintAction)action); } }
        else if (action instanceof Diagram.RemovePredicateAction)            { for (var actionListener : this._actionListeners) { actionListener.handleUndoOfAction((Diagram.RemovePredicateAction)action); } }
        else if (action instanceof Diagram.RemoveObjectifiedPredicateAction) { for (var actionListener : this._actionListeners) { actionListener.handleUndoOfAction((Diagram.RemoveObjectifiedPredicateAction)action); } }

        else if (action instanceof Diagram.ConnectBySubtypingRelationAction)           { for (var actionListener : this._actionListeners) { actionListener.handleUndoOfAction((Diagram.ConnectBySubtypingRelationAction)action); } }
        else if (action instanceof Diagram.ConnectByRoleRelationAction)                { for (var actionListener : this._actionListeners) { actionListener.handleUndoOfAction((Diagram.ConnectByRoleRelationAction)action); } }
        else if (action instanceof Diagram.ConnectBySubtypingConstraintRelationAction) { for (var actionListener : this._actionListeners) { actionListener.handleUndoOfAction((Diagram.ConnectBySubtypingConstraintRelationAction)action); } }
        else if (action instanceof Diagram.ConnectByRoleConstraintRelationAction)      { for (var actionListener : this._actionListeners) { actionListener.handleUndoOfAction((Diagram.ConnectByRoleConstraintRelationAction)action); } }

        else if (action instanceof Diagram.DisconnectSubtypingRelationAction)           { for (var actionListener : this._actionListeners) { actionListener.handleUndoOfAction((Diagram.DisconnectSubtypingRelationAction)action); } }
        else if (action instanceof Diagram.DisconnectRoleRelationAction)                { for (var actionListener : this._actionListeners) { actionListener.handleUndoOfAction((Diagram.DisconnectRoleRelationAction)action); } }
        else if (action instanceof Diagram.DisconnectSubtypingConstraintRelationAction) { for (var actionListener : this._actionListeners) { actionListener.handleUndoOfAction((Diagram.DisconnectSubtypingConstraintRelationAction)action); } }
        else if (action instanceof Diagram.DisconnectRoleConstraintRelationAction)      { for (var actionListener : this._actionListeners) { actionListener.handleUndoOfAction((Diagram.DisconnectRoleConstraintRelationAction)action); } }
    }
}
