package com.orm2_graph_library;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {
    static private Map<int[], Object> _map = new HashMap<>();

    private void addNewValue(Object value, int... indexes) {
        _map.put(indexes, value);
    }

    public static void main(String[] args) {
        _map.put(new int[]{0, 2}, 4);
        _map.put(new int[]{0, 2}, 8);

        for (var objectInfo : _map.entrySet()) {
            System.out.println(Arrays.stream(objectInfo.getKey()).toString() + ": " + objectInfo.getValue());
        }
    }
}
