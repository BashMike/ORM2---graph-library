package com.orm2_graph_library.post_validators;

import com.orm2_graph_library.core.Action;
import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.PostValidator;
import com.orm2_graph_library.edges.SubtypingRelationEdge;
import com.orm2_graph_library.logic_errors.SubtypingCycleLogicError;
import com.orm2_graph_library.nodes.common.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class ConnectBySubtypingRelationPostValidator extends PostValidator {
    private SubtypingRelationEdge _edge;

    // TODO - @modify :: Store edge which probably starts subtyping cycle.
    public ConnectBySubtypingRelationPostValidator(@NotNull Diagram diagram, @NotNull Action action, @NotNull SubtypingRelationEdge edge) {
        super(diagram, action);
        this._edge = edge;
    }

    @Override
    protected void validate() {
        // Search for cycles
        ArrayList<ArrayList<EntityType>> subtypingCycles = new ArrayList<>();

        ArrayList<ArrayList<EntityType>> currentWave = new ArrayList<>();
        currentWave.add(new ArrayList<>(List.of(this._edge.begin())));

        while (!currentWave.isEmpty()) {
            ArrayList<ArrayList<EntityType>> newWave = new ArrayList<>();
            for (ArrayList<EntityType> path : currentWave) {
                EntityType lastElement = path.get(path.size()-1);

                if (path.stream().filter(e -> e == lastElement).count() > 1) {
                    ArrayList<EntityType> resultPath = new ArrayList<>(path.subList(path.indexOf(lastElement), path.lastIndexOf(lastElement)+1));
                    if (path.size() == resultPath.size() && resultPath.contains(this._edge.end())) { subtypingCycles.add(new ArrayList<>(resultPath.subList(0, resultPath.size()-1))); }
                }
                else {
                    path.get(path.size()-1).getIncidentElements(EntityType.class)
                            .forEachOrdered(e -> {
                                ArrayList<EntityType> newPath = new ArrayList<>(path);
                                newPath.add(e);

                                newWave.add(newPath);
                            });
                }
            }

            currentWave.clear();
            currentWave.addAll(newWave);
        }

        subtypingCycles = this._removeCycleDuplicates(subtypingCycles);
        for (ArrayList<EntityType> cycle : subtypingCycles) { this._addLogicErrorToDiagram(new SubtypingCycleLogicError(cycle)); }
    }

    private ArrayList<ArrayList<EntityType>> _removeCycleDuplicates(ArrayList<ArrayList<EntityType>> subtypingCycles) {
        ArrayList<HashSet<EntityType>> subtypingCyclesSets = subtypingCycles.stream().map(HashSet::new).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<HashSet<EntityType>> existSubtypingCyclesSets = this._diagram.logicErrors(SubtypingCycleLogicError.class)
                .map(e -> new HashSet<>(e.cycleAsNodes()))
                .collect(Collectors.toCollection(ArrayList::new));

        // Remove cycles that diagram already has
        ArrayList<Integer> indexesToRemove = subtypingCyclesSets.stream()
                .filter(existSubtypingCyclesSets::contains)
                .map(subtypingCyclesSets::indexOf)
                .collect(Collectors.toCollection(ArrayList::new));

        // Remove duplicates cycles from gotten cycles pool
        for (int i=0; i<subtypingCyclesSets.size() && !indexesToRemove.contains(i); i++) {
            HashSet<EntityType> current = subtypingCyclesSets.get(i);

            indexesToRemove.addAll(subtypingCyclesSets.stream()
                    .filter(e -> e != current && e.equals(current))
                    .map(subtypingCyclesSets::indexOf)
                    .collect(Collectors.toCollection(ArrayList::new)));
        }

        return subtypingCycles.stream().filter(e -> !indexesToRemove.contains(subtypingCycles.indexOf(e))).collect(Collectors.toCollection(ArrayList::new));
    }
}
