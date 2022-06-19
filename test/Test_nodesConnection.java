import com.orm2_graph_library.action_errors.*;
import com.orm2_graph_library.anchor_points.AnchorPosition;
import com.orm2_graph_library.core.DiagramElement;
import com.orm2_graph_library.core.Edge;
import com.orm2_graph_library.core.LogicError;
import com.orm2_graph_library.edges.RoleConstraintRelationEdge;
import com.orm2_graph_library.edges.RoleRelationEdge;
import com.orm2_graph_library.edges.SubtypingConstraintRelationEdge;
import com.orm2_graph_library.edges.SubtypingRelationEdge;
import com.orm2_graph_library.logic_errors.*;
import com.orm2_graph_library.nodes.common.EntityType;
import com.orm2_graph_library.nodes.common.ValueType;
import com.orm2_graph_library.nodes.constraints.*;
import com.orm2_graph_library.nodes.predicates.ObjectifiedPredicate;
import com.orm2_graph_library.nodes.predicates.Predicate;
import com.orm2_graph_library.nodes.predicates.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import utils.Test_globalTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class Test_nodesConnection extends Test_globalTest {
    // ==================== INIT ====================
    @AfterEach
    void endTest_nodesConnection() {
        for (Predicate predicate : test_predicates) {
            for (Role role : predicate.roles().collect(Collectors.toCollection(ArrayList::new))) {
                LogicError logicError = new RoleHasNoTextSetLogicError(role);

                test_addLogicErrorTo(role.ownerPredicate(), logicError);
                test_addLogicErrorTo(role, logicError);
            }
        }
    }

    // ==================== TEST ====================
    // ----------------- CONNECTION -----------------
    // * Subtyping relation
    @Test
    void subtypingRelation_connect() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint()));

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
    }

    @Test
    void subtypingRelation_connectNotInDiagram() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }

        EntityType entityType0 = new EntityType();
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint()));

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
    }

    @Test
    void subtypingRelation_undoConnect() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint());
        this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint());
        this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint());

        for (int i=0; i<3; i++) { this._diagram.undoState(); }

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
    }

    @Test
    void subtypingRelation_redoConnect() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint()));

        for (int i=0; i<3; i++) { this._diagram.undoState(); }
        for (int i=0; i<3; i++) { this._diagram.redoState(); }

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
    }

    @Test
    void subtypingRelation_connectAfterUndo() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint());
        this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint());
        this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint());

        for (int i=0; i<3; i++) { this._diagram.undoState(); }

        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(2).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(4).centerAnchorPoint()));

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
    }

    @Test
    void actionError_subtypingRelation_connectWithSelf() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(0).centerAnchorPoint());

        // Check result
        test_actionErrors.add(new DiagramElementSelfConnectedActionError<>(test_entityTypes.get(0)));
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
    }

    @Test
    void actionError_subtypingRelation_connectTwice() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));

        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint()));
        this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint());

        // Check result
        test_actionErrors.add(new DoubleConnectionActionError<>(test_entityTypes.get(0), test_entityTypes.get(1), test_subtypingRelationEdges.get(0)));
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
    }

    @Test
    void actionError_makeIndependentEntityTypeBeInherited() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));

        test_entityTypes.get(0).setIsIndependent(true);

        this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint());
        this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint());
        this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint());

        // Check result
        test_actionErrors.add(new MakeIndependentEntityTypeBeInheritedActionError(test_entityTypes.get(0)));
        test_actionErrors.add(new MakeIndependentEntityTypeBeInheritedActionError(test_entityTypes.get(0)));
        test_actionErrors.add(new MakeIndependentEntityTypeBeInheritedActionError(test_entityTypes.get(0)));
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
    }

    @Test
    void subtypingRelation_deleteConnectedEntityType() {
        // Prepare data and start testing ...
        // ... Add nodes
        EntityType entityType = this._diagram.addNode(new EntityType());
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));

        // ... Connect nodes
        this._diagram.connectBySubtypingRelation(entityType.centerAnchorPoint(), test_entityTypes.get(0).centerAnchorPoint());
        this._diagram.connectBySubtypingRelation(entityType.centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint());
        this._diagram.connectBySubtypingRelation(entityType.centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint());
        this._diagram.connectBySubtypingRelation(test_entityTypes.get(2).centerAnchorPoint(), entityType.centerAnchorPoint());
        this._diagram.connectBySubtypingRelation(test_entityTypes.get(3).centerAnchorPoint(), entityType.centerAnchorPoint());
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(0).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(2).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(3).centerAnchorPoint(), test_entityTypes.get(0).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint()));

        // ... Main test action
        this._diagram.removeNode(entityType);

        // Check result
        ArrayList<EntityType> entityTypeCycle0 = new ArrayList<>(List.of(test_entityTypes.get(1), test_entityTypes.get(0)));
        ArrayList<EntityType> entityTypeCycle1 = new ArrayList<>(List.of(test_entityTypes.get(0), test_entityTypes.get(2), test_entityTypes.get(3)));

        test_addLogicErrorsTo(test_entityTypes.get(0), List.of(new EntityTypeWithNoneRefModeLogicError(test_entityTypes.get(0)), new SubtypingCycleLogicError(entityTypeCycle0), new SubtypingCycleLogicError(entityTypeCycle1)));
        test_addLogicErrorsTo(test_entityTypes.get(1), List.of(new EntityTypeWithNoneRefModeLogicError(test_entityTypes.get(1)), new SubtypingCycleLogicError(entityTypeCycle0)));
        test_addLogicErrorsTo(test_entityTypes.get(2), List.of(new EntityTypeWithNoneRefModeLogicError(test_entityTypes.get(2)), new SubtypingCycleLogicError(entityTypeCycle1)));
        test_addLogicErrorsTo(test_entityTypes.get(3), List.of(new EntityTypeWithNoneRefModeLogicError(test_entityTypes.get(3)), new SubtypingCycleLogicError(entityTypeCycle1)));

        test_addLogicErrorTo(test_subtypingRelationEdges.get(0), new SubtypingCycleLogicError(entityTypeCycle0));
        test_addLogicErrorTo(test_subtypingRelationEdges.get(1), new SubtypingCycleLogicError(entityTypeCycle0));
        test_addLogicErrorTo(test_subtypingRelationEdges.get(2), new SubtypingCycleLogicError(entityTypeCycle1));
        test_addLogicErrorTo(test_subtypingRelationEdges.get(3), new SubtypingCycleLogicError(entityTypeCycle1));
        test_addLogicErrorTo(test_subtypingRelationEdges.get(4), new SubtypingCycleLogicError(entityTypeCycle1));
    }

    @Test
    void subtypingRelation_undoDeletingConnectedEntityType() {
        // Prepare data and start testing ...
        // ... Add nodes
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));

        // ... Connect nodes
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(3).centerAnchorPoint(), test_entityTypes.get(0).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(4).centerAnchorPoint(), test_entityTypes.get(0).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(2).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(3).centerAnchorPoint(), test_entityTypes.get(4).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(4).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint()));

        // ... Main test action
        this._diagram.removeNode(test_entityTypes.get(0));
        this._diagram.undoState();

        // Check result
        SubtypingCycleLogicError cycleError0 = new SubtypingCycleLogicError(new ArrayList<>(List.of(test_entityTypes.get(1), test_entityTypes.get(2))));
        SubtypingCycleLogicError cycleError1 = new SubtypingCycleLogicError(new ArrayList<>(List.of(test_entityTypes.get(1), test_entityTypes.get(3), test_entityTypes.get(4))));
        SubtypingCycleLogicError cycleError2 = new SubtypingCycleLogicError(new ArrayList<>(List.of(test_entityTypes.get(3), test_entityTypes.get(0))));
        SubtypingCycleLogicError cycleError3 = new SubtypingCycleLogicError(new ArrayList<>(List.of(test_entityTypes.get(3), test_entityTypes.get(4), test_entityTypes.get(0))));
        SubtypingCycleLogicError cycleError4 = new SubtypingCycleLogicError(new ArrayList<>(List.of(test_entityTypes.get(1), test_entityTypes.get(3), test_entityTypes.get(0))));
        SubtypingCycleLogicError cycleError5 = new SubtypingCycleLogicError(new ArrayList<>(List.of(test_entityTypes.get(1), test_entityTypes.get(3), test_entityTypes.get(0), test_entityTypes.get(2))));
        SubtypingCycleLogicError cycleError6 = new SubtypingCycleLogicError(new ArrayList<>(List.of(test_entityTypes.get(1), test_entityTypes.get(3), test_entityTypes.get(4), test_entityTypes.get(0))));
        SubtypingCycleLogicError cycleError7 = new SubtypingCycleLogicError(new ArrayList<>(List.of(test_entityTypes.get(1), test_entityTypes.get(3), test_entityTypes.get(4), test_entityTypes.get(0), test_entityTypes.get(2))));

        test_addLogicErrorsTo(test_entityTypes.get(0), List.of(new EntityTypeWithNoneRefModeLogicError(test_entityTypes.get(0)), cycleError2, cycleError3, cycleError4, cycleError5, cycleError6, cycleError7));
        test_addLogicErrorsTo(test_entityTypes.get(1), List.of(new EntityTypeWithNoneRefModeLogicError(test_entityTypes.get(1)), cycleError0, cycleError1, cycleError4, cycleError5, cycleError6, cycleError7));
        test_addLogicErrorsTo(test_entityTypes.get(2), List.of(new EntityTypeWithNoneRefModeLogicError(test_entityTypes.get(2)), cycleError0, cycleError5, cycleError7));
        test_addLogicErrorsTo(test_entityTypes.get(3), List.of(new EntityTypeWithNoneRefModeLogicError(test_entityTypes.get(3)), cycleError1, cycleError2, cycleError3, cycleError4, cycleError5, cycleError6, cycleError7));
        test_addLogicErrorsTo(test_entityTypes.get(4), List.of(new EntityTypeWithNoneRefModeLogicError(test_entityTypes.get(4)), cycleError1, cycleError3, cycleError6, cycleError7));

        test_addLogicErrorsTo(test_subtypingRelationEdges.get(0), List.of(cycleError4, cycleError6));
        test_addLogicErrorsTo(test_subtypingRelationEdges.get(1), List.of(cycleError5, cycleError7));
        test_addLogicErrorsTo(test_subtypingRelationEdges.get(2), List.of(cycleError2, cycleError3));
        test_addLogicErrorsTo(test_subtypingRelationEdges.get(3), List.of(cycleError2, cycleError4, cycleError5));
        test_addLogicErrorsTo(test_subtypingRelationEdges.get(4), List.of(cycleError3, cycleError6, cycleError7));
        test_addLogicErrorsTo(test_subtypingRelationEdges.get(5), List.of(cycleError0));
        test_addLogicErrorsTo(test_subtypingRelationEdges.get(6), List.of(cycleError0, cycleError5, cycleError7));
        test_addLogicErrorsTo(test_subtypingRelationEdges.get(7), List.of(cycleError1, cycleError3, cycleError6, cycleError7));
        test_addLogicErrorsTo(test_subtypingRelationEdges.get(8), List.of(cycleError1));
        test_addLogicErrorsTo(test_subtypingRelationEdges.get(9), List.of(cycleError1, cycleError4, cycleError5, cycleError6, cycleError7));
    }

    @Test
    void subtypingRelation_redoDeletingConnectedEntityType() {
        // Prepare data and start testing ...
        // ... Add nodes
        EntityType entityType = this._diagram.addNode(new EntityType());
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));

        // ... Connect nodes
        this._diagram.connectBySubtypingRelation(entityType.centerAnchorPoint(), test_entityTypes.get(0).centerAnchorPoint());
        this._diagram.connectBySubtypingRelation(entityType.centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint());
        this._diagram.connectBySubtypingRelation(entityType.centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint());
        this._diagram.connectBySubtypingRelation(test_entityTypes.get(2).centerAnchorPoint(), entityType.centerAnchorPoint());
        this._diagram.connectBySubtypingRelation(test_entityTypes.get(3).centerAnchorPoint(), entityType.centerAnchorPoint());
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(0).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(2).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(3).centerAnchorPoint(), test_entityTypes.get(0).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint()));

        // ... Main test action
        this._diagram.removeNode(entityType);
        this._diagram.undoState();
        this._diagram.redoState();

        // Check result
        ArrayList<EntityType> entityTypeCycle0 = new ArrayList<>(List.of(test_entityTypes.get(1), test_entityTypes.get(0)));
        ArrayList<EntityType> entityTypeCycle1 = new ArrayList<>(List.of(test_entityTypes.get(0), test_entityTypes.get(2), test_entityTypes.get(3)));

        test_addLogicErrorsTo(test_entityTypes.get(0), List.of(new EntityTypeWithNoneRefModeLogicError(test_entityTypes.get(0)), new SubtypingCycleLogicError(entityTypeCycle0), new SubtypingCycleLogicError(entityTypeCycle1)));
        test_addLogicErrorsTo(test_entityTypes.get(1), List.of(new EntityTypeWithNoneRefModeLogicError(test_entityTypes.get(1)), new SubtypingCycleLogicError(entityTypeCycle0)));
        test_addLogicErrorsTo(test_entityTypes.get(2), List.of(new EntityTypeWithNoneRefModeLogicError(test_entityTypes.get(2)), new SubtypingCycleLogicError(entityTypeCycle1)));
        test_addLogicErrorsTo(test_entityTypes.get(3), List.of(new EntityTypeWithNoneRefModeLogicError(test_entityTypes.get(3)), new SubtypingCycleLogicError(entityTypeCycle1)));

        test_addLogicErrorTo(test_subtypingRelationEdges.get(0), new SubtypingCycleLogicError(entityTypeCycle0));
        test_addLogicErrorTo(test_subtypingRelationEdges.get(1), new SubtypingCycleLogicError(entityTypeCycle0));
        test_addLogicErrorTo(test_subtypingRelationEdges.get(2), new SubtypingCycleLogicError(entityTypeCycle1));
        test_addLogicErrorTo(test_subtypingRelationEdges.get(3), new SubtypingCycleLogicError(entityTypeCycle1));
        test_addLogicErrorTo(test_subtypingRelationEdges.get(4), new SubtypingCycleLogicError(entityTypeCycle1));
    }
    // * Role relation
    @Test
    void roleRelation_connect() {
        // Prepare data and start testing
        for (int i=0; i<3; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        for (int i=0; i<3; i++) { test_addDiagramElement(this._diagram.addNode(new ValueType())); }
        for (int i=0; i<3; i++) { test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(2)))); }

        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));

        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(0).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(1).anchorPoint(AnchorPosition.UP),    test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(2).anchorPoint(AnchorPosition.DOWN),  test_valueTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(3).anchorPoint(AnchorPosition.RIGHT), test_objectifiedPredicates.get(0).leftAnchorPoint()));

        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(0).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(2).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(1).anchorPoint(AnchorPosition.RIGHT), test_objectifiedPredicates.get(2).leftAnchorPoint()));

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
        for (ValueType valueType : test_valueTypes) { test_addLogicErrorTo(valueType, new ValueTypeWithNoneDataTypeLogicError(valueType)); }
    }

    @Test
    void roleRelation_undoConnect() {
        // Prepare data and start testing
        for (int i=0; i<3; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        for (int i=0; i<3; i++) { test_addDiagramElement(this._diagram.addNode(new ValueType())); }
        for (int i=0; i<3; i++) { test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(2)))); }

        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));

        this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(0).centerAnchorPoint());
        this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(1).anchorPoint(AnchorPosition.UP),    test_entityTypes.get(1).centerAnchorPoint());
        this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(2).anchorPoint(AnchorPosition.DOWN),  test_valueTypes.get(1).centerAnchorPoint());
        this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(3).anchorPoint(AnchorPosition.RIGHT), test_objectifiedPredicates.get(0).leftAnchorPoint());

        this._diagram.connectByRoleRelation(test_predicates.get(0).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(2).centerAnchorPoint());
        this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(1).anchorPoint(AnchorPosition.RIGHT), test_objectifiedPredicates.get(2).leftAnchorPoint());

        for (int i=0; i<6; i++) { this._diagram.undoState(); }

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
        for (ValueType valueType : test_valueTypes) { test_addLogicErrorTo(valueType, new ValueTypeWithNoneDataTypeLogicError(valueType)); }
    }

    @Test
    void roleRelation_redoConnect() {
        // Prepare data and start testing
        for (int i=0; i<3; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        for (int i=0; i<3; i++) { test_addDiagramElement(this._diagram.addNode(new ValueType())); }
        for (int i=0; i<3; i++) { test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(2)))); }

        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));

        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(0).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(1).anchorPoint(AnchorPosition.UP),    test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(2).anchorPoint(AnchorPosition.DOWN),  test_valueTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(3).anchorPoint(AnchorPosition.RIGHT), test_objectifiedPredicates.get(0).leftAnchorPoint()));

        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(0).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(2).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(1).anchorPoint(AnchorPosition.RIGHT), test_objectifiedPredicates.get(2).leftAnchorPoint()));

        for (int i=0; i<6; i++) { this._diagram.undoState(); }
        for (int i=0; i<6; i++) { this._diagram.redoState(); }

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
        for (ValueType valueType : test_valueTypes) { test_addLogicErrorTo(valueType, new ValueTypeWithNoneDataTypeLogicError(valueType)); }
    }

    @Test
    void roleRelation_connectAfterUndo() {
        // Prepare data and start testing
        for (int i=0; i<3; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        for (int i=0; i<3; i++) { test_addDiagramElement(this._diagram.addNode(new ValueType())); }
        for (int i=0; i<3; i++) { test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(2)))); }

        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));

        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(0).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(1).anchorPoint(AnchorPosition.UP),    test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(2).anchorPoint(AnchorPosition.DOWN),  test_valueTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(3).anchorPoint(AnchorPosition.RIGHT), test_objectifiedPredicates.get(0).leftAnchorPoint()));

        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(0).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(2).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(1).anchorPoint(AnchorPosition.RIGHT), test_objectifiedPredicates.get(2).leftAnchorPoint()));

        for (int i=0; i<6; i++) { this._diagram.undoState(); }
        for (int i=0; i<6; i++) { this._diagram.redoState(); }

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
        for (ValueType valueType : test_valueTypes) { test_addLogicErrorTo(valueType, new ValueTypeWithNoneDataTypeLogicError(valueType)); }
    }

    @Test
    void actionError_roleRelation_connectObjectifiedPredicateWithItsInnerPredicate() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(3))));

        this._diagram.connectByRoleRelation(test_predicates.get(0).getRole(0).anchorPoint(AnchorPosition.LEFT), test_objectifiedPredicates.get(0).leftAnchorPoint());

        // Check result
        test_actionErrors.add(new ObjectifiedPredicateIsConnectedToItsInnerPredicateActionError(test_objectifiedPredicates.get(0)));
    }

    @Test
    void actionError_roleRelation_manyRoleParticipants() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new ValueType()));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));

        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(0).getRole(0).anchorPoint(AnchorPosition.LEFT), test_entityTypes.get(0).centerAnchorPoint()));
        this._diagram.connectByRoleRelation(test_predicates.get(0).getRole(0).anchorPoint(AnchorPosition.LEFT), test_valueTypes.get(0).centerAnchorPoint());

        // Check result
        test_actionErrors.add(new RoleHasTwoOrMoreRoleParticipantsActionError(test_predicates.get(0).getRole(0)));

        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
        for (ValueType valueType : test_valueTypes) { test_addLogicErrorTo(valueType, new ValueTypeWithNoneDataTypeLogicError(valueType)); }
    }

    @Test
    void roleRelation_deleteConnectedObjectifiedPredicateWithConnectedInnerPredicate() {
        // Prepare data and start testing ...
        // ... Add nodes
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));

        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));

        ObjectifiedPredicate objectifiedPredicate = this._diagram.addNode(new ObjectifiedPredicate(new Predicate(3)));

        // ... Connect nodes
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(0).getRole(1).anchorPoint(AnchorPosition.UP),   test_entityTypes.get(0).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(0).anchorPoint(AnchorPosition.LEFT), test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(2).getRole(0).anchorPoint(AnchorPosition.LEFT), test_entityTypes.get(2).centerAnchorPoint()));

        this._diagram.connectByRoleRelation(test_predicates.get(0).getRole(0).anchorPoint(AnchorPosition.LEFT),  objectifiedPredicate.rightAnchorPoint());
        this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(1).anchorPoint(AnchorPosition.DOWN),  objectifiedPredicate.upAnchorPoint());
        this._diagram.connectByRoleRelation(test_predicates.get(2).getRole(2).anchorPoint(AnchorPosition.RIGHT), objectifiedPredicate.rightAnchorPoint());

        this._diagram.connectByRoleRelation(objectifiedPredicate.innerPredicate().getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(0).centerAnchorPoint());
        this._diagram.connectByRoleRelation(objectifiedPredicate.innerPredicate().getRole(1).anchorPoint(AnchorPosition.UP),    test_entityTypes.get(1).centerAnchorPoint());
        this._diagram.connectByRoleRelation(objectifiedPredicate.innerPredicate().getRole(2).anchorPoint(AnchorPosition.RIGHT), test_entityTypes.get(2).centerAnchorPoint());

        // ... Main test action
        this._diagram.removeNode(objectifiedPredicate);

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
    }

    @Test
    void roleRelation_undoDeletingConnectedObjectifiedPredicateWithConnectedInnerPredicate() {
        // Prepare data and start testing ...
        // ... Add nodes
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));

        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));

        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(3))));

        // ... Connect nodes
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(0).getRole(1).anchorPoint(AnchorPosition.UP),   test_entityTypes.get(0).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(0).anchorPoint(AnchorPosition.LEFT), test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(2).getRole(0).anchorPoint(AnchorPosition.LEFT), test_entityTypes.get(2).centerAnchorPoint()));

        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(0).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_objectifiedPredicates.get(0).rightAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(1).anchorPoint(AnchorPosition.DOWN),  test_objectifiedPredicates.get(0).upAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(2).getRole(2).anchorPoint(AnchorPosition.RIGHT), test_objectifiedPredicates.get(0).rightAnchorPoint()));

        test_addDiagramElement(this._diagram.connectByRoleRelation(test_objectifiedPredicates.get(0).innerPredicate().getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(0).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_objectifiedPredicates.get(0).innerPredicate().getRole(1).anchorPoint(AnchorPosition.UP),    test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_objectifiedPredicates.get(0).innerPredicate().getRole(2).anchorPoint(AnchorPosition.RIGHT), test_entityTypes.get(2).centerAnchorPoint()));

        // ... Main test action
        this._diagram.removeNode(test_objectifiedPredicates.get(0));

        this._diagram.undoState();

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
    }

    @Test
    void roleRelation_redoDeletingConnectedObjectifiedPredicateWithConnectedInnerPredicate() {
        // Prepare data and start testing ...
        // ... Add nodes
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));

        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));

        ObjectifiedPredicate objectifiedPredicate = this._diagram.addNode(new ObjectifiedPredicate(new Predicate(3)));

        // ... Connect nodes
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(0).getRole(1).anchorPoint(AnchorPosition.UP),   test_entityTypes.get(0).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(0).anchorPoint(AnchorPosition.LEFT), test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(2).getRole(0).anchorPoint(AnchorPosition.LEFT), test_entityTypes.get(2).centerAnchorPoint()));

        this._diagram.connectByRoleRelation(test_predicates.get(0).getRole(0).anchorPoint(AnchorPosition.LEFT),  objectifiedPredicate.rightAnchorPoint());
        this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(1).anchorPoint(AnchorPosition.DOWN),  objectifiedPredicate.upAnchorPoint());
        this._diagram.connectByRoleRelation(test_predicates.get(2).getRole(2).anchorPoint(AnchorPosition.RIGHT), objectifiedPredicate.rightAnchorPoint());

        this._diagram.connectByRoleRelation(objectifiedPredicate.innerPredicate().getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(0).centerAnchorPoint());
        this._diagram.connectByRoleRelation(objectifiedPredicate.innerPredicate().getRole(1).anchorPoint(AnchorPosition.UP),    test_entityTypes.get(1).centerAnchorPoint());
        this._diagram.connectByRoleRelation(objectifiedPredicate.innerPredicate().getRole(2).anchorPoint(AnchorPosition.RIGHT), test_entityTypes.get(2).centerAnchorPoint());

        // ... Main test action
        this._diagram.removeNode(objectifiedPredicate);

        this._diagram.undoState();
        this._diagram.redoState();

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
    }

    @Test
    void roleRelation_disconnectAfterUndoDeletingConnectedObjectifiedPredicateWithConnectedInnerPredicate() {
        // Prepare data and start testing ...
        // ... Add nodes
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));

        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));

        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(3))));

        // ... Connect nodes
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(0).getRole(1).anchorPoint(AnchorPosition.UP),   test_entityTypes.get(0).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(0).anchorPoint(AnchorPosition.LEFT), test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(2).getRole(0).anchorPoint(AnchorPosition.LEFT), test_entityTypes.get(2).centerAnchorPoint()));

        this._diagram.connectByRoleRelation(test_predicates.get(0).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_objectifiedPredicates.get(0).rightAnchorPoint());
        this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(1).anchorPoint(AnchorPosition.DOWN),  test_objectifiedPredicates.get(0).upAnchorPoint());
        this._diagram.connectByRoleRelation(test_predicates.get(2).getRole(2).anchorPoint(AnchorPosition.RIGHT), test_objectifiedPredicates.get(0).rightAnchorPoint());

        this._diagram.connectByRoleRelation(test_objectifiedPredicates.get(0).innerPredicate().getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(0).centerAnchorPoint());
        this._diagram.connectByRoleRelation(test_objectifiedPredicates.get(0).innerPredicate().getRole(1).anchorPoint(AnchorPosition.UP),    test_entityTypes.get(1).centerAnchorPoint());
        this._diagram.connectByRoleRelation(test_objectifiedPredicates.get(0).innerPredicate().getRole(2).anchorPoint(AnchorPosition.RIGHT), test_entityTypes.get(2).centerAnchorPoint());

        // ... Main test action
        this._diagram.removeNode(test_objectifiedPredicates.get(0));

        this._diagram.undoState();

        this._diagram.disconnectRoleRelation(test_predicates.get(0).getRole(0), test_objectifiedPredicates.get(0));
        this._diagram.disconnectRoleRelation(test_predicates.get(1).getRole(1), test_objectifiedPredicates.get(0));
        this._diagram.disconnectRoleRelation(test_predicates.get(2).getRole(2), test_objectifiedPredicates.get(0));

        this._diagram.disconnectRoleRelation(test_objectifiedPredicates.get(0).innerPredicate().getRole(0), test_entityTypes.get(0));
        this._diagram.disconnectRoleRelation(test_objectifiedPredicates.get(0).innerPredicate().getRole(1), test_entityTypes.get(1));
        this._diagram.disconnectRoleRelation(test_objectifiedPredicates.get(0).innerPredicate().getRole(2), test_entityTypes.get(2));

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
    }

    // * Subtyping constraint relation
    @Test
    void subtypingConstraintRelation_connect() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        test_addDiagramElement(this._diagram.addNode(new ExclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new InclusiveOrConstraint()));

        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(4).centerAnchorPoint()));

        test_addDiagramElement(this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_subtypingRelationEdges.get(0).anchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_subtypingRelationEdges.get(1).anchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_subtypingRelationEdges.get(2).anchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_subtypingRelationEdges.get(3).anchorPoint()));

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
    }

    @Test
    void subtypingConstraintRelation_undoConnect() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        test_addDiagramElement(this._diagram.addNode(new ExclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new InclusiveOrConstraint()));

        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(4).centerAnchorPoint()));

        this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_subtypingRelationEdges.get(0).anchorPoint());
        this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_subtypingRelationEdges.get(1).anchorPoint());
        this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_subtypingRelationEdges.get(2).anchorPoint());
        this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_subtypingRelationEdges.get(3).anchorPoint());

        for (int i=0; i<4; i++) { this._diagram.undoState(); }

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
        for (Constraint constraint : test_constraints) { test_addLogicErrorTo(constraint, new ConstraintHasNotEnoughConnectsLogicError(constraint, constraint.getIncidentElements(Edge.class)
                .collect(Collectors.toCollection(ArrayList::new)))); }
    }

    @Test
    void subtypingConstraintRelation_redoConnect() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        test_addDiagramElement(this._diagram.addNode(new ExclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new InclusiveOrConstraint()));

        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(4).centerAnchorPoint()));

        test_addDiagramElement(this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_subtypingRelationEdges.get(0).anchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_subtypingRelationEdges.get(1).anchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_subtypingRelationEdges.get(2).anchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_subtypingRelationEdges.get(3).anchorPoint()));

        for (int i=0; i<4; i++) { this._diagram.undoState(); }
        for (int i=0; i<4; i++) { this._diagram.redoState(); }

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
    }

    @Test
    void subtypingConstraintRelation_connectAfterUndo() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        test_addDiagramElement(this._diagram.addNode(new ExclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new InclusiveOrConstraint()));

        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(4).centerAnchorPoint()));

        test_addDiagramElement(this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_subtypingRelationEdges.get(0).anchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_subtypingRelationEdges.get(1).anchorPoint()));
        this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_subtypingRelationEdges.get(2).anchorPoint());
        this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_subtypingRelationEdges.get(3).anchorPoint());

        this._diagram.undoState();
        this._diagram.undoState();

        test_addDiagramElement(this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_subtypingRelationEdges.get(2).anchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_subtypingRelationEdges.get(3).anchorPoint()));

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
        test_addLogicErrorTo(test_constraints.get(1), new ConstraintHasNotEnoughConnectsLogicError(test_constraints.get(1), test_constraints.get(1).getIncidentElements(Edge.class)
                .collect(Collectors.toCollection(ArrayList::new))));
    }

    @Test
    void subtypingRelation_deleteConnectedSubtypingRelationEdge() {
        // Prepare data and start testing ...
        // ... Add nodes
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new ExclusionConstraint()));

        // ... Connect nodes
        SubtypingRelationEdge edge = this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint());
        this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(0).centerAnchorPoint(), edge.anchorPoint());

        // ... Main test action
        this._diagram.disconnectSubtypingRelation(test_entityTypes.get(0), test_entityTypes.get(1));

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
        test_addLogicErrorTo(test_constraints.get(0), new ConstraintHasNotEnoughConnectsLogicError(test_constraints.get(0), new ArrayList<>()));
    }

    // * Role constraint relation
    @Test
    void roleConstraintRelation_connect() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new SubsetConstraint()));
        test_addDiagramElement(this._diagram.addNode(new InclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new UniquenessConstraint()));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(3))));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0, 1));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(1, 2));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(0, 1, 2));
        test_addDiagramElement(test_predicates.get(2).rolesSequence(0, 1, 2));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(1));
        test_addDiagramElement(test_predicates.get(2).rolesSequence(2));

        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0, 1)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(1).rolesSequence(1, 2)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_predicates.get(1).rolesSequence(0, 1, 2)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_predicates.get(2).rolesSequence(0, 1, 2)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(1).rolesSequence(1)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(2).rolesSequence(2)));

        // Check result
        Assertions.assertFalse(test_roleConstraintRelationEdges.get(0).isEndingEdge());
        Assertions.assertTrue(test_roleConstraintRelationEdges.get(1).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(2).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(3).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(4).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(5).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(6).isEndingEdge());

        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
    }

    @Test
    void roleConstraintRelation_undoConnect() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new SubsetConstraint()));
        test_addDiagramElement(this._diagram.addNode(new InclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new UniquenessConstraint()));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(3))));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0, 1));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(1, 2));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(0, 1, 2));
        test_addDiagramElement(test_predicates.get(2).rolesSequence(0, 1, 2));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(1));
        test_addDiagramElement(test_predicates.get(2).rolesSequence(2));

        this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0, 1));
        this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(1).rolesSequence(1, 2));
        this._diagram.connectByRoleConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_predicates.get(1).rolesSequence(0, 1, 2));
        this._diagram.connectByRoleConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_predicates.get(2).rolesSequence(0, 1, 2));
        this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0));
        this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(1).rolesSequence(1));
        this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(2).rolesSequence(2));

        for (int i=0; i<7; i++) { this._diagram.undoState(); }

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
        for (Constraint constraint : test_constraints) { test_addLogicErrorTo(constraint, new ConstraintHasNotEnoughConnectsLogicError(constraint, constraint.getIncidentElements(Edge.class)
                .collect(Collectors.toCollection(ArrayList::new)))); }
    }

    @Test
    void roleConstraintRelation_redoConnect() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new SubsetConstraint()));
        test_addDiagramElement(this._diagram.addNode(new InclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new UniquenessConstraint()));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(3))));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0, 1));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(1, 2));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(0, 1, 2));
        test_addDiagramElement(test_predicates.get(2).rolesSequence(0, 1, 2));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(1));
        test_addDiagramElement(test_predicates.get(2).rolesSequence(2));

        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0, 1)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(1).rolesSequence(1, 2)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_predicates.get(1).rolesSequence(0, 1, 2)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_predicates.get(2).rolesSequence(0, 1, 2)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(1).rolesSequence(1)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(2).rolesSequence(2)));

        for (int i=0; i<7; i++) { this._diagram.undoState(); }
        for (int i=0; i<7; i++) { this._diagram.redoState(); }

        // Check result
        Assertions.assertFalse(test_roleConstraintRelationEdges.get(0).isEndingEdge());
        Assertions.assertTrue(test_roleConstraintRelationEdges.get(1).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(2).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(3).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(4).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(5).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(6).isEndingEdge());

        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
    }

    @Test
    void roleConstraintRelation_connectAfterUndo() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new SubsetConstraint()));
        test_addDiagramElement(this._diagram.addNode(new InclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new UniquenessConstraint()));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(3))));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0, 1));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(1, 2));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(0, 1, 2));
        test_addDiagramElement(test_predicates.get(2).rolesSequence(0, 1, 2));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(1));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(0, 2));
        test_addDiagramElement(test_predicates.get(2).rolesSequence(0, 1));
        test_addDiagramElement(test_predicates.get(2).rolesSequence(2));

        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0, 1)));
        this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(1).rolesSequence(1, 2));
        this._diagram.connectByRoleConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_predicates.get(1).rolesSequence(0, 1, 2));
        this._diagram.connectByRoleConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_predicates.get(2).rolesSequence(0, 1, 2));
        this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0));
        this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(1).rolesSequence(1));
        this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(2).rolesSequence(2));

        for (int i=0; i<6; i++) { this._diagram.undoState(); }

        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(1).rolesSequence(0, 2)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_predicates.get(1).rolesSequence(0, 2)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_predicates.get(2).rolesSequence(0, 1)));

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
        test_addLogicErrorTo(test_constraints.get(2), new ConstraintHasNotEnoughConnectsLogicError(test_constraints.get(2), test_constraints.get(2).getIncidentElements(Edge.class)
                .collect(Collectors.toCollection(ArrayList::new))));
        Assertions.assertFalse(test_roleConstraintRelationEdges.get(0).isEndingEdge());
        Assertions.assertTrue(test_roleConstraintRelationEdges.get(1).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(2).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(3).isEndingEdge());
    }

    @Test
    void roleConstraintRelation_constraintsConflicts() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new SubsetConstraint()));
        test_addDiagramElement(this._diagram.addNode(new EqualityConstraint()));
        test_addDiagramElement(this._diagram.addNode(new ExclusionConstraint()));
        test_addDiagramElement(this._diagram.addNode(new ExclusionConstraint()));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));

        test_addDiagramElement(test_predicates.get(0).rolesSequence(0, 1));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(0, 2));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0, 1));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(0, 2));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0, 1, 2));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(0, 1, 2));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0, 1, 2));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(0, 1, 2));

        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0, 1)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(1).rolesSequence(0, 2)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0, 1)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(1).rolesSequence(0, 2)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0, 1, 2)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_predicates.get(1).rolesSequence(0, 1, 2)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(3).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0, 1, 2)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(3).centerAnchorPoint(), test_predicates.get(1).rolesSequence(0, 1, 2)));

        // Check result
        ArrayList<LogicError> logicErrors = new ArrayList<>();
        logicErrors.add(new RoleConstraintsConflictLogicError(new ArrayList<>(List.of(test_constraints.get(0), test_constraints.get(2))), new ArrayList<>(List.of(test_predicates.get(0).rolesSequence(0, 1), test_predicates.get(1).rolesSequence(0, 2)))));
        logicErrors.add(new RoleConstraintsConflictLogicError(new ArrayList<>(List.of(test_constraints.get(1), test_constraints.get(3))), new ArrayList<>(List.of(test_predicates.get(0).rolesSequence(0, 1, 2), test_predicates.get(1).rolesSequence(0, 1, 2)))));

        test_addLogicErrorTo(test_constraints.get(0), logicErrors.get(0));
        test_addLogicErrorTo(test_constraints.get(1), logicErrors.get(1));
        test_addLogicErrorTo(test_constraints.get(2), logicErrors.get(0));
        test_addLogicErrorTo(test_constraints.get(3), logicErrors.get(1));
    }

    // * Complex
    @Test
    void connect_complex() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new ValueType()));

        test_addDiagramElement(this._diagram.addNode(new SubsetConstraint()));
        test_addDiagramElement(this._diagram.addNode(new InclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new UniquenessConstraint()));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(3))));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0, 1));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(1, 2));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(0, 2));
        test_addDiagramElement(test_predicates.get(2).rolesSequence(0, 1));

        test_predicates.get(1).setOrientation(DiagramElement.Orientation.VERTICAL);

        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint()));

        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(0).getRole(0).anchorPoint(AnchorPosition.LEFT), test_entityTypes.get(0).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(1).anchorPoint(AnchorPosition.LEFT), test_valueTypes.get(0).centerAnchorPoint()));

        test_addDiagramElement(this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_subtypingRelationEdges.get(0).anchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_subtypingRelationEdges.get(1).anchorPoint()));

        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0, 1)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(1).rolesSequence(1, 2)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(1).rolesSequence(0, 2)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(2).rolesSequence(0, 1)));

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
        for (ValueType valueType : test_valueTypes) { test_addLogicErrorTo(valueType, new ValueTypeWithNoneDataTypeLogicError(valueType)); }
    }

    // ---------------- DISCONNECTION ---------------
    // * Subtyping relation
    @Test
    void subtypingRelation_disconnect() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint());
        this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint());
        this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint());
        this._diagram.disconnectSubtypingRelation(test_entityTypes.get(0), test_entityTypes.get(1));
        this._diagram.disconnectSubtypingRelation(test_entityTypes.get(0), test_entityTypes.get(2));
        this._diagram.disconnectSubtypingRelation(test_entityTypes.get(1), test_entityTypes.get(3));

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
    }

    @Test
    void subtypingRelation_undoDisconnect() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint()));
        this._diagram.disconnectSubtypingRelation(test_entityTypes.get(0), test_entityTypes.get(1));
        this._diagram.disconnectSubtypingRelation(test_entityTypes.get(0), test_entityTypes.get(2));
        this._diagram.disconnectSubtypingRelation(test_entityTypes.get(1), test_entityTypes.get(3));

        for (int i=0; i<3; i++) { this._diagram.undoState(); }

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
    }

    @Test
    void subtypingRelation_redoDisconnect() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint());
        this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint());
        this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint());
        this._diagram.disconnectSubtypingRelation(test_entityTypes.get(0), test_entityTypes.get(1));
        this._diagram.disconnectSubtypingRelation(test_entityTypes.get(0), test_entityTypes.get(2));
        this._diagram.disconnectSubtypingRelation(test_entityTypes.get(1), test_entityTypes.get(3));

        for (int i=0; i<3; i++) { this._diagram.undoState(); }
        for (int i=0; i<3; i++) { this._diagram.redoState(); }

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
    }

    @Test
    void subtypingRelation_disconnectAfterUndo() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint()));
        this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint());
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint()));
        this._diagram.disconnectSubtypingRelation(test_entityTypes.get(0), test_entityTypes.get(1));
        this._diagram.disconnectSubtypingRelation(test_entityTypes.get(0), test_entityTypes.get(2));
        this._diagram.disconnectSubtypingRelation(test_entityTypes.get(1), test_entityTypes.get(3));

        for (int i=0; i<3; i++) { this._diagram.undoState(); }

        this._diagram.disconnectSubtypingRelation(test_entityTypes.get(0), test_entityTypes.get(2));

        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(2).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(4).centerAnchorPoint()));

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
    }

    // * Role relation
    @Test
    void roleRelation_disconnect() {
        // Prepare data and start testing
        for (int i=0; i<3; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        for (int i=0; i<3; i++) { test_addDiagramElement(this._diagram.addNode(new ValueType())); }
        for (int i=0; i<3; i++) { test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(2)))); }

        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));

        this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(0).centerAnchorPoint());
        this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(1).anchorPoint(AnchorPosition.UP),    test_entityTypes.get(1).centerAnchorPoint());
        this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(2).anchorPoint(AnchorPosition.DOWN),  test_valueTypes.get(1).centerAnchorPoint());
        this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(3).anchorPoint(AnchorPosition.RIGHT), test_objectifiedPredicates.get(0).leftAnchorPoint());
        this._diagram.connectByRoleRelation(test_predicates.get(0).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(2).centerAnchorPoint());
        this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(1).anchorPoint(AnchorPosition.RIGHT), test_objectifiedPredicates.get(2).leftAnchorPoint());

        this._diagram.disconnectRoleRelation(test_predicates.get(3).getRole(0), test_entityTypes.get(0));
        this._diagram.disconnectRoleRelation(test_predicates.get(3).getRole(1), test_entityTypes.get(1));
        this._diagram.disconnectRoleRelation(test_predicates.get(3).getRole(2), test_valueTypes.get(1));
        this._diagram.disconnectRoleRelation(test_predicates.get(3).getRole(3), test_objectifiedPredicates.get(0));
        this._diagram.disconnectRoleRelation(test_predicates.get(0).getRole(0), test_entityTypes.get(2));
        this._diagram.disconnectRoleRelation(test_predicates.get(1).getRole(1), test_objectifiedPredicates.get(2));

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
        for (ValueType valueType : test_valueTypes) { test_addLogicErrorTo(valueType, new ValueTypeWithNoneDataTypeLogicError(valueType)); }
    }

    @Test
    void roleRelation_undoDisconnect() {
        // Prepare data and start testing
        for (int i=0; i<3; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        for (int i=0; i<3; i++) { test_addDiagramElement(this._diagram.addNode(new ValueType())); }
        for (int i=0; i<3; i++) { test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(2)))); }

        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));

        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(0).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(1).anchorPoint(AnchorPosition.UP),    test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(2).anchorPoint(AnchorPosition.DOWN),  test_valueTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(3).anchorPoint(AnchorPosition.RIGHT), test_objectifiedPredicates.get(0).leftAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(0).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(2).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(1).anchorPoint(AnchorPosition.RIGHT), test_objectifiedPredicates.get(2).leftAnchorPoint()));

        this._diagram.disconnectRoleRelation(test_predicates.get(3).getRole(0), test_entityTypes.get(0));
        this._diagram.disconnectRoleRelation(test_predicates.get(3).getRole(1), test_entityTypes.get(1));
        this._diagram.disconnectRoleRelation(test_predicates.get(3).getRole(2), test_valueTypes.get(1));
        this._diagram.disconnectRoleRelation(test_predicates.get(3).getRole(3), test_objectifiedPredicates.get(0));
        this._diagram.disconnectRoleRelation(test_predicates.get(0).getRole(0), test_entityTypes.get(2));
        this._diagram.disconnectRoleRelation(test_predicates.get(1).getRole(1), test_objectifiedPredicates.get(2));

        for (int i=0; i<6; i++) { this._diagram.undoState(); }

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
        for (ValueType valueType : test_valueTypes) { test_addLogicErrorTo(valueType, new ValueTypeWithNoneDataTypeLogicError(valueType)); }
    }

    @Test
    void roleRelation_redoDisconnect() {
        // Prepare data and start testing
        for (int i=0; i<3; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        for (int i=0; i<3; i++) { test_addDiagramElement(this._diagram.addNode(new ValueType())); }
        for (int i=0; i<3; i++) { test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(2)))); }

        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));

        this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(0).centerAnchorPoint());
        this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(1).anchorPoint(AnchorPosition.UP),    test_entityTypes.get(1).centerAnchorPoint());
        this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(2).anchorPoint(AnchorPosition.DOWN),  test_valueTypes.get(1).centerAnchorPoint());
        this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(3).anchorPoint(AnchorPosition.RIGHT), test_objectifiedPredicates.get(0).leftAnchorPoint());
        this._diagram.connectByRoleRelation(test_predicates.get(0).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(2).centerAnchorPoint());
        this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(1).anchorPoint(AnchorPosition.RIGHT), test_objectifiedPredicates.get(2).leftAnchorPoint());

        this._diagram.disconnectRoleRelation(test_predicates.get(3).getRole(0), test_entityTypes.get(0));
        this._diagram.disconnectRoleRelation(test_predicates.get(3).getRole(1), test_entityTypes.get(1));
        this._diagram.disconnectRoleRelation(test_predicates.get(3).getRole(2), test_valueTypes.get(1));
        this._diagram.disconnectRoleRelation(test_predicates.get(3).getRole(3), test_objectifiedPredicates.get(0));
        this._diagram.disconnectRoleRelation(test_predicates.get(0).getRole(0), test_entityTypes.get(2));
        this._diagram.disconnectRoleRelation(test_predicates.get(1).getRole(1), test_objectifiedPredicates.get(2));

        for (int i=0; i<6; i++) { this._diagram.undoState(); }
        for (int i=0; i<6; i++) { this._diagram.redoState(); }

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
        for (ValueType valueType : test_valueTypes) { test_addLogicErrorTo(valueType, new ValueTypeWithNoneDataTypeLogicError(valueType)); }
    }

    @Test
    void roleRelation_disconnectAfterUndo() {
        // Prepare data and start testing
        for (int i=0; i<3; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        for (int i=0; i<3; i++) { test_addDiagramElement(this._diagram.addNode(new ValueType())); }
        for (int i=0; i<3; i++) { test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(2)))); }

        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));

        this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(0).centerAnchorPoint());
        this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(1).anchorPoint(AnchorPosition.UP),    test_entityTypes.get(1).centerAnchorPoint());
        this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(2).anchorPoint(AnchorPosition.DOWN),  test_valueTypes.get(1).centerAnchorPoint());
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(3).anchorPoint(AnchorPosition.RIGHT), test_objectifiedPredicates.get(0).leftAnchorPoint()));
        this._diagram.connectByRoleRelation(test_predicates.get(0).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(2).centerAnchorPoint());
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(1).anchorPoint(AnchorPosition.RIGHT), test_objectifiedPredicates.get(2).leftAnchorPoint()));

        this._diagram.disconnectRoleRelation(test_predicates.get(3).getRole(0), test_entityTypes.get(0));
        this._diagram.disconnectRoleRelation(test_predicates.get(3).getRole(1), test_entityTypes.get(1));
        this._diagram.disconnectRoleRelation(test_predicates.get(3).getRole(2), test_valueTypes.get(1));
        this._diagram.disconnectRoleRelation(test_predicates.get(3).getRole(3), test_objectifiedPredicates.get(0));
        this._diagram.disconnectRoleRelation(test_predicates.get(0).getRole(0), test_entityTypes.get(2));
        this._diagram.disconnectRoleRelation(test_predicates.get(1).getRole(1), test_objectifiedPredicates.get(2));

        for (int i=0; i<3; i++) { this._diagram.undoState(); }

        this._diagram.disconnectRoleRelation(test_predicates.get(0).getRole(0), test_entityTypes.get(2));

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
        for (ValueType valueType : test_valueTypes) { test_addLogicErrorTo(valueType, new ValueTypeWithNoneDataTypeLogicError(valueType)); }
    }

    // * Subtyping constraint relation
    @Test
    void subtypingConstraintRelation_disconnect() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        test_addDiagramElement(this._diagram.addNode(new ExclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new InclusiveOrConstraint()));

        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(4).centerAnchorPoint()));

        this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_subtypingRelationEdges.get(0).anchorPoint());
        this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_subtypingRelationEdges.get(1).anchorPoint());
        this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_subtypingRelationEdges.get(2).anchorPoint());
        this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_subtypingRelationEdges.get(3).anchorPoint());

        this._diagram.disconnectSubtypingConstraintRelation(test_constraints.get(0), test_subtypingRelationEdges.get(0));
        this._diagram.disconnectSubtypingConstraintRelation(test_constraints.get(0), test_subtypingRelationEdges.get(1));
        this._diagram.disconnectSubtypingConstraintRelation(test_constraints.get(1), test_subtypingRelationEdges.get(2));
        this._diagram.disconnectSubtypingConstraintRelation(test_constraints.get(1), test_subtypingRelationEdges.get(3));

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
        for (Constraint constraint : test_constraints) { test_addLogicErrorTo(constraint, new ConstraintHasNotEnoughConnectsLogicError(constraint, constraint.getIncidentElements(Edge.class)
                .collect(Collectors.toCollection(ArrayList::new)))); }
    }

    @Test
    void subtypingConstraintRelation_undoDisconnect() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        test_addDiagramElement(this._diagram.addNode(new ExclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new InclusiveOrConstraint()));

        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(4).centerAnchorPoint()));

        test_addDiagramElement(this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_subtypingRelationEdges.get(0).anchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_subtypingRelationEdges.get(1).anchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_subtypingRelationEdges.get(2).anchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_subtypingRelationEdges.get(3).anchorPoint()));

        this._diagram.disconnectSubtypingConstraintRelation(test_constraints.get(0), test_subtypingRelationEdges.get(0));
        this._diagram.disconnectSubtypingConstraintRelation(test_constraints.get(0), test_subtypingRelationEdges.get(1));
        this._diagram.disconnectSubtypingConstraintRelation(test_constraints.get(1), test_subtypingRelationEdges.get(2));
        this._diagram.disconnectSubtypingConstraintRelation(test_constraints.get(1), test_subtypingRelationEdges.get(3));

        for (int i=0; i<4; i++) { this._diagram.undoState(); }

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
    }

    @Test
    void subtypingConstraintRelation_redoDisconnect() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        test_addDiagramElement(this._diagram.addNode(new ExclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new InclusiveOrConstraint()));

        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(4).centerAnchorPoint()));

        this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_subtypingRelationEdges.get(0).anchorPoint());
        this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_subtypingRelationEdges.get(1).anchorPoint());
        this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_subtypingRelationEdges.get(2).anchorPoint());
        this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_subtypingRelationEdges.get(3).anchorPoint());

        this._diagram.disconnectSubtypingConstraintRelation(test_constraints.get(0), test_subtypingRelationEdges.get(0));
        this._diagram.disconnectSubtypingConstraintRelation(test_constraints.get(0), test_subtypingRelationEdges.get(1));
        this._diagram.disconnectSubtypingConstraintRelation(test_constraints.get(1), test_subtypingRelationEdges.get(2));
        this._diagram.disconnectSubtypingConstraintRelation(test_constraints.get(1), test_subtypingRelationEdges.get(3));

        for (int i=0; i<4; i++) { this._diagram.undoState(); }
        for (int i=0; i<4; i++) { this._diagram.redoState(); }

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
        for (Constraint constraint : test_constraints) { test_addLogicErrorTo(constraint, new ConstraintHasNotEnoughConnectsLogicError(constraint, constraint.getIncidentElements(Edge.class)
                .collect(Collectors.toCollection(ArrayList::new)))); }
    }

    @Test
    void subtypingConstraintRelation_disconnectAfterUndo() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        test_addDiagramElement(this._diagram.addNode(new ExclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new InclusiveOrConstraint()));

        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(4).centerAnchorPoint()));

        this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_subtypingRelationEdges.get(0).anchorPoint());
        this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_subtypingRelationEdges.get(1).anchorPoint());
        test_addDiagramElement(this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_subtypingRelationEdges.get(2).anchorPoint()));
        this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_subtypingRelationEdges.get(3).anchorPoint());

        this._diagram.disconnectSubtypingConstraintRelation(test_constraints.get(0), test_subtypingRelationEdges.get(0));
        this._diagram.disconnectSubtypingConstraintRelation(test_constraints.get(0), test_subtypingRelationEdges.get(1));
        this._diagram.disconnectSubtypingConstraintRelation(test_constraints.get(1), test_subtypingRelationEdges.get(2));
        this._diagram.disconnectSubtypingConstraintRelation(test_constraints.get(1), test_subtypingRelationEdges.get(3));

        this._diagram.undoState();
        this._diagram.undoState();

        this._diagram.disconnectSubtypingConstraintRelation(test_constraints.get(1), test_subtypingRelationEdges.get(3));

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
        test_addLogicErrorTo(test_constraints.get(0), new ConstraintHasNotEnoughConnectsLogicError(test_constraints.get(0), test_constraints.get(0).getIncidentElements(Edge.class).collect(Collectors.toCollection(ArrayList::new))));
        test_addLogicErrorTo(test_constraints.get(1), new ConstraintHasNotEnoughConnectsLogicError(test_constraints.get(1), test_constraints.get(1).getIncidentElements(Edge.class).collect(Collectors.toCollection(ArrayList::new))));
    }

    // * Role constraint relation
    @Test
    void roleConstraintRelation_disconnect() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new SubsetConstraint()));
        test_addDiagramElement(this._diagram.addNode(new InclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new UniquenessConstraint()));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(3))));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0, 1));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(1, 2));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(0, 1, 2));
        test_addDiagramElement(test_predicates.get(2).rolesSequence(0, 1, 2));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(1));
        test_addDiagramElement(test_predicates.get(2).rolesSequence(2));

        ArrayList<RoleConstraintRelationEdge> roleConstraintRelationEdges = new ArrayList<>();
        roleConstraintRelationEdges.add(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0, 1)));
        roleConstraintRelationEdges.add(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(1).rolesSequence(1, 2)));
        roleConstraintRelationEdges.add(this._diagram.connectByRoleConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_predicates.get(1).rolesSequence(0, 1, 2)));
        roleConstraintRelationEdges.add(this._diagram.connectByRoleConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_predicates.get(2).rolesSequence(0, 1, 2)));
        roleConstraintRelationEdges.add(this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0)));
        roleConstraintRelationEdges.add(this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(1).rolesSequence(1)));
        roleConstraintRelationEdges.add(this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(2).rolesSequence(2)));

        this._diagram.disconnectRoleConstraintRelation(test_constraints.get(0), test_predicates.get(0).rolesSequence(0, 1));
        this._diagram.disconnectRoleConstraintRelation(test_constraints.get(0), test_predicates.get(1).rolesSequence(1, 2));
        this._diagram.disconnectRoleConstraintRelation(test_constraints.get(1), test_predicates.get(1).rolesSequence(0, 1, 2));
        this._diagram.disconnectRoleConstraintRelation(test_constraints.get(1), test_predicates.get(2).rolesSequence(0, 1, 2));
        this._diagram.disconnectRoleConstraintRelation(test_constraints.get(2), test_predicates.get(0).rolesSequence(0));
        this._diagram.disconnectRoleConstraintRelation(test_constraints.get(2), test_predicates.get(1).rolesSequence(1));
        this._diagram.disconnectRoleConstraintRelation(test_constraints.get(2), test_predicates.get(2).rolesSequence(2));

        // Check result
        Assertions.assertFalse(roleConstraintRelationEdges.get(0).isEndingEdge());
        Assertions.assertTrue(roleConstraintRelationEdges.get(1).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> roleConstraintRelationEdges.get(2).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> roleConstraintRelationEdges.get(3).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> roleConstraintRelationEdges.get(4).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> roleConstraintRelationEdges.get(5).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> roleConstraintRelationEdges.get(6).isEndingEdge());

        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
        for (Constraint constraint : test_constraints) { test_addLogicErrorTo(constraint, new ConstraintHasNotEnoughConnectsLogicError(constraint, constraint.getIncidentElements(Edge.class)
                .collect(Collectors.toCollection(ArrayList::new)))); }
    }

    @Test
    void roleConstraintRelation_undoDisconnect() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new SubsetConstraint()));
        test_addDiagramElement(this._diagram.addNode(new InclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new UniquenessConstraint()));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(3))));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0, 1));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(1, 2));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(0, 1, 2));
        test_addDiagramElement(test_predicates.get(2).rolesSequence(0, 1, 2));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(1));
        test_addDiagramElement(test_predicates.get(2).rolesSequence(2));

        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0, 1)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(1).rolesSequence(1, 2)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_predicates.get(1).rolesSequence(0, 1, 2)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_predicates.get(2).rolesSequence(0, 1, 2)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(1).rolesSequence(1)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(2).rolesSequence(2)));

        this._diagram.disconnectRoleConstraintRelation(test_constraints.get(0), test_predicates.get(0).rolesSequence(0, 1));
        this._diagram.disconnectRoleConstraintRelation(test_constraints.get(0), test_predicates.get(1).rolesSequence(1, 2));
        this._diagram.disconnectRoleConstraintRelation(test_constraints.get(1), test_predicates.get(1).rolesSequence(0, 1, 2));
        this._diagram.disconnectRoleConstraintRelation(test_constraints.get(1), test_predicates.get(2).rolesSequence(0, 1, 2));
        this._diagram.disconnectRoleConstraintRelation(test_constraints.get(2), test_predicates.get(0).rolesSequence(0));
        this._diagram.disconnectRoleConstraintRelation(test_constraints.get(2), test_predicates.get(1).rolesSequence(1));
        this._diagram.disconnectRoleConstraintRelation(test_constraints.get(2), test_predicates.get(2).rolesSequence(2));

        for (int i=0; i<7; i++) { this._diagram.undoState(); }

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
    }

    @Test
    void roleConstraintRelation_redoDisconnect() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new SubsetConstraint()));
        test_addDiagramElement(this._diagram.addNode(new InclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new UniquenessConstraint()));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(3))));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0, 1));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(1, 2));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(0, 1, 2));
        test_addDiagramElement(test_predicates.get(2).rolesSequence(0, 1, 2));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(1));
        test_addDiagramElement(test_predicates.get(2).rolesSequence(2));

        ArrayList<RoleConstraintRelationEdge> roleConstraintRelationEdges = new ArrayList<>();
        roleConstraintRelationEdges.add(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0, 1)));
        roleConstraintRelationEdges.add(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(1).rolesSequence(1, 2)));
        roleConstraintRelationEdges.add(this._diagram.connectByRoleConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_predicates.get(1).rolesSequence(0, 1, 2)));
        roleConstraintRelationEdges.add(this._diagram.connectByRoleConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_predicates.get(2).rolesSequence(0, 1, 2)));
        roleConstraintRelationEdges.add(this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0)));
        roleConstraintRelationEdges.add(this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(1).rolesSequence(1)));
        roleConstraintRelationEdges.add(this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(2).rolesSequence(2)));

        this._diagram.disconnectRoleConstraintRelation(test_constraints.get(0), test_predicates.get(0).rolesSequence(0, 1));
        this._diagram.disconnectRoleConstraintRelation(test_constraints.get(0), test_predicates.get(1).rolesSequence(1, 2));
        this._diagram.disconnectRoleConstraintRelation(test_constraints.get(1), test_predicates.get(1).rolesSequence(0, 1, 2));
        this._diagram.disconnectRoleConstraintRelation(test_constraints.get(1), test_predicates.get(2).rolesSequence(0, 1, 2));
        this._diagram.disconnectRoleConstraintRelation(test_constraints.get(2), test_predicates.get(0).rolesSequence(0));
        this._diagram.disconnectRoleConstraintRelation(test_constraints.get(2), test_predicates.get(1).rolesSequence(1));
        this._diagram.disconnectRoleConstraintRelation(test_constraints.get(2), test_predicates.get(2).rolesSequence(2));

        for (int i=0; i<7; i++) {
            this._diagram.undoState();
        }

        for (int i=0; i<7; i++) { this._diagram.redoState(); }

        // Check result
        Assertions.assertFalse(roleConstraintRelationEdges.get(0).isEndingEdge());
        Assertions.assertTrue(roleConstraintRelationEdges.get(1).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> roleConstraintRelationEdges.get(2).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> roleConstraintRelationEdges.get(3).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> roleConstraintRelationEdges.get(4).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> roleConstraintRelationEdges.get(5).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> roleConstraintRelationEdges.get(6).isEndingEdge());

        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
        for (Constraint constraint : test_constraints) { test_addLogicErrorTo(constraint, new ConstraintHasNotEnoughConnectsLogicError(constraint, constraint.getIncidentElements(Edge.class)
                .collect(Collectors.toCollection(ArrayList::new)))); }
    }

    @Test
    void roleConstraintRelation_disconnectAfterUndo() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new SubsetConstraint()));
        test_addDiagramElement(this._diagram.addNode(new InclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new UniquenessConstraint()));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(3))));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0, 1));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(1, 2));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(0, 1, 2));
        test_addDiagramElement(test_predicates.get(2).rolesSequence(0, 1, 2));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(1));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(0, 2));
        test_addDiagramElement(test_predicates.get(2).rolesSequence(0, 1));
        test_addDiagramElement(test_predicates.get(2).rolesSequence(2));

        this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0, 1));
        this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(1).rolesSequence(1, 2));
        this._diagram.connectByRoleConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_predicates.get(1).rolesSequence(0, 1, 2));
        this._diagram.connectByRoleConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_predicates.get(2).rolesSequence(0, 1, 2));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0)));
        this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(1).rolesSequence(1));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(2).rolesSequence(2)));

        this._diagram.disconnectRoleConstraintRelation(test_constraints.get(0), test_predicates.get(0).rolesSequence(0, 1));
        this._diagram.disconnectRoleConstraintRelation(test_constraints.get(0), test_predicates.get(1).rolesSequence(1, 2));
        this._diagram.disconnectRoleConstraintRelation(test_constraints.get(1), test_predicates.get(1).rolesSequence(0, 1, 2));
        this._diagram.disconnectRoleConstraintRelation(test_constraints.get(1), test_predicates.get(2).rolesSequence(0, 1, 2));
        this._diagram.disconnectRoleConstraintRelation(test_constraints.get(2), test_predicates.get(0).rolesSequence(0));
        this._diagram.disconnectRoleConstraintRelation(test_constraints.get(2), test_predicates.get(1).rolesSequence(1));
        this._diagram.disconnectRoleConstraintRelation(test_constraints.get(2), test_predicates.get(2).rolesSequence(2));

        for (int i=0; i<4; i++) { this._diagram.undoState(); }

        this._diagram.disconnectRoleConstraintRelation(test_constraints.get(1), test_predicates.get(2).rolesSequence(0, 1, 2));
        this._diagram.disconnectRoleConstraintRelation(test_constraints.get(2), test_predicates.get(1).rolesSequence(1));

        // Check result
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(0).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(1).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(2).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(3).isEndingEdge());

        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
        test_addLogicErrorTo(test_constraints.get(0), new ConstraintHasNotEnoughConnectsLogicError(test_constraints.get(0), test_constraints.get(0).getIncidentElements(Edge.class).collect(Collectors.toCollection(ArrayList::new))));
        test_addLogicErrorTo(test_constraints.get(1), new ConstraintHasNotEnoughConnectsLogicError(test_constraints.get(1), test_constraints.get(1).getIncidentElements(Edge.class).collect(Collectors.toCollection(ArrayList::new))));
    }

    @Test
    void roleConstraintRelation_undoDisconnectingBeginOfSubsetConstraint() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new SubsetConstraint()));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(3))));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0, 1));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(1, 2));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(0, 1, 2));

        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0, 1)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(1).rolesSequence(1, 2)));

        this._diagram.disconnectRoleConstraintRelation(test_constraints.get(0), test_predicates.get(0).rolesSequence(0, 1));
        this._diagram.undoState();

        // Check result
        Assertions.assertFalse(test_roleConstraintRelationEdges.get(0).isEndingEdge());
        Assertions.assertTrue(test_roleConstraintRelationEdges.get(1).isEndingEdge());
    }

    @Test
    void roleConstraintRelation_connectAfterDisconnectingBeginOfSubsetConstraint() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new SubsetConstraint()));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(3))));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0, 1));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(1, 2));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(0, 1, 2));

        this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0, 1));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(1).rolesSequence(1, 2)));

        this._diagram.disconnectRoleConstraintRelation(test_constraints.get(0), test_predicates.get(0).rolesSequence(0, 1));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0, 1)));

        // Check result
        Assertions.assertFalse(test_roleConstraintRelationEdges.get(0).isEndingEdge());
        Assertions.assertTrue(test_roleConstraintRelationEdges.get(1).isEndingEdge());
    }

    @Test
    void roleConstraintRelation_undoDisconnectingAndNewConnectingBeginOfSubsetConstraint() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new SubsetConstraint()));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(3))));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0, 1));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(1, 2));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(0, 1, 2));

        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0, 1)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(1).rolesSequence(1, 2)));

        this._diagram.disconnectRoleConstraintRelation(test_constraints.get(0), test_predicates.get(0).rolesSequence(0, 1));
        this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0, 1));

        this._diagram.undoState();
        this._diagram.undoState();

        // Check result
        Assertions.assertFalse(test_roleConstraintRelationEdges.get(0).isEndingEdge());
        Assertions.assertTrue(test_roleConstraintRelationEdges.get(1).isEndingEdge());
    }

    @Test
    void roleConstraintRelation_redoUndoDisconnectingAndNewConnectingBeginOfSubsetConstraint() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new SubsetConstraint()));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(3))));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0, 1));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(1, 2));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(0, 1, 2));

        this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0, 1));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(1).rolesSequence(1, 2)));

        this._diagram.disconnectRoleConstraintRelation(test_constraints.get(0), test_predicates.get(0).rolesSequence(0, 1));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0, 1)));

        this._diagram.undoState();
        this._diagram.undoState();
        this._diagram.redoState();
        this._diagram.redoState();

        // Check result
        Assertions.assertFalse(test_roleConstraintRelationEdges.get(0).isEndingEdge());
        Assertions.assertTrue(test_roleConstraintRelationEdges.get(1).isEndingEdge());
    }

    @Test
    void roleConstraintRelation_setNewConnectAfterUndoDisconnectingAndNewConnectingBeginOfSubsetConstraint() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new SubsetConstraint()));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(3))));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0, 1));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(1, 2));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(0, 1, 2));

        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0, 1)));
        this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(1).rolesSequence(1, 2));

        this._diagram.disconnectRoleConstraintRelation(test_constraints.get(0), test_predicates.get(0).rolesSequence(0, 1));
        this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0, 1));

        this._diagram.undoState();
        this._diagram.undoState();

        this._diagram.disconnectRoleConstraintRelation(test_constraints.get(0), test_predicates.get(1).rolesSequence(1, 2));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(1).rolesSequence(1, 2)));

        // Check result
        Assertions.assertFalse(test_roleConstraintRelationEdges.get(0).isEndingEdge());
        Assertions.assertTrue(test_roleConstraintRelationEdges.get(1).isEndingEdge());
    }

    // * Complex
    @Test
    void disconnect_complex() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new ValueType()));

        test_addDiagramElement(this._diagram.addNode(new SubsetConstraint()));
        test_addDiagramElement(this._diagram.addNode(new InclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new UniquenessConstraint()));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(3))));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0, 1));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(1, 2));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(0, 2));
        test_addDiagramElement(test_predicates.get(2).rolesSequence(0, 1));

        test_predicates.get(1).setOrientation(DiagramElement.Orientation.VERTICAL);

        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint()));
        SubtypingRelationEdge subtypingRelationEdge = this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint());

        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(0).getRole(0).anchorPoint(AnchorPosition.LEFT), test_entityTypes.get(0).centerAnchorPoint()));
        this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(1).anchorPoint(AnchorPosition.LEFT), test_valueTypes.get(0).centerAnchorPoint());

        this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_subtypingRelationEdges.get(0).anchorPoint());
        this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(1).centerAnchorPoint(), subtypingRelationEdge.anchorPoint());

        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0, 1)));
        this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(1).rolesSequence(1, 2));
        this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(1).rolesSequence(0, 2));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(2).rolesSequence(0, 1)));

        this._diagram.disconnectRelation(test_entityTypes.get(0), test_entityTypes.get(2));
        this._diagram.disconnectRelation(test_predicates.get(1).getRole(1), test_valueTypes.get(0));
        this._diagram.disconnectRelation(test_constraints.get(1), test_subtypingRelationEdges.get(0));
        this._diagram.disconnectRelation(test_constraints.get(0), test_predicates.get(1).rolesSequence(1, 2));
        this._diagram.disconnectRelation(test_constraints.get(2), test_predicates.get(1).rolesSequence(0, 2));

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
        for (ValueType valueType : test_valueTypes) { test_addLogicErrorTo(valueType, new ValueTypeWithNoneDataTypeLogicError(valueType)); }
        test_addLogicErrorTo(test_constraints.get(0), new ConstraintHasNotEnoughConnectsLogicError(test_constraints.get(0), test_constraints.get(0).getIncidentElements(Edge.class).collect(Collectors.toCollection(ArrayList::new))));
        test_addLogicErrorTo(test_constraints.get(1), new ConstraintHasNotEnoughConnectsLogicError(test_constraints.get(1), test_constraints.get(1).getIncidentElements(Edge.class).collect(Collectors.toCollection(ArrayList::new))));
    }

    // ---------------- RECONNECTION ----------------
    // * Subtyping relation
    @Test
    void subtypingRelation_reconnect() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        SubtypingRelationEdge edge0 = this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint());
        SubtypingRelationEdge edge1 = this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint());
        SubtypingRelationEdge edge2 = this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint());

        this._diagram.reconnectSubtypingRelation(edge0, test_entityTypes.get(4).centerAnchorPoint());
        test_addDiagramElement(this._diagram.reconnectSubtypingRelation(edge1, test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.reconnectSubtypingRelation(test_entityTypes.get(2).centerAnchorPoint(), edge2));
        test_addDiagramElement(this._diagram.reconnectSubtypingRelation(test_entityTypes.get(3).centerAnchorPoint(), edge0));

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
    }

    @Test
    void actionError_subtypingRelation_reconnectToSelf() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        SubtypingRelationEdge edge0 = this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint());
        SubtypingRelationEdge edge1 = this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint());
        SubtypingRelationEdge edge2 = this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint());

        this._diagram.reconnectSubtypingRelation(edge0, test_entityTypes.get(4).centerAnchorPoint());
        test_addDiagramElement(this._diagram.reconnectSubtypingRelation(edge1, test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.reconnectSubtypingRelation(test_entityTypes.get(2).centerAnchorPoint(), edge2));
        test_addDiagramElement(this._diagram.reconnectSubtypingRelation(test_entityTypes.get(3).centerAnchorPoint(), edge0));

        this._diagram.reconnectSubtypingRelation(test_entityTypes.get(4).centerAnchorPoint(), edge0);

        // Check result
        test_actionErrors.add(new DiagramElementSelfConnectedActionError<>(test_entityTypes.get(4)));
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
    }

    @Test
    void subtypingRelation_undoReconnect() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint()));

        this._diagram.reconnectSubtypingRelation(test_subtypingRelationEdges.get(0), test_entityTypes.get(4).centerAnchorPoint());
        this._diagram.reconnectSubtypingRelation(test_subtypingRelationEdges.get(1), test_entityTypes.get(1).centerAnchorPoint());
        this._diagram.reconnectSubtypingRelation(test_entityTypes.get(2).centerAnchorPoint(), test_subtypingRelationEdges.get(2));
        this._diagram.reconnectSubtypingRelation(test_entityTypes.get(3).centerAnchorPoint(), test_subtypingRelationEdges.get(0));

        for (int i=0; i<4; i++) { this._diagram.undoState(); }

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
    }

    @Test
    void subtypingRelation_redoReconnect() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        SubtypingRelationEdge edge0 = this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint());
        SubtypingRelationEdge edge1 = this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint());
        SubtypingRelationEdge edge2 = this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint());

        this._diagram.reconnectSubtypingRelation(edge0, test_entityTypes.get(4).centerAnchorPoint());
        test_addDiagramElement(this._diagram.reconnectSubtypingRelation(edge1, test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.reconnectSubtypingRelation(test_entityTypes.get(2).centerAnchorPoint(), edge2));
        test_addDiagramElement(this._diagram.reconnectSubtypingRelation(test_entityTypes.get(3).centerAnchorPoint(), edge0));

        for (int i=0; i<4; i++) { this._diagram.undoState(); }
        for (int i=0; i<4; i++) { this._diagram.redoState(); }

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
    }

    @Test
    void subtypingRelation_reconnectAfterUndo() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        SubtypingRelationEdge edge0 = this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint());
        SubtypingRelationEdge edge1 = this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint());
        SubtypingRelationEdge edge2 = this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint());

        this._diagram.reconnectSubtypingRelation(edge0, test_entityTypes.get(4).centerAnchorPoint());
        test_addDiagramElement(this._diagram.reconnectSubtypingRelation(edge1, test_entityTypes.get(1).centerAnchorPoint()));
        this._diagram.reconnectSubtypingRelation(test_entityTypes.get(2).centerAnchorPoint(), edge2);
        this._diagram.reconnectSubtypingRelation(test_entityTypes.get(3).centerAnchorPoint(), edge0);

        this._diagram.undoState();
        this._diagram.undoState();

        test_addDiagramElement(this._diagram.reconnectSubtypingRelation(edge2, test_entityTypes.get(2).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.reconnectSubtypingRelation(edge0, test_entityTypes.get(3).centerAnchorPoint()));

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
    }

    // * Role relation
    @Test
    void roleRelation_reconnect() {
        // Prepare data and start testing
        for (int i=0; i<3; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        for (int i=0; i<3; i++) { test_addDiagramElement(this._diagram.addNode(new ValueType())); }
        for (int i=0; i<3; i++) { test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(2)))); }

        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));

        RoleRelationEdge edge0 = this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(0).centerAnchorPoint());
        RoleRelationEdge edge1 = this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(1).anchorPoint(AnchorPosition.UP),    test_entityTypes.get(1).centerAnchorPoint());
        RoleRelationEdge edge2 = this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(2).anchorPoint(AnchorPosition.DOWN),  test_valueTypes.get(1).centerAnchorPoint());
        RoleRelationEdge edge3 = this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(3).anchorPoint(AnchorPosition.RIGHT), test_objectifiedPredicates.get(0).leftAnchorPoint());
        RoleRelationEdge edge4 = this._diagram.connectByRoleRelation(test_predicates.get(0).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(2).centerAnchorPoint());
        RoleRelationEdge edge5 = this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(1).anchorPoint(AnchorPosition.RIGHT), test_objectifiedPredicates.get(2).leftAnchorPoint());

        test_addDiagramElement(this._diagram.reconnectRoleRelation(edge0, test_valueTypes.get(0).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.reconnectRoleRelation(test_predicates.get(1).getRole(0).anchorPoint(AnchorPosition.LEFT), edge1));
        test_addDiagramElement(this._diagram.reconnectRoleRelation(edge2, test_valueTypes.get(2).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.reconnectRoleRelation(test_predicates.get(3).getRole(1).anchorPoint(AnchorPosition.UP), edge3));
        test_addDiagramElement(this._diagram.reconnectRoleRelation(edge4, test_entityTypes.get(0).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.reconnectRoleRelation(test_predicates.get(0).getRole(1).anchorPoint(AnchorPosition.RIGHT), edge5));

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
        for (ValueType valueType : test_valueTypes) { test_addLogicErrorTo(valueType, new ValueTypeWithNoneDataTypeLogicError(valueType)); }
    }

    @Test
    void roleRelation_undoReconnect() {
        // Prepare data and start testing
        for (int i=0; i<3; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        for (int i=0; i<3; i++) { test_addDiagramElement(this._diagram.addNode(new ValueType())); }
        for (int i=0; i<3; i++) { test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(2)))); }

        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));

        this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(0).centerAnchorPoint());
        this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(1).anchorPoint(AnchorPosition.UP),    test_entityTypes.get(1).centerAnchorPoint());
        this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(2).anchorPoint(AnchorPosition.DOWN),  test_valueTypes.get(1).centerAnchorPoint());
        this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(3).anchorPoint(AnchorPosition.RIGHT), test_objectifiedPredicates.get(0).leftAnchorPoint());

        this._diagram.connectByRoleRelation(test_predicates.get(0).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(2).centerAnchorPoint());
        this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(1).anchorPoint(AnchorPosition.RIGHT), test_objectifiedPredicates.get(2).leftAnchorPoint());

        for (int i=0; i<6; i++) { this._diagram.undoState(); }

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
        for (ValueType valueType : test_valueTypes) { test_addLogicErrorTo(valueType, new ValueTypeWithNoneDataTypeLogicError(valueType)); }
    }

    @Test
    void roleRelation_redoReconnect() {
        // Prepare data and start testing
        for (int i=0; i<3; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        for (int i=0; i<3; i++) { test_addDiagramElement(this._diagram.addNode(new ValueType())); }
        for (int i=0; i<3; i++) { test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(2)))); }

        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));

        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(0).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(1).anchorPoint(AnchorPosition.UP),    test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(2).anchorPoint(AnchorPosition.DOWN),  test_valueTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(3).anchorPoint(AnchorPosition.RIGHT), test_objectifiedPredicates.get(0).leftAnchorPoint()));

        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(0).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(2).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(1).anchorPoint(AnchorPosition.RIGHT), test_objectifiedPredicates.get(2).leftAnchorPoint()));

        for (int i=0; i<6; i++) { this._diagram.undoState(); }
        for (int i=0; i<6; i++) { this._diagram.redoState(); }

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
        for (ValueType valueType : test_valueTypes) { test_addLogicErrorTo(valueType, new ValueTypeWithNoneDataTypeLogicError(valueType)); }
    }

    @Test
    void roleRelation_reconnectAfterUndo() {
        // Prepare data and start testing
        for (int i=0; i<3; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        for (int i=0; i<3; i++) { test_addDiagramElement(this._diagram.addNode(new ValueType())); }
        for (int i=0; i<3; i++) { test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(2)))); }

        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));

        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(0).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(1).anchorPoint(AnchorPosition.UP),    test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(2).anchorPoint(AnchorPosition.DOWN),  test_valueTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(3).anchorPoint(AnchorPosition.RIGHT), test_objectifiedPredicates.get(0).leftAnchorPoint()));

        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(0).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(2).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(1).anchorPoint(AnchorPosition.RIGHT), test_objectifiedPredicates.get(2).leftAnchorPoint()));

        for (int i=0; i<6; i++) { this._diagram.undoState(); }
        for (int i=0; i<6; i++) { this._diagram.redoState(); }

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
        for (ValueType valueType : test_valueTypes) { test_addLogicErrorTo(valueType, new ValueTypeWithNoneDataTypeLogicError(valueType)); }
    }

    @Test
    void roleRelation_objectifiedPredicatesReconnectsToInnerPredicate() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(2))));
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(4))));

        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(0).anchorPoint(AnchorPosition.LEFT), test_objectifiedPredicates.get(0).upAnchorPoint()));

        this._diagram.reconnectRoleRelation(test_predicates.get(0).getRole(0).anchorPoint(AnchorPosition.LEFT), test_roleRelationEdges.get(0));
        this._diagram.reconnectRoleRelation(test_roleRelationEdges.get(0), test_objectifiedPredicates.get(1).upAnchorPoint());

        // Check result
        test_actionErrors.add(new ObjectifiedPredicateIsConnectedToItsInnerPredicateActionError(test_objectifiedPredicates.get(0)));
        test_actionErrors.add(new ObjectifiedPredicateIsConnectedToItsInnerPredicateActionError(test_objectifiedPredicates.get(1)));
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
    }

    @Test
    void roleRelation_objectifiedPredicatesReconnectsToAnotherPersonalAnchorPoint() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(2))));
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(4))));

        RoleRelationEdge edge0 = this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(0).anchorPoint(AnchorPosition.LEFT), test_objectifiedPredicates.get(0).upAnchorPoint());
        test_addDiagramElement(this._diagram.reconnectRoleRelation(edge0, test_objectifiedPredicates.get(0).upAnchorPoint()));

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
    }

    // * Subtyping constraint relation
    @Test
    void subtypingConstraintRelation_reconnect() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        test_addDiagramElement(this._diagram.addNode(new ExclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new InclusiveOrConstraint()));

        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(4).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(2).centerAnchorPoint(), test_entityTypes.get(4).centerAnchorPoint()));

        SubtypingConstraintRelationEdge edge0 = this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_subtypingRelationEdges.get(0).anchorPoint());
        SubtypingConstraintRelationEdge edge1 = this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_subtypingRelationEdges.get(1).anchorPoint());
        SubtypingConstraintRelationEdge edge2 = this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_subtypingRelationEdges.get(2).anchorPoint());
        SubtypingConstraintRelationEdge edge3 = this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_subtypingRelationEdges.get(3).anchorPoint());

        test_addDiagramElement(this._diagram.reconnectSubtypingConstraintRelation(edge0, test_subtypingRelationEdges.get(4).anchorPoint()));
        test_addDiagramElement(this._diagram.reconnectSubtypingConstraintRelation(test_constraints.get(1).centerAnchorPoint(), edge1));
        test_addDiagramElement(this._diagram.reconnectSubtypingConstraintRelation(edge2, test_subtypingRelationEdges.get(0).anchorPoint()));
        test_addDiagramElement(this._diagram.reconnectSubtypingConstraintRelation(test_constraints.get(0).centerAnchorPoint(), edge3));

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
    }

    @Test
    void subtypingConstraintRelation_undoReconnect() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        test_addDiagramElement(this._diagram.addNode(new ExclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new InclusiveOrConstraint()));

        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(4).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(2).centerAnchorPoint(), test_entityTypes.get(4).centerAnchorPoint()));

        test_addDiagramElement(this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_subtypingRelationEdges.get(0).anchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_subtypingRelationEdges.get(1).anchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_subtypingRelationEdges.get(2).anchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_subtypingRelationEdges.get(3).anchorPoint()));

        this._diagram.reconnectSubtypingConstraintRelation(test_subtypingConstraintRelationEdges.get(0), test_subtypingRelationEdges.get(4).anchorPoint());
        this._diagram.reconnectSubtypingConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_subtypingConstraintRelationEdges.get(1));
        this._diagram.reconnectSubtypingConstraintRelation(test_subtypingConstraintRelationEdges.get(2), test_subtypingRelationEdges.get(0).anchorPoint());
        this._diagram.reconnectSubtypingConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_subtypingConstraintRelationEdges.get(3));

        for (int i=0; i<4; i++) { this._diagram.undoState(); }

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
    }

    @Test
    void subtypingConstraintRelation_redoReconnect() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        test_addDiagramElement(this._diagram.addNode(new ExclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new InclusiveOrConstraint()));

        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(4).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(2).centerAnchorPoint(), test_entityTypes.get(4).centerAnchorPoint()));

        SubtypingConstraintRelationEdge edge0 = this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_subtypingRelationEdges.get(0).anchorPoint());
        SubtypingConstraintRelationEdge edge1 = this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_subtypingRelationEdges.get(1).anchorPoint());
        SubtypingConstraintRelationEdge edge2 = this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_subtypingRelationEdges.get(2).anchorPoint());
        SubtypingConstraintRelationEdge edge3 = this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_subtypingRelationEdges.get(3).anchorPoint());

        test_addDiagramElement(this._diagram.reconnectSubtypingConstraintRelation(edge0, test_subtypingRelationEdges.get(4).anchorPoint()));
        test_addDiagramElement(this._diagram.reconnectSubtypingConstraintRelation(test_constraints.get(1).centerAnchorPoint(), edge1));
        test_addDiagramElement(this._diagram.reconnectSubtypingConstraintRelation(edge2, test_subtypingRelationEdges.get(0).anchorPoint()));
        test_addDiagramElement(this._diagram.reconnectSubtypingConstraintRelation(test_constraints.get(0).centerAnchorPoint(), edge3));

        for (int i=0; i<4; i++) { this._diagram.undoState(); }
        for (int i=0; i<4; i++) { this._diagram.redoState(); }

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
    }

    @Test
    void subtypingConstraintRelation_reconnectAfterUndo() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        test_addDiagramElement(this._diagram.addNode(new ExclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new InclusiveOrConstraint()));

        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(4).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(2).centerAnchorPoint(), test_entityTypes.get(4).centerAnchorPoint()));

        SubtypingConstraintRelationEdge edge0 = this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_subtypingRelationEdges.get(0).anchorPoint());
        SubtypingConstraintRelationEdge edge1 = this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_subtypingRelationEdges.get(1).anchorPoint());
        SubtypingConstraintRelationEdge edge2 = this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_subtypingRelationEdges.get(2).anchorPoint());
        SubtypingConstraintRelationEdge edge3 = test_addDiagramElement(this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_subtypingRelationEdges.get(3).anchorPoint()));

        test_addDiagramElement(this._diagram.reconnectSubtypingConstraintRelation(edge0, test_subtypingRelationEdges.get(4).anchorPoint()));
        test_addDiagramElement(this._diagram.reconnectSubtypingConstraintRelation(test_constraints.get(1).centerAnchorPoint(), edge1));
        this._diagram.reconnectSubtypingConstraintRelation(edge2, test_subtypingRelationEdges.get(0).anchorPoint());
        this._diagram.reconnectSubtypingConstraintRelation(test_constraints.get(0).centerAnchorPoint(), edge3);

        for (int i=0; i<2; i++) { this._diagram.undoState(); }

        test_addDiagramElement(this._diagram.reconnectSubtypingConstraintRelation(edge2, test_subtypingRelationEdges.get(2).anchorPoint()));

        // Check result
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
        test_addLogicErrorTo(test_constraints.get(0), new ConstraintHasNotEnoughConnectsLogicError(test_constraints.get(0), test_constraints.get(0).getIncidentElements(Edge.class)
                .collect(Collectors.toCollection(ArrayList::new))));
    }

    // * Role constraint relation
    @Test
    void roleConstraintRelation_reconnect() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new SubsetConstraint()));
        test_addDiagramElement(this._diagram.addNode(new InclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new UniquenessConstraint()));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(3))));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0, 1));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0, 2));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(1, 2));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(0, 1, 2));
        test_addDiagramElement(test_predicates.get(2).rolesSequence(0, 1, 2));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(1));
        test_addDiagramElement(test_predicates.get(2).rolesSequence(2));

        RoleConstraintRelationEdge edge0 = this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0, 1));
        RoleConstraintRelationEdge edge1 = this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(1).rolesSequence(1, 2));
        RoleConstraintRelationEdge edge2 = this._diagram.connectByRoleConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_predicates.get(1).rolesSequence(0, 1, 2));
        RoleConstraintRelationEdge edge3 = this._diagram.connectByRoleConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_predicates.get(2).rolesSequence(0, 1, 2));
        RoleConstraintRelationEdge edge4 = this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0));
        RoleConstraintRelationEdge edge5 = this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(1).rolesSequence(1));
        RoleConstraintRelationEdge edge6 = this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(2).rolesSequence(2));

        test_addDiagramElement(this._diagram.reconnectRoleConstraintRelation(edge0, test_predicates.get(0).rolesSequence(0, 2)));
        test_addDiagramElement(this._diagram.reconnectRoleConstraintRelation(test_constraints.get(1).centerAnchorPoint(), edge1));
        test_addDiagramElement(this._diagram.reconnectRoleConstraintRelation(edge2, test_predicates.get(1).rolesSequence(0, 1, 2)));
        test_addDiagramElement(this._diagram.reconnectRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), edge3));
        test_addDiagramElement(this._diagram.reconnectRoleConstraintRelation(edge4, test_predicates.get(0).rolesSequence(0)));
        test_addDiagramElement(this._diagram.reconnectRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), edge5));
        test_addDiagramElement(this._diagram.reconnectRoleConstraintRelation(edge6, test_predicates.get(2).rolesSequence(2)));

        // Check result
        Assertions.assertFalse(test_roleConstraintRelationEdges.get(0).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(1).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(2).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(3).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(4).isEndingEdge());
        Assertions.assertTrue(test_roleConstraintRelationEdges.get(5).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(6).isEndingEdge());

        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }

        ArrayList<LogicError> logicErrors = new ArrayList<>();
        logicErrors.add(new TwoOrMoreRolesSequencesWithIncompatibleSizeAreConnectedLogicError(test_constraints.get(0), new ArrayList<>(List.of(edge0, edge5))));
        logicErrors.add(new TwoOrMoreRolesSequencesWithIncompatibleSizeAreConnectedLogicError(test_constraints.get(2), new ArrayList<>(List.of(edge3, edge4, edge6))));

        test_addLogicErrorTo(test_constraints.get(0), logicErrors.get(0));
        test_addLogicErrorTo(edge0, logicErrors.get(0));
        test_addLogicErrorTo(edge5, logicErrors.get(0));

        test_addLogicErrorTo(test_constraints.get(2), logicErrors.get(1));
        test_addLogicErrorTo(edge3, logicErrors.get(1));
        test_addLogicErrorTo(edge4, logicErrors.get(1));
        test_addLogicErrorTo(edge6, logicErrors.get(1));
    }

    @Test
    void roleConstraintRelation_undoReconnect() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new SubsetConstraint()));
        test_addDiagramElement(this._diagram.addNode(new InclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new UniquenessConstraint()));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(3))));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0, 1));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0, 2));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(1, 2));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(0, 1, 2));
        test_addDiagramElement(test_predicates.get(2).rolesSequence(0, 1, 2));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(1));
        test_addDiagramElement(test_predicates.get(2).rolesSequence(2));

        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0, 1)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(1).rolesSequence(1, 2)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_predicates.get(1).rolesSequence(0, 1, 2)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_predicates.get(2).rolesSequence(0, 1, 2)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(1).rolesSequence(1)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(2).rolesSequence(2)));

        this._diagram.reconnectRoleConstraintRelation(test_roleConstraintRelationEdges.get(0), test_predicates.get(0).rolesSequence(0, 2));
        this._diagram.reconnectRoleConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_roleConstraintRelationEdges.get(1));
        this._diagram.reconnectRoleConstraintRelation(test_roleConstraintRelationEdges.get(2), test_predicates.get(1).rolesSequence(0, 1, 2));
        this._diagram.reconnectRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_roleConstraintRelationEdges.get(3));
        this._diagram.reconnectRoleConstraintRelation(test_roleConstraintRelationEdges.get(4), test_predicates.get(0).rolesSequence(0));
        this._diagram.reconnectRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_roleConstraintRelationEdges.get(5));
        this._diagram.reconnectRoleConstraintRelation(test_roleConstraintRelationEdges.get(6), test_predicates.get(2).rolesSequence(2));

        for (int i=0; i<7; i++) { this._diagram.undoState(); }

        // Check result
        Assertions.assertFalse(test_roleConstraintRelationEdges.get(0).isEndingEdge());
        Assertions.assertTrue(test_roleConstraintRelationEdges.get(1).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(2).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(3).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(4).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(5).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(6).isEndingEdge());

        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
    }

    @Test
    void roleConstraintRelation_redoReconnect() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new SubsetConstraint()));
        test_addDiagramElement(this._diagram.addNode(new InclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new UniquenessConstraint()));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(3))));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0, 1));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0, 2));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(1, 2));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(0, 1, 2));
        test_addDiagramElement(test_predicates.get(2).rolesSequence(0, 1, 2));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(1));
        test_addDiagramElement(test_predicates.get(2).rolesSequence(2));

        RoleConstraintRelationEdge edge0 = this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0, 1));
        RoleConstraintRelationEdge edge1 = this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(1).rolesSequence(1, 2));
        RoleConstraintRelationEdge edge2 = this._diagram.connectByRoleConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_predicates.get(1).rolesSequence(0, 1, 2));
        RoleConstraintRelationEdge edge3 = this._diagram.connectByRoleConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_predicates.get(2).rolesSequence(0, 1, 2));
        RoleConstraintRelationEdge edge4 = this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0));
        RoleConstraintRelationEdge edge5 = this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(1).rolesSequence(1));
        RoleConstraintRelationEdge edge6 = this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(2).rolesSequence(2));

        test_addDiagramElement(this._diagram.reconnectRoleConstraintRelation(edge0, test_predicates.get(0).rolesSequence(0, 2)));
        test_addDiagramElement(this._diagram.reconnectRoleConstraintRelation(test_constraints.get(1).centerAnchorPoint(), edge1));
        test_addDiagramElement(this._diagram.reconnectRoleConstraintRelation(edge2, test_predicates.get(1).rolesSequence(0, 1, 2)));
        test_addDiagramElement(this._diagram.reconnectRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), edge3));
        test_addDiagramElement(this._diagram.reconnectRoleConstraintRelation(edge4, test_predicates.get(0).rolesSequence(0)));
        test_addDiagramElement(this._diagram.reconnectRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), edge5));
        test_addDiagramElement(this._diagram.reconnectRoleConstraintRelation(edge6, test_predicates.get(2).rolesSequence(2)));

        for (int i=0; i<7; i++) { this._diagram.undoState(); }
        for (int i=0; i<7; i++) { this._diagram.redoState(); }

        // Check result
        Assertions.assertFalse(test_roleConstraintRelationEdges.get(0).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(1).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(2).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(3).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(4).isEndingEdge());
        Assertions.assertTrue(test_roleConstraintRelationEdges.get(5).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(6).isEndingEdge());

        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }

        ArrayList<LogicError> logicErrors = new ArrayList<>();
        logicErrors.add(new TwoOrMoreRolesSequencesWithIncompatibleSizeAreConnectedLogicError(test_constraints.get(0), new ArrayList<>(List.of(edge0, edge5))));
        logicErrors.add(new TwoOrMoreRolesSequencesWithIncompatibleSizeAreConnectedLogicError(test_constraints.get(2), new ArrayList<>(List.of(edge3, edge4, edge6))));

        test_addLogicErrorTo(test_constraints.get(0), logicErrors.get(0));
        test_addLogicErrorTo(edge0, logicErrors.get(0));
        test_addLogicErrorTo(edge5, logicErrors.get(0));

        test_addLogicErrorTo(test_constraints.get(2), logicErrors.get(1));
        test_addLogicErrorTo(edge3, logicErrors.get(1));
        test_addLogicErrorTo(edge4, logicErrors.get(1));
        test_addLogicErrorTo(edge6, logicErrors.get(1));
    }

    @Test
    void roleConstraintRelation_reconnectAfterUndo() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new SubsetConstraint()));
        test_addDiagramElement(this._diagram.addNode(new InclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new UniquenessConstraint()));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(3))));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0, 1));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0, 2));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(1, 2));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(0, 1, 2));
        test_addDiagramElement(test_predicates.get(2).rolesSequence(0, 1, 2));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(1));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(1));
        test_addDiagramElement(test_predicates.get(2).rolesSequence(1));
        test_addDiagramElement(test_predicates.get(2).rolesSequence(2));

        RoleConstraintRelationEdge edge0 = this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0, 1));
        RoleConstraintRelationEdge edge1 = this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(1).rolesSequence(1, 2));
        RoleConstraintRelationEdge edge2 = this._diagram.connectByRoleConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_predicates.get(1).rolesSequence(0, 1, 2));
        RoleConstraintRelationEdge edge3 = test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_predicates.get(2).rolesSequence(0, 1, 2)));
        RoleConstraintRelationEdge edge4 = this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0));
        RoleConstraintRelationEdge edge5 = test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(1).rolesSequence(1)));
        RoleConstraintRelationEdge edge6 = this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(2).rolesSequence(2));

        test_addDiagramElement(this._diagram.reconnectRoleConstraintRelation(edge0, test_predicates.get(0).rolesSequence(0, 2)));
        test_addDiagramElement(this._diagram.reconnectRoleConstraintRelation(test_constraints.get(1).centerAnchorPoint(), edge1));
        test_addDiagramElement(this._diagram.reconnectRoleConstraintRelation(edge2, test_predicates.get(1).rolesSequence(0, 1, 2)));
        this._diagram.reconnectRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), edge3);
        this._diagram.reconnectRoleConstraintRelation(edge4, test_predicates.get(0).rolesSequence(0));
        this._diagram.reconnectRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), edge5);
        this._diagram.reconnectRoleConstraintRelation(edge6, test_predicates.get(2).rolesSequence(2));

        for (int i=0; i<4; i++) { this._diagram.undoState(); }

        test_addDiagramElement(this._diagram.reconnectRoleConstraintRelation(edge4, test_predicates.get(0).rolesSequence(1)));
        test_addDiagramElement(this._diagram.reconnectRoleConstraintRelation(edge6, test_predicates.get(2).rolesSequence(1)));

        // Check result
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(0).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(1).isEndingEdge());
        Assertions.assertFalse(test_roleConstraintRelationEdges.get(2).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(3).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(4).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(5).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(6).isEndingEdge());

        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }

        ArrayList<LogicError> logicErrors = new ArrayList<>();
        logicErrors.add(new ConstraintHasNotEnoughConnectsLogicError(test_constraints.get(0), test_constraints.get(0).getIncidentElements(Edge.class).collect(Collectors.toCollection(ArrayList::new))));
        logicErrors.add(new TwoOrMoreRolesSequencesWithIncompatibleSizeAreConnectedLogicError(test_constraints.get(1), new ArrayList<>(List.of(edge1, edge2, edge3))));

        test_addLogicErrorTo(test_constraints.get(0), logicErrors.get(0));
        test_addLogicErrorTo(test_constraints.get(1), logicErrors.get(1));
        test_addLogicErrorTo(edge1, logicErrors.get(1));
        test_addLogicErrorTo(edge2, logicErrors.get(1));
        test_addLogicErrorTo(edge3, logicErrors.get(1));
    }

    /*
    // * Complex
    @Test
    void connect_complex() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new ValueType()));

        test_addDiagramElement(this._diagram.addNode(new SubsetConstraint()));
        test_addDiagramElement(this._diagram.addNode(new InclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new UniquenessConstraint()));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(3))));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0, 1));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(1, 2));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(0, 2));
        test_addDiagramElement(test_predicates.get(2).rolesSequence(0, 1));

        test_predicates.get(1).setOrientation(DiagramElement.Orientation.VERTICAL);

        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint()));

        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(0).getRole(0).anchorPoint(AnchorPosition.LEFT), test_entityTypes.get(0).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(1).anchorPoint(AnchorPosition.LEFT), test_valueTypes.get(0).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(1).anchorPoint(AnchorPosition.RIGHT), test_objectifiedPredicates.get(0).rightAnchorPoint()));

        test_addDiagramElement(this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_subtypingRelationEdges.get(0).anchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_subtypingRelationEdges.get(1).anchorPoint()));

        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0, 1)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(1).rolesSequence(1, 2)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(1).rolesSequence(0, 2)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(2).centerAnchorPoint(), test_predicates.get(2).rolesSequence(0, 1)));

        // Check result
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
    }
    */

    // ------------------- COMPLEX -------------------
    // * Role constraint relation

    // TODO - @test :: connect elements from different diagrams
}
