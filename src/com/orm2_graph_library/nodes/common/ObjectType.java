package com.orm2_graph_library.nodes.common;

import com.orm2_graph_library.core.*;
import com.orm2_graph_library.core.Action;
import com.orm2_graph_library.nodes.predicates.RoleParticipant;
import com.orm2_graph_library.nodes_shapes.EllipseShape;
import com.orm2_graph_library.post_validators.ObjectTypesNameDuplicationPostValidator;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.font.FontRenderContext;

public abstract class ObjectType extends RoleParticipant {
    // ================== STATIC ==================
    static private int _verticalEmptyGap   = 10;
    static private int _horizontalEmptyGap = 10;

    static public int verticalEmptyGap()   { return ObjectType._verticalEmptyGap; }
    static public int horizontalEmptyGap() { return ObjectType._horizontalEmptyGap; }

    static public void setVerticalEmptyGap(int verticalEmptyGap)     { ObjectType._verticalEmptyGap   = verticalEmptyGap; }
    static public void setHorizontalEmptyGap(int horizontalEmptyGap) { ObjectType._horizontalEmptyGap = horizontalEmptyGap; }

    // ================ ATTRIBUTES ================
    private String _name = "";

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

    // TODO - @check :: Check that getting font size like that is proper.
    @Override public int borderWidth()  { return ObjectType._horizontalEmptyGap * 2 + 80; }
    @Override public int borderHeight() { return ObjectType._verticalEmptyGap   * 2 + 30; }

    // ================= SUBTYPES =================
    private class RenameObjectTypeAction extends Action {
        private final ObjectType _node;
        private final String     _oldName;
        private final String     _newName;

        public RenameObjectTypeAction(@NotNull Diagram diagram, @NotNull ObjectType node, @NotNull String oldName, @NotNull String newName) {
            super(diagram);

            this._node    = node;
            this._oldName = oldName;
            this._newName = newName;

            this._postValidators.add(new ObjectTypesNameDuplicationPostValidator(diagram, this, node, oldName, newName));
        }

        @Override
        public void _execute() { this._node._name = this._newName; }
        @Override
        public void _undo() { this._node._name = this._oldName; }
    }
}