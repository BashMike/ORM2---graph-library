package com.orm2_graph_library.logic_errors;

import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.LogicError;
import com.orm2_graph_library.edges.SubtypingRelationEdge;
import com.orm2_graph_library.nodes.common.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SubtypingCycleLogicError extends LogicError {
    final private ArrayList<EntityType>            _cycleAsNodes = new ArrayList<>();
    final private ArrayList<SubtypingRelationEdge> _cycleAsEdges = new ArrayList<>();

    public SubtypingCycleLogicError(@NotNull ArrayList<EntityType> entityTypeCycle) {
        super();
        this._cycleAsNodes.addAll(entityTypeCycle);

        Diagram ownerDiagram = this._cycleAsNodes.get(0).ownerDiagram();
        for (int i=0; i<this._cycleAsNodes.size()-1; i++) { this._cycleAsEdges.add(ownerDiagram.getConnectBySubtypingRelation(this._cycleAsNodes.get(i), this._cycleAsNodes.get(i+1))); }
        this._cycleAsEdges.add(ownerDiagram.getConnectBySubtypingRelation(this._cycleAsNodes.get(this._cycleAsNodes.size()-1), this._cycleAsNodes.get(0)));

        this._errorParticipants.addAll(this._cycleAsNodes);
        this._errorParticipants.addAll(this._cycleAsEdges);
    }

    public ArrayList<EntityType> cycleAsNodes() { return this._cycleAsNodes; }
    public ArrayList<SubtypingRelationEdge> cycleAsEdges() { return this._cycleAsEdges; }

    @Override
    public String description() {
        String result = "Subtyping cycle: ";
        for (EntityType entityType : this._cycleAsNodes) {
            result += entityType + " -> ";
        }
        result += this._cycleAsNodes.get(0);

        return result;
    }

    @Override
    public int hashCode() { return this._errorParticipants.size(); }
}
