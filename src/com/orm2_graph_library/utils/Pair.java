package com.orm2_graph_library.utils;

import java.util.ArrayList;

public class Pair<T, G> {
    // ================ ATTRIBUTES ================
    final private T _first;
    final private G _second;

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    public Pair(T first, G second) {
        this._first  = first;
        this._second = second;
    }

    // ----------------- contract -----------------
    public T first()  { return this._first; }
    public G second() { return this._second; }

    // ---------------- comparison ----------------
    @Override
    public boolean equals(Object other) {
        if (other instanceof Pair) { return (this._first.equals(((Pair)other)._first) && this._second.equals(((Pair)other)._second)); }
        else                       { return false; }
    }

    @Override
    public int hashCode() { return this._first.hashCode() * this._second.hashCode(); }
}
