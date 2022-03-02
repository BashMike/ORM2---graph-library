package com.orm2_graph_library.nodes.common;

import com.orm2_graph_library.core.*;
import com.orm2_graph_library.logic_errors.ObjectTypesNameDuplicationLogicError;
import com.orm2_graph_library.nodes.predicates.RoleParticipant;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.stream.Collectors;

public abstract class ObjectType extends MovableNode implements RoleParticipant {
    // ================ ATTRIBUTES ================
    private String _name = "";

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public ObjectType() {}

    // ---------------- connection ----------------
    @Override
    protected void _initSelf(Diagram owner) {
        boolean isVacantNameFound = false;
        int index;
        for (index=1; index<Integer.MAX_VALUE && !isVacantNameFound; index++) {
            String name = this.basicName() + " " + index;
            isVacantNameFound = !owner.getElements(ObjectType.class).anyMatch(e -> e._name.equals(name));
        }

        assert isVacantNameFound : "ERROR :: Failed to find proper index for object type.";
        this._name = this.basicName() + " " + (index-1);
    }

    // ---------------- attributes ----------------
    public String name() { return this._name; }
    public void setName(String name) {
        this._owner._actionManager().executeAction(new RenameObjectTypeAction(this._owner, this, this._name, name));
    }

    public abstract String basicName();

    // ================= SUBTYPES =================
    private class RenameObjectTypeAction extends Action {
        private final ObjectType _node;
        private final String     _oldName;
        private final String     _newName;

        public RenameObjectTypeAction(Diagram diagram, @NotNull ObjectType node, String oldName, String newName) {
            super(diagram);

            this._node    = node;
            this._oldName = oldName;
            this._newName = newName;
        }

        @Override
        public void _execute() { this._node._name = this._newName; }
        @Override
        public void _undo() { this._node._name = this._oldName; }
        @Override
        public void _validate() {
            ArrayList<ObjectType> sameNameObjectTypes = this._diagram.getElements(ObjectType.class).filter(e -> e._name.equals(this._newName)).collect(Collectors.toCollection(ArrayList::new));

            ObjectTypesNameDuplicationLogicError sameNameLogicError = null;
            for (LogicError logicError : this._diagram.logicErrors()) {
                if (logicError instanceof ObjectTypesNameDuplicationLogicError && ((ObjectTypesNameDuplicationLogicError)logicError).name().equals(this._newName)) {
                    sameNameLogicError = (ObjectTypesNameDuplicationLogicError)logicError;
                    break;
                }
            }

            if (sameNameObjectTypes.size() > 1) {
                if (sameNameLogicError == null) {
                    this._addLogicErrorToDiagram(new ObjectTypesNameDuplicationLogicError(this._newName, sameNameObjectTypes));
                }
                else {
                    sameNameLogicError.update(sameNameObjectTypes);
                }
            }
            else if (sameNameLogicError != null) {
                this._removeLogicErrorFromDiagram(sameNameLogicError);
            }
        }
    }
}