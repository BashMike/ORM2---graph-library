package com.orm2_graph_library.core;

import java.util.ArrayList;

public abstract class Attribute <T> {
    // ================ ATTRIBUTES ================
    private String _name;
    private T      _value;

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public Attribute(String name, T value) {
        this._name  = name.toLowerCase();
        this._value = value;
    }

    // ---------------- attributes ----------------
    public String name() { return this._name; }

    public T value() { return this._value; }
    public void setValue(T value) { this._value = value; }
}
