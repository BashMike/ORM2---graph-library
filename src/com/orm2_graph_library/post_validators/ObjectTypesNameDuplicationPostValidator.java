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
        ArrayList<ObjectType> sameNameObjectTypes = this._diagram.getElements(ObjectType.class).filter(e -> e.name().equals(this._newName)).collect(Collectors.toCollection(ArrayList::new));

        ObjectTypesNameDuplicationLogicError sameNameLogicError = null;
        for (LogicError logicError : this._diagram.logicErrors()) {
            if (logicError instanceof ObjectTypesNameDuplicationLogicError && ((ObjectTypesNameDuplicationLogicError)logicError).name().equals(this._newName)) {
                sameNameLogicError = (ObjectTypesNameDuplicationLogicError)logicError;
                break;
            }
        }

        if (sameNameLogicError != null)     { this._removeLogicErrorFromDiagram(sameNameLogicError); }
        if (sameNameObjectTypes.size() > 1) { this._addLogicErrorToDiagram(new ObjectTypesNameDuplicationLogicError(this._newName, sameNameObjectTypes)); }
    }
}
