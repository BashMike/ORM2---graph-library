package com.orm2_graph_library.post_validators;

import com.orm2_graph_library.core.Action;
import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.PostValidator;
import com.orm2_graph_library.edges.SubtypingRelationEdge;
import com.orm2_graph_library.logic_errors.SubtypingCycleLogicError;
import com.orm2_graph_library.nodes.common.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SubtypingCyclePostValidator extends PostValidator {
    private EntityType _startNode;

    public SubtypingCyclePostValidator(@NotNull Diagram diagram, @NotNull Action action, @NotNull EntityType startNode) {
        super(diagram, action);
        this._startNode = startNode;
    }

    @Override
    protected void validate() {
        ArrayList<ArrayList<EntityType>> subtypingCycles = new ArrayList<>();

        ArrayList<ArrayList<EntityType>> currentWave = new ArrayList<>();
        currentWave.add(new ArrayList<>(List.of(this._startNode)));

        while (!currentWave.isEmpty()) {
            ArrayList<ArrayList<EntityType>> newWave = new ArrayList<>();
            for (ArrayList<EntityType> path : currentWave) {
                if (path.get(0) == path.get(path.size()-1) && path.size() != 1) {
                    subtypingCycles.add(path);
                }
                else {
                    this._diagram.getElements(SubtypingRelationEdge.class)
                            .filter(e -> e.beginAnchorPoint().owner() == path.get(path.size()-1))
                            .forEach(e -> {
                                ArrayList<EntityType> newPath = new ArrayList<>(path);
                                newPath.add(e.endAnchorPoint().owner());

                                newWave.add(newPath);
                            });
                }
            }

            currentWave.clear();
            currentWave.addAll(newWave);
        }

        for (ArrayList<EntityType> cycle : subtypingCycles) { this._addLogicErrorToDiagram(new SubtypingCycleLogicError(new ArrayList<>(cycle.subList(0, cycle.size()-1)))); }
    }
}
