package com.orm2_graph_library.post_validators;

import com.orm2_graph_library.core.Action;
import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.LogicError;
import com.orm2_graph_library.core.PostValidator;
import com.orm2_graph_library.edges.SubtypingRelationEdge;
import com.orm2_graph_library.logic_errors.EntityTypeWithNoneRefModeLogicError;
import com.orm2_graph_library.logic_errors.SubtypingCycleLogicError;
import com.orm2_graph_library.nodes.common.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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
                if (path.get(0) == path.get(path.size()-1) && path.size() != 1) {
                    subtypingCycles.add(path);
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

        for (ArrayList<EntityType> cycle : subtypingCycles) { this._addLogicErrorToDiagram(new SubtypingCycleLogicError(new ArrayList<>(cycle.subList(0, cycle.size()-1)))); }

        // Update inherit paths if current edge doesn't add subtyping cycles
        if (subtypingCycles.isEmpty()) {
            if (this._edge.end().hasRefMode()) {
                if (this._edge.begin().getIncidentElements(SubtypingRelationEdge.class).anyMatch(e -> e != this._edge)) {
                    this._edge.beNotInheritPathForRefMode();
                }

                ArrayList<SubtypingRelationEdge> currentWaveEdges = new ArrayList<>(List.of(this._edge));

                while (!currentWaveEdges.isEmpty()) {
                    ArrayList<SubtypingRelationEdge> newWaveEdges = new ArrayList<>();

                    for (SubtypingRelationEdge edge : currentWaveEdges) {
                        if (!edge.hasLogicErrors(SubtypingCycleLogicError.class)) {
                            if (this._canEdgeBeInheritPath(edge)) { edge.beInheritPathForRefMode(); }
                            else                                  { edge.beNotInheritPathForRefMode(); }

                            ArrayList<LogicError> noneRefModeLogicErrors = edge.begin().getLogicErrors(EntityTypeWithNoneRefModeLogicError.class).collect(Collectors.toCollection(ArrayList::new));
                            for (LogicError logicError : noneRefModeLogicErrors) { this._removeLogicErrorFromDiagram(logicError); }

                            newWaveEdges.addAll(edge.begin().getIncidentElements(SubtypingRelationEdge.class).filter(e -> e.end() == edge.begin()).collect(Collectors.toCollection(ArrayList::new)));
                        }
                    }

                    currentWaveEdges = new ArrayList<>(newWaveEdges);
                }
            }
            else if (this._edge.begin().hasRefMode()) {
                this._edge.beNotInheritPathForRefMode();
            }
        }
    }

    private boolean _canEdgeBeInheritPath(@NotNull SubtypingRelationEdge edge) {
        boolean result = true;
        if (edge.begin().isRefModeSet()) { // TODO :: @hack
            return false;
        }

        return result;
    }
}
