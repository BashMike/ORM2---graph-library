package com.orm2_graph_library.logic_errors;

import com.orm2_graph_library.core.LogicError;
import com.orm2_graph_library.nodes.common.ObjectType;

import java.util.ArrayList;
import java.util.HashSet;

public class ObjectTypesNameDuplicationLogicError extends LogicError {
    // ================ ATTRIBUTES ================
    private String _name;
    private ArrayList<ObjectType> _objectTypes = new ArrayList<>();

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public ObjectTypesNameDuplicationLogicError(String name, ArrayList<ObjectType> objectTypes) {
        super();

        this._name = name;
        this._objectTypes.addAll(objectTypes);

        this._errorParticipants.addAll(this._objectTypes);
    }

    // ---------------- attributes ----------------
    public String name() { return this._name; }

    public ArrayList<ObjectType> objectTypes() { return new ArrayList<>(this._objectTypes); }

    // ----------------- contract -----------------
    public String description() {
        String result = "Logic error :: Two or more object types with same name (\"" + this._name + "\")\n";
        for (ObjectType o : this._objectTypes) { result += " - " + o + "\n"; }

        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (super.equals(other)) {
            boolean areSameNames = this._name.equals(((ObjectTypesNameDuplicationLogicError)other)._name);
            boolean areSameObjectTypes = new HashSet<>(this._objectTypes).equals(new HashSet<>(((ObjectTypesNameDuplicationLogicError)other)._objectTypes));

            return areSameNames && areSameObjectTypes;
        }

        return false;
    }

    @Override
    public int hashCode() { return this._name.length() * this._objectTypes.size(); }
}
