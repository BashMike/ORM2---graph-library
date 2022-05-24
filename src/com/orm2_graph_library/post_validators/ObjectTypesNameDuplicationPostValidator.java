package com.orm2_graph_library.post_validators;

import com.orm2_graph_library.core.Action;
import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.LogicError;
import com.orm2_graph_library.core.PostValidator;
import com.orm2_graph_library.logic_errors.ObjectTypesNameDuplicationLogicError;
import com.orm2_graph_library.nodes.common.ObjectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ObjectTypesNameDuplicationPostValidator extends PostValidator {
    // ================ ATTRIBUTES ================
    private final ObjectType _node;
    private final String     _oldName;
    private final String     _newName;

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public ObjectTypesNameDuplicationPostValidator(@NotNull Diagram diagram, @NotNull Action action, @NotNull ObjectType node, @NotNull String oldName, @NotNull String newName) {
        super(diagram, action);

        this._node    = node;
        this._oldName = oldName;
        this._newName = newName;
    }

    // ----------------- contract -----------------
    @Override
    protected void validate() {
        ArrayList<ObjectType> oldNameObjectTypes = this._diagram.getElements(ObjectType.class).filter(e -> e.name().equals(this._oldName)).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<ObjectType> newNameObjectTypes = this._diagram.getElements(ObjectType.class).filter(e -> e.name().equals(this._newName)).collect(Collectors.toCollection(ArrayList::new));
        ObjectTypesNameDuplicationLogicError oldSameNameLogicError = null;
        ObjectTypesNameDuplicationLogicError newSameNameLogicError = null;

        for (LogicError logicError : this._diagram.logicErrors().toList()) {
            if (logicError instanceof ObjectTypesNameDuplicationLogicError) {
                if      (((ObjectTypesNameDuplicationLogicError)logicError).name().equals(this._oldName)) { oldSameNameLogicError = (ObjectTypesNameDuplicationLogicError)logicError; }
                else if (((ObjectTypesNameDuplicationLogicError)logicError).name().equals(this._newName)) { newSameNameLogicError = (ObjectTypesNameDuplicationLogicError)logicError; }
            }
        }

        if (oldSameNameLogicError != null)  { this._removeLogicErrorFromDiagram(oldSameNameLogicError); }
        if (newSameNameLogicError != null)  { this._removeLogicErrorFromDiagram(newSameNameLogicError); }
        if (oldNameObjectTypes.size() > 1)  { this._addLogicErrorToDiagram(new ObjectTypesNameDuplicationLogicError(this._oldName, oldNameObjectTypes)); }
        if (newNameObjectTypes.size() > 1)  { this._addLogicErrorToDiagram(new ObjectTypesNameDuplicationLogicError(this._newName, newNameObjectTypes)); }
    }
}
