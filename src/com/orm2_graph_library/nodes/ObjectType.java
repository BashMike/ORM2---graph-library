package com.orm2_graph_library.nodes;

import com.orm2_graph_library.attributes.StringAttribute;
import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.Node;

public abstract class ObjectType extends Node {
    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public ObjectType() {
        this._attributes.add(new StringAttribute(this, "name", ""));
    }

    // ---------------- connection ----------------
    @Override
    protected void _initSelf(Diagram owner) {
        boolean isVacantNameFound = false;
        int index;
        for (index=0; index<Integer.MAX_VALUE && !isVacantNameFound; index++) {
            final String objectTypeName = this.basicName() + " " + (index + 1);
            isVacantNameFound = !owner.getElements(ObjectType.class).stream().anyMatch(e -> e.attributes().attribute("name", StringAttribute.class).value().equals(objectTypeName));
        }

        assert isVacantNameFound : "ERROR :: Failed to find proper index for object type.";
        assert this._attributes.hasAttribute("name", StringAttribute.class) : "ERROR :: No name attribute in the object type.";

        this._owner._actionManager().stopRecordingActions();
        this._attributes.attribute("name", StringAttribute.class).setValue(this.basicName() + " " + index);
        this._owner._actionManager().startRecordingActions();

        // TODO - @add :: Connect post-validators to attributes so when they're changed post-validators will be activated.
    }

    public abstract String basicName();
}
