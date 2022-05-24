package com.orm2_graph_library.nodes.common;

public abstract class DataType {
    public String name() {
        String name = this.getClass().getSimpleName();

        String result = Character.toString(name.charAt(0));
        for (int i=1; i < name.length(); i++) {
            result += (Character.isUpperCase(name.charAt(i)) ? " " + Character.toLowerCase(name.charAt(i)) : name.charAt(i));
        }

        return result;
    }
}
