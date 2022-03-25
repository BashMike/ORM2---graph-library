package com.orm2_graph_library.nodes.common;

import com.orm2_graph_library.core.*;
import com.orm2_graph_library.core.Action;
import com.orm2_graph_library.nodes.predicates.ObjectifiedPredicate;
import com.orm2_graph_library.nodes.predicates.Predicate;
import com.orm2_graph_library.nodes.predicates.RoleParticipant;
import com.orm2_graph_library.nodes_shapes.EllipseShape;
import com.orm2_graph_library.post_validators.ObjectTypesNameDuplicationPostValidator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.stream.Collectors;

public abstract class ObjectType extends RoleParticipant {
    // ================ ATTRIBUTES ================
    private String _name           = "";
    private boolean _isPersonal    = false;
    private boolean _isIndependent = false;

    protected int _borderWidth  = -1;
    protected int _borderHeight = -1;

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
    public void setName(String name) { this._ownerDiagram._actionManager().executeAction(new ObjectTypeNameChangeAction(this._ownerDiagram, this, this._name, name)); }

    public abstract String basicName();

    // * Signs
    public boolean isPersonal() { return this._isPersonal; }
    public void setIsPersonal(boolean isPersonal) { this._ownerDiagram._actionManager().executeAction(new ObjectTypeIsPersonalFlagChangeAction(this._ownerDiagram, this, this._isPersonal, isPersonal)); }

    public boolean isIndependent() { return this._isIndependent; }
    public void setIsIndependent(boolean isIndependent) { this._ownerDiagram._actionManager().executeAction(new ObjectTypeIsIndependentFlagChangeAction(this._ownerDiagram, this, this._isIndependent, isIndependent)); }

    public void setBorderSize(int borderWidth, int borderHeight) {
        this._borderWidth  = borderWidth;
        this._borderHeight = borderHeight;
    }

    @Override public int borderWidth()  { return this._borderWidth; }
    @Override public int borderHeight() { return this._borderHeight; }

    // ----------------- contract -----------------
    @Override
    public <T extends DiagramElement> ArrayList<T> getIncidentElements(Class<T> elementType) {
        ArrayList<T> result = super.getIncidentElements(elementType);

        if (Predicate.class.isAssignableFrom(elementType)) {
            result.addAll(this._ownerDiagram.getElements(elementType)
                    .filter(e -> e.isIncidentElement(this))
                    .collect(Collectors.toCollection(ArrayList::new)));
        }
        else if (ObjectifiedPredicate.class.isAssignableFrom(elementType)) {
            result.addAll(this._ownerDiagram.getElements(elementType)
                    .filter(e -> e.isIncidentElement(this))
                    .collect(Collectors.toCollection(ArrayList::new)));
        }

        return result;
    }

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