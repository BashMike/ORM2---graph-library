package com.orm2_graph_library.nodes.common;

import com.orm2_graph_library.core.*;
import com.orm2_graph_library.core.Action;
import com.orm2_graph_library.nodes.predicates.RoleParticipant;
import com.orm2_graph_library.nodes_shapes.EllipseShape;
import com.orm2_graph_library.post_validators.ObjectTypesNameDuplicationPostValidator;
import org.jetbrains.annotations.NotNull;

public abstract class ObjectType extends RoleParticipant {
    // ================== STATIC ==================
    static private int _verticalEmptyGap   = 10;
    static private int _horizontalEmptyGap = 10;

    static public int verticalEmptyGap()   { return ObjectType._verticalEmptyGap; }
    static public int horizontalEmptyGap() { return ObjectType._horizontalEmptyGap; }

    static public void setVerticalEmptyGap(int verticalEmptyGap)     { ObjectType._verticalEmptyGap   = verticalEmptyGap; }
    static public void setHorizontalEmptyGap(int horizontalEmptyGap) { ObjectType._horizontalEmptyGap = horizontalEmptyGap; }

    // ================ ATTRIBUTES ================
    private String _name           = "";
    private boolean _isPersonal    = false;
    private boolean _isIndependent = false;

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public ObjectType() { this._shape = new EllipseShape(); }

    // ---------------- connection ----------------
    @Override
    protected void _initSelf(Diagram owner) {
        boolean isVacantNameFound = false;
        int index;
        for (index=1; index<Integer.MAX_VALUE && !isVacantNameFound; index++) {
            String name = this.basicName() + " " + index;
            isVacantNameFound = owner.getElements(ObjectType.class).noneMatch(e -> e._name.equals(name));
        }

        assert isVacantNameFound : "ERROR :: Failed to find proper index for object type.";
        this._name = this.basicName() + " " + (index-1);
    }

    // ---------------- attributes ----------------
    // * Name
    public String name() { return this._name; }
    public void setName(String name) { this._owner._actionManager().executeAction(new ObjectTypeNameChangeAction(this._owner, this, this._name, name)); }

    public abstract String basicName();

    // * Signs
    public boolean isPersonal() { return this._isPersonal; }
    public void setIsPersonal(boolean isPersonal) { this._owner._actionManager().executeAction(new ObjectTypeIsPersonalFlagChangeAction(this._owner, this, this._isPersonal, isPersonal)); }

    public boolean isIndependent() { return this._isIndependent; }
    public void setIsIndependent(boolean isIndependent) { this._owner._actionManager().executeAction(new ObjectTypeIsIndependentFlagChangeAction(this._owner, this, this._isIndependent, isIndependent)); }

    // TODO - @check :: Check that getting font size like that is proper.
    @Override public int borderWidth()  { return ObjectType._horizontalEmptyGap * 2 + 80; }
    @Override public int borderHeight() { return ObjectType._verticalEmptyGap   * 2 + 30; }

    // ================= SUBTYPES =================
    protected abstract class ObjectTypeAttributeChangeAction extends Action {
        protected final ObjectType _node;
        protected final Object     _oldAttributeValue;
        protected final Object     _newAttributeValue;

        public ObjectTypeAttributeChangeAction(@NotNull Diagram diagram, @NotNull ObjectType node, @NotNull Object oldAttributeValue, @NotNull Object newAttributeValue) {
            super(diagram);

            this._node    = node;
            this._oldAttributeValue = oldAttributeValue;
            this._newAttributeValue = newAttributeValue;

            // Check if object type's old name is the same as new name
            if (this._oldAttributeValue.equals(this._newAttributeValue)) { this._becomeInvalid(); }
        }
    }

    private class ObjectTypeNameChangeAction extends ObjectTypeAttributeChangeAction {
        public ObjectTypeNameChangeAction(@NotNull Diagram diagram, @NotNull ObjectType node, @NotNull String oldName, @NotNull String newName) {
            super(diagram, node, oldName, newName);
            this._postValidators.add(new ObjectTypesNameDuplicationPostValidator(diagram, this, node, oldName, newName));
        }

        @Override
        public void _execute() { this._node._name = (String)this._newAttributeValue; }
        @Override
        public void _undo() { this._node._name = (String)this._oldAttributeValue; }
    }

    private class ObjectTypeIsPersonalFlagChangeAction extends ObjectTypeAttributeChangeAction {
        public ObjectTypeIsPersonalFlagChangeAction(@NotNull Diagram diagram, @NotNull ObjectType node, boolean oldIsPersonal, boolean newIsPersonal) { super(diagram, node, oldIsPersonal, newIsPersonal); }

        @Override
        public void _execute() { this._node._isPersonal = (boolean)this._newAttributeValue; }
        @Override
        public void _undo() { this._node._isPersonal = (boolean)this._oldAttributeValue; }
    }

    private class ObjectTypeIsIndependentFlagChangeAction extends ObjectTypeAttributeChangeAction {
        public ObjectTypeIsIndependentFlagChangeAction(@NotNull Diagram diagram, @NotNull ObjectType node, boolean oldIsIndependent, boolean newIsIndependent) { super(diagram, node, oldIsIndependent, newIsIndependent); }

        @Override
        public void _execute() { this._node._isIndependent = (boolean)this._newAttributeValue; }
        @Override
        public void _undo() { this._node._isIndependent = (boolean)this._oldAttributeValue; }
    }
}