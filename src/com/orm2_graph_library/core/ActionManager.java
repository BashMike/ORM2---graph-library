package com.orm2_graph_library.core;

import java.util.ArrayList;

public class ActionManager {
    // ================ ATTRIBUTES ================
    private ArrayList<Action> _actions         = new ArrayList<>();
    private int               _currActionIndex = -1;

    private boolean           _recordActions   = true;

    // ================ OPERATIONS ================
    // ----------------- contract -----------------
    public boolean canUndo() { return (this._currActionIndex >=  0 && this._currActionIndex < this._actions.size()  ); }
    public boolean canRedo() { return (this._currActionIndex >= -1 && this._currActionIndex < this._actions.size()-1); }

    public void undo() {
        if (!this.canUndo()) {
            throw new RuntimeException("ERROR: No action to which undo operation can be performed.");
        }

        this._actions.get(this._currActionIndex).undo();
        this._currActionIndex--;
    }

    public void redo() {
        if (!this.canRedo()) {
            throw new RuntimeException("ERROR: No action to which redo operation can be performed.");
        }

        this._currActionIndex++;
        this._actions.get(this._currActionIndex).execute();
    }

    public Action addAction(Action action) {
        if (this._recordActions) {
            this._actions.add(action);
            this._actions = new ArrayList<>(this._actions.subList(0, ++this._currActionIndex + 1));

            return action;
        }

        return null;
    }

    public void startRecordingActions() { this._recordActions = true; }
    public void stopRecordingActions()  { this._recordActions = false; }
}
