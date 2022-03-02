package com.orm2_graph_library.logic_errors;

import com.orm2_graph_library.core.LogicError;
import com.orm2_graph_library.nodes.common.ObjectType;

import java.util.ArrayList;

public class ObjectTypesNameDuplicationLogicError extends LogicError {
    // ================ ATTRIBUTES ================
    private String _name;
    private ArrayList<ObjectType> _objectTypes = new ArrayList<>();

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public ObjectTypesNameDuplicationLogicError(String name, ArrayList<ObjectType> objectTypes) {
        this._name = name;
        this._objectTypes.addAll(objectTypes);
    }

    // ---------------- attributes ----------------
    public String name() { return this._name; }

    public ArrayList<ObjectType> objectTypes() { return new ArrayList<>(this._objectTypes); }
    public void update(ArrayList<ObjectType> newObjectTypes) { this._objectTypes.clear(); this._objectTypes.addAll(newObjectTypes); }

    // ----------------- contract -----------------
    public String description() {
        String result = "Logic error :: Two or more object types with same name (\"" + this._name + "\")\n";
        for (ObjectType o : this._objectTypes) { result += " - " + o + "\n"; }

        return result;
    }
}
