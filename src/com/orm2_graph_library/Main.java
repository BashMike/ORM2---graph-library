package com.orm2_graph_library;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        HashSet<ArrayList<Integer>> set = new HashSet<>();
        set.add(new ArrayList<>(List.of(1, 2, 3)));
        set.add(new ArrayList<>(List.of(1, 2, 3)));

        System.out.println(set);
    }
}
