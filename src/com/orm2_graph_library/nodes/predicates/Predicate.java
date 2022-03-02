package com.orm2_graph_library.nodes.predicates;

import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.MovableNode;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Predicate extends MovableNode {
    // ================ ATTRIBUTES ================
    private ArrayList<Role> _roles = new ArrayList<>();

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public Predicate(ArrayList<Role> roles) { this._roles.addAll(roles); }

    @Override
    protected void _initSelf(Diagram owner) {}

    // ---------------- attributes ----------------
    public ArrayList<Role> roles() { return new ArrayList<>(this._roles); }

    @Override
    public void moveTo(int x, int y) {
        super.moveTo(x, y);

        for (Role role : this._roles) {
            // TODO - @modify :: Move predicate with its roles.
            role.geometry().borderArea().leftTop().move(x, y);
        }
    }

    @Override
    public void moveBy(int shiftX, int shiftY) {
        super.moveBy(shiftX, shiftY);

        for (Role role : this._roles) {
            // TODO - @modify :: Move predicate with its roles.
            role.geometry().borderArea().leftTop().translate(shiftX, shiftY);
        }
    }
}
