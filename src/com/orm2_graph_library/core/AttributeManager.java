package com.orm2_graph_library.core;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class AttributeManager {
    // ================ ATTRIBUTES ================
    private final Set<Attribute> _attributes = new HashSet<>();

    // ================ OPERATIONS ================
    // ----------------- contract -----------------
    public boolean hasAttribute(String name, Class<? extends Attribute> type) {
        name = name.toLowerCase();
        for (var attr : this._attributes) {
            if (attr.name() == name && type.isAssignableFrom(attr.getClass())) { return true; }
        }

        return false;
    }

    @NotNull
    public <T extends Attribute> T attribute(String name, Class<T> type) {
        name = name.toLowerCase();
        T result = null;
        for (var attr : this._attributes) {
            if (attr.name() == name && type.isAssignableFrom(attr.getClass())) { result = (T)attr; }
        }

        return result;
    }

    public boolean add(Attribute attribute) { return this._attributes.add(attribute); }
}
