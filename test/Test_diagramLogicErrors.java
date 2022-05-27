import com.orm2_graph_library.core.LogicError;
import com.orm2_graph_library.edges.SubtypingRelationEdge;
import com.orm2_graph_library.logic_errors.*;
import com.orm2_graph_library.nodes.common.EntityType;
import com.orm2_graph_library.nodes.constraints.ExclusionConstraint;
import com.orm2_graph_library.nodes.constraints.SubsetConstraint;
import com.orm2_graph_library.nodes.predicates.Predicate;
import org.junit.jupiter.api.Test;
import utils.Test_globalTest;

import java.util.*;
import java.util.stream.Collectors;

public class Test_diagramLogicErrors extends Test_globalTest {
    // ==================== TEST ====================
    // ---------------- ENTITY TYPES ----------------
    // Name duplication
    @Test
    void entityType_NameDuplication() {
        // Prepare data and start testing
        for (int i=0; i<3; i++) {
            test_addDiagramElement(this._diagram.addNode(new EntityType()));
            test_addDiagramElement(this._diagram.addNode(new EntityType()));
            test_addDiagramElement(this._diagram.addNode(new EntityType()));
            test_addDiagramElement(this._diagram.addNode(new EntityType())).setName("Hello");
        }

        // Check result
        for (var entityType : test_entityTypes) {
            Set<LogicError> expLogicErrors = new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)));
            if (entityType.name().equals("Hello")) { expLogicErrors.add(new ObjectTypesNameDuplicationLogicError("Hello", test_entityTypes.stream().filter(e -> e.name().equals("Hello")).collect(Collectors.toCollection(ArrayList::new)))); }

            test_entityTypesLogicErrors.put(entityType, expLogicErrors);
        }
    }

    @Test
    void entityType_severalNameDuplications() {
        // Prepare data and start testing
        for (int i=0; i<3; i++) {
            test_addDiagramElement(this._diagram.addNode(new EntityType()));
            test_addDiagramElement(this._diagram.addNode(new EntityType()));
            test_addDiagramElement(this._diagram.addNode(new EntityType())).setName("Hello");
            test_addDiagramElement(this._diagram.addNode(new EntityType())).setName("Super");
            test_addDiagramElement(this._diagram.addNode(new EntityType())).setName("Super");
        }

        // Check result
        for (var entityType : test_entityTypes) {
            Set<LogicError> expLogicErrors = new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)));
            if (entityType.name().equals("Hello"))      { expLogicErrors.add(new ObjectTypesNameDuplicationLogicError("Hello", test_entityTypes.stream().filter(e -> e.name().equals("Hello")).collect(Collectors.toCollection(ArrayList::new)))); }
            else if (entityType.name().equals("Super")) { expLogicErrors.add(new ObjectTypesNameDuplicationLogicError("Super", test_entityTypes.stream().filter(e -> e.name().equals("Super")).collect(Collectors.toCollection(ArrayList::new)))); }

            test_entityTypesLogicErrors.put(entityType, expLogicErrors);
        }
    }

    @Test
    void entityType_renameForRemovingNameDuplication() {
        // Prepare data and start testing
        for (int i=0; i<3; i++) {
            test_addDiagramElement(this._diagram.addNode(new EntityType()));
            test_addDiagramElement(this._diagram.addNode(new EntityType()));
            test_addDiagramElement(this._diagram.addNode(new EntityType()));
            test_addDiagramElement(this._diagram.addNode(new EntityType())).setName("Hello");
        }

        for (int i=0; i<3; i++) {
            test_entityTypes.get(i*4 + 3).setName("Hello " + i);
        }

        // Check result
        for (var entityType : test_entityTypes) {
            test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType))));
        }
    }

    @Test
    void entityType_renameForRemovingSeveralNameDuplications() {
        // Prepare data and start testing
        for (int i=0; i<3; i++) {
            test_addDiagramElement(this._diagram.addNode(new EntityType()));
            test_addDiagramElement(this._diagram.addNode(new EntityType()));
            test_addDiagramElement(this._diagram.addNode(new EntityType())).setName("Hello");
            test_addDiagramElement(this._diagram.addNode(new EntityType())).setName("Super");
            test_addDiagramElement(this._diagram.addNode(new EntityType())).setName("Super");
        }

        for (int i=0; i<3; i++) {
            test_entityTypes.get(i*5 + 2).setName("Hello " + i);
            test_entityTypes.get(i*5 + 3).setName("Super0 " + i);
            test_entityTypes.get(i*5 + 4).setName("Super1 " + i);
        }

        // Check result
        for (var entityType : test_entityTypes) {
            test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType))));
        }
    }

    // Subtyping cycles
    @Test
    void entityType_cycleConnection() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));

        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(2).centerAnchorPoint(), test_entityTypes.get(0).centerAnchorPoint()));

        // Check result
        ArrayList<EntityType> entityTypeCycle = new ArrayList<>(List.of(test_entityTypes.get(2), test_entityTypes.get(0), test_entityTypes.get(1)));
        for (var entityType : test_entityTypes) {
            test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType), new SubtypingCycleLogicError(entityTypeCycle))));
        }

        for (var subtypingRelationEdge : test_subtypingRelationEdges) {
            test_subtypingRelationEdgesLogicErrors.put(subtypingRelationEdge, new HashSet<>(List.of(new SubtypingCycleLogicError(entityTypeCycle))));
        }
    }

    @Test
    void entityType_twoCycleConnection() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }

        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(2).centerAnchorPoint(), test_entityTypes.get(0).centerAnchorPoint()));

        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(3).centerAnchorPoint(), test_entityTypes.get(4).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(4).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint()));

        // Check result
        ArrayList<EntityType> entityTypeCycle0 = new ArrayList<>(List.of(test_entityTypes.get(2), test_entityTypes.get(0), test_entityTypes.get(1)));
        ArrayList<EntityType> entityTypeCycle1 = new ArrayList<>(List.of(test_entityTypes.get(4), test_entityTypes.get(3)));

        for (var entityType : test_entityTypes) {
            if (test_entityTypes.indexOf(entityType) <= 2) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType), new SubtypingCycleLogicError(entityTypeCycle0)))); }
            else                                           { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType), new SubtypingCycleLogicError(entityTypeCycle1)))); }
        }

        for (var subtypingRelationEdge : test_subtypingRelationEdges) {
            if (test_entityTypes.indexOf(subtypingRelationEdge.begin()) <= 2) { test_subtypingRelationEdgesLogicErrors.put(subtypingRelationEdge, new HashSet<>(List.of(new SubtypingCycleLogicError(entityTypeCycle0)))); }
            else                                                              { test_subtypingRelationEdgesLogicErrors.put(subtypingRelationEdge, new HashSet<>(List.of(new SubtypingCycleLogicError(entityTypeCycle1)))); }
        }
    }

    @Test
    void entityType_twoCycleConnectionByReconnection() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }

        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint()));
        SubtypingRelationEdge edge0 = this._diagram.connectBySubtypingRelation(test_entityTypes.get(2).centerAnchorPoint(), test_entityTypes.get(0).centerAnchorPoint());

        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(3).centerAnchorPoint(), test_entityTypes.get(4).centerAnchorPoint()));
        SubtypingRelationEdge edge1 = this._diagram.connectBySubtypingRelation(test_entityTypes.get(4).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint());

        test_addDiagramElement(this._diagram.reconnectSubtypingRelation(test_entityTypes.get(4).centerAnchorPoint(), edge0));
        test_addDiagramElement(this._diagram.reconnectSubtypingRelation(test_entityTypes.get(2).centerAnchorPoint(), edge1));

        // Check result
        ArrayList<EntityType> entityTypeCycle = new ArrayList<>(List.of(test_entityTypes.get(2), test_entityTypes.get(3), test_entityTypes.get(4), test_entityTypes.get(0), test_entityTypes.get(1)));
        for (var entityType : test_entityTypes) {
            test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType), new SubtypingCycleLogicError(entityTypeCycle))));
        }

        for (var subtypingRelationEdge : test_subtypingRelationEdges) {
            test_subtypingRelationEdgesLogicErrors.put(subtypingRelationEdge, new HashSet<>(List.of(new SubtypingCycleLogicError(entityTypeCycle))));
        }
    }

    @Test
    void subtypingRelation_simpleCycle() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(i).centerAnchorPoint(), test_entityTypes.get((i+1) % 5).centerAnchorPoint())); }

        // Check result
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new SubtypingCycleLogicError(test_entityTypes), new EntityTypeWithNoneRefModeLogicError(entityType)))); }
        for (SubtypingRelationEdge edge : test_subtypingRelationEdges) { test_subtypingRelationEdgesLogicErrors.put(edge, new HashSet<>(List.of(new SubtypingCycleLogicError(test_entityTypes)))); }
    }

    @Test
    void subtypingRelation_selfCycle() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));

        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(0).centerAnchorPoint()));

        // Check result
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new SubtypingCycleLogicError(test_entityTypes), new EntityTypeWithNoneRefModeLogicError(entityType)))); }
        for (SubtypingRelationEdge edge : test_subtypingRelationEdges) { test_subtypingRelationEdgesLogicErrors.put(edge, new HashSet<>(List.of(new SubtypingCycleLogicError(test_entityTypes)))); }
    }

    @Test
    void subtypingConstraintRelation_connect() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new ExclusionConstraint()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));

        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_subtypingRelationEdges.get(0).anchorPoint()));

        // Check result
        for (var entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
        test_constraintsLogicErrors.put(test_constraints.get(0), new HashSet<>(List.of(new ConstraintHasNotEnoughConnectsLogicError(test_constraints.get(0), test_constraints.get(0).getIncidentElements(SubtypingRelationEdge.class).collect(Collectors.toCollection(ArrayList::new))))));
    }

    // TODO - @test :: Setting reference mode and data type for entity type.

    // ----------------- CONSTRAINTS ----------------
    @Test
    void constraint_wrongSubtypingConstraintRelationUsage() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new SubsetConstraint()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));

        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_subtypingRelationEdges.get(0).anchorPoint()));

        // Check result
        for (var entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
        test_subtypingConstraintRelationEdgesLogicErrors.put(test_subtypingConstraintRelationEdges.get(0), new HashSet<>(List.of(new IllegalSubtypingConstraintRelationLogicError(test_subtypingConstraintRelationEdges.get(0)))));
        test_constraintsLogicErrors.put(test_constraints.get(0), new HashSet<>(List.of(new ConstraintHasNotEnoughConnectsLogicError(test_constraints.get(0), test_constraints.get(0).getIncidentElements(SubtypingRelationEdge.class).collect(Collectors.toCollection(ArrayList::new))))));
        test_subtypingConstraintRelationEdgesLogicErrors.put(test_subtypingConstraintRelationEdges.get(0), new HashSet<>(List.of(new IllegalSubtypingConstraintRelationLogicError(test_subtypingConstraintRelationEdges.get(0)))));
    }
}
