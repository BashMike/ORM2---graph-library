package com.orm2_graph_library.core;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public abstract class Attribute <T> {
    // ================ ATTRIBUTES ================
    private final DiagramElement _owner;
    private final String         _name;
    private T                    _value;

    private ArrayList<PostValidator> _postValidators = new ArrayList<>();

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public Attribute(DiagramElement diagramElement, String name, T value) {
        this._owner = diagramElement;
        this._name  = name.toLowerCase();
        this._value = value;
    }

    // ---------------- attributes ----------------
    public String name() { return this._name; }

    public void addPostValidator(PostValidator postValidator) { this._postValidators.add(postValidator); }

    public T value() { return this._value; }
    public void setValue(T value) {
        this._value = value;
        this._owner._owner._actionManager.addAction(new ChangeAttributeValueAction(this, this._value, value));
    }

    // ================= SUBTYPES =================
    private class ChangeAttributeValueAction<T> extends Action {
        private final Attribute<T> _attribute;
        private final T            _oldValue;
        private final T            _newValue;

        public ChangeAttributeValueAction(Attribute<T> attribute, T oldValue, T newValue) {
            super(attribute._owner._owner);

            this._attribute = attribute;
            this._oldValue  = oldValue;
            this._newValue  = newValue;
        }

        @Override
        public void _execute() {
            this._attribute.setValue(this._newValue);

            for (PostValidator postValidator : this._attribute._postValidators) {
                postValidator.validate(this._attribute._owner._owner, this);
            }
        }

        @Override
        public void _undo() {
            this._attribute.setValue(this._oldValue);

            for (PostValidator postValidator : this._attribute._postValidators) {
                postValidator.validate(this._attribute._owner._owner, this);
            }
        }
    }
}
