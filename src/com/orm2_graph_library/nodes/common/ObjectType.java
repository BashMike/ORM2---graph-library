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
import java.util.stream.Stream;

abstract public class ObjectType extends RoleParticipant {
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
    protected void _initSelf() {
        boolean isVacantNameFound = false;
        int index;
        for (index=1; index<Integer.MAX_VALUE && !isVacantNameFound; index++) {
            String name = this.basicName() + " " + index;
            isVacantNameFound = this._ownerDiagram.getElements(ObjectType.class).noneMatch(e -> e._name.equals(name));
        }

        assert isVacantNameFound : "ERROR :: Failed to find proper index for object type.";
        this._name = this.basicName() + " " + (index-1);
    }

    @Override protected void _finalizeSelf() {}

    // ---------------- attributes ----------------
    // * Name
    public String name() { return this._name; }
    abstract public String basicName();

    public void setName(String name) { this._ownerDiagramActionManager().executeAction(new ObjectTypeNameChangeAction(this._ownerDiagram, this, this._name, name)); }

    // * Signs
    public boolean isPersonal() { return this._isPersonal; }
    public void setIsPersonal(boolean isPersonal) { this._ownerDiagramActionManager().executeAction(new ObjectTypeIsPersonalFlagChangeAction(this._ownerDiagram, this, this._isPersonal, isPersonal)); }

    public boolean isIndependent() { return this._isIndependent; }
    public void setIsIndependent(boolean isIndependent) { this._ownerDiagramActionManager().executeAction(new ObjectTypeIsIndependentFlagChangeAction(this._ownerDiagram, this, this._isIndependent, isIndependent)); }

    // * Geometry
    public void setBorderSize(int borderWidth, int borderHeight) {
        this._borderWidth  = borderWidth;
        this._borderHeight = borderHeight;
    }

    @Override public int borderWidth()  { return this._borderWidth; }
    @Override public int borderHeight() { return this._borderHeight; }

    // ----------------- contract -----------------
    @Override
    public <T extends DiagramElement> Stream<T> getIncidentElements(Class<T> elementType) {
        Stream<T> result = super.getIncidentElements(elementType);

        if (Predicate.class.isAssignableFrom(elementType)) {
            result = Stream.concat(result, this._ownerDiagram.getElements(elementType)
                    .filter(e -> e.isIncidentElement(this)));
        }
        else if (ObjectifiedPredicate.class.isAssignableFrom(elementType)) {
            result = Stream.concat(result, this._ownerDiagram.getElements(elementType)
                    .filter(e -> e.isIncidentElement(this)));
        }

        return result;
    }

    // ================= SUBTYPES =================
    public class ObjectTypeNameChangeAction extends Diagram.DiagramElementAttributeChangeAction {
        protected ObjectTypeNameChangeAction(@NotNull Diagram diagram, @NotNull ObjectType node, @NotNull String oldName, @NotNull String newName) {
            super(diagram, node, oldName, newName);
            this._postValidators.add(new ObjectTypesNameDuplicationPostValidator(diagram, this, node, oldName, newName));
        }

        public ObjectType node() { return (ObjectType)this._diagramElement; }
        public String oldName() { return (String)this._oldAttributeValue; }
        public String newName() { return (String)this._newAttributeValue; }

        @Override public void _execute() { ((ObjectType)this._diagramElement)._name = (String)this._newAttributeValue; }
        @Override public void _undo()    { ((ObjectType)this._diagramElement)._name = (String)this._oldAttributeValue; } }

    public class ObjectTypeIsPersonalFlagChangeAction extends Diagram.DiagramElementAttributeChangeAction {
        private ObjectTypeIsPersonalFlagChangeAction(@NotNull Diagram diagram, @NotNull ObjectType node, boolean oldIsPersonal, boolean newIsPersonal) { super(diagram, node, oldIsPersonal, newIsPersonal); }

        public ObjectType node() { return (ObjectType)this._diagramElement; }
        public boolean oldIsPersonal() { return (Boolean)this._oldAttributeValue; }
        public boolean newIsPersonal() { return (Boolean)this._newAttributeValue; }

        @Override public void _execute() { ((ObjectType)this._diagramElement)._isPersonal = (boolean)this._newAttributeValue; }
        @Override public void _undo()    { ((ObjectType)this._diagramElement)._isPersonal = (boolean)this._oldAttributeValue; }
    }

    public class ObjectTypeIsIndependentFlagChangeAction extends Diagram.DiagramElementAttributeChangeAction {
        private ObjectTypeIsIndependentFlagChangeAction(@NotNull Diagram diagram, @NotNull ObjectType node, boolean oldIsIndependent, boolean newIsIndependent) { super(diagram, node, oldIsIndependent, newIsIndependent); }

        public ObjectType node() { return (ObjectType)this._diagramElement; }
        public boolean oldIsIndependent() { return (Boolean)this._oldAttributeValue; }
        public boolean newIsIndependent() { return (Boolean)this._newAttributeValue; }

        @Override public void _execute() { ((ObjectType)this._diagramElement)._isIndependent = (boolean)this._newAttributeValue; }
        @Override public void _undo()    { ((ObjectType)this._diagramElement)._isIndependent = (boolean)this._oldAttributeValue; }
    }
}