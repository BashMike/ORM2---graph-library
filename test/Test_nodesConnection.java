import com.orm2_graph_library.action_errors.DiagramElementSelfConnectedActionError;
import com.orm2_graph_library.action_errors.DoubleConnectionActionError;
import com.orm2_graph_library.action_errors.ObjectifiedPredicateIsConnectedToItsInnerPredicateActionError;
import com.orm2_graph_library.anchor_points.AnchorPosition;
import com.orm2_graph_library.core.*;
import com.orm2_graph_library.edges.RoleConstraintRelationEdge;
import com.orm2_graph_library.edges.SubtypingRelationEdge;
import com.orm2_graph_library.logic_errors.*;
import com.orm2_graph_library.nodes.common.EntityType;
import com.orm2_graph_library.nodes.common.ValueType;
import com.orm2_graph_library.nodes.constraints.*;
import com.orm2_graph_library.nodes.predicates.ObjectifiedPredicate;
import com.orm2_graph_library.nodes.predicates.Predicate;
import com.orm2_graph_library.nodes.predicates.Role;
import com.orm2_graph_library.utils.Pair;
import org.junit.jupiter.api.*;
import utils.Test_globalTest;

import java.util.*;
import java.util.stream.Collectors;

class Test_nodesConnection extends Test_globalTest {
    // ==================== INIT ====================
    @AfterEach
    void endTest_nodesConnection() {
        for (EntityType entityType : test_entityTypes) {
            test_entityTypesLogicErrors.computeIfAbsent(entityType, k -> new HashSet<>()).add(new EntityTypeWithNoneRefModeLogicError(entityType));
        }

        for (Predicate predicate : test_predicates) {
            for (Role role : predicate.roles().collect(Collectors.toCollection(ArrayList::new))) {
                LogicError logicError = new RoleHasNoTextSetLogicError(role);

                test_predicatesLogicErrors.computeIfAbsent(role.ownerPredicate(), k -> new HashSet<>()).add(logicError);
                test_rolesLogicErrors.computeIfAbsent(role, k -> new HashSet<>()).add(logicError);
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
    }

    @Test
    void actionError_subtypingRelation_connectWithSelf() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(0).centerAnchorPoint());

        // Check result
        test_actionErrors.add(new DiagramElementSelfConnectedActionError<>(test_entityTypes.get(0)));
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
    }

    @Test
    void actionError_roleRelation_connectObjectifiedPredicateWithItInnerPredicate() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(3))));

        this._diagram.connectByRoleRelation(test_predicates.get(0).getRole(0).anchorPoint(AnchorPosition.LEFT), test_objectifiedPredicates.get(0).leftAnchorPoint());

        // Check result
        test_actionErrors.add(new ObjectifiedPredicateIsConnectedToItsInnerPredicateActionError(test_objectifiedPredicates.get(0)));
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
        for (Constraint constraint : test_constraints) { test_constraintsLogicErrors.put(constraint, new HashSet<>(List.of(new ConstraintHasNotEnoughConnectsLogicError(constraint, constraint.getIncidentElements(Edge.class)
                .collect(Collectors.toCollection(ArrayList::new)))))); }
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
        test_constraintsLogicErrors.put(test_constraints.get(1), new HashSet<>(List.of(new ConstraintHasNotEnoughConnectsLogicError(test_constraints.get(1), test_constraints.get(1).getIncidentElements(Edge.class)
                .collect(Collectors.toCollection(ArrayList::new))))));
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
        Assertions.assertFalse(test_roleConstraintRelationEdges.get(0).isEndingEdge());
        Assertions.assertTrue(test_roleConstraintRelationEdges.get(1).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(2).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(3).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(4).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(5).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(6).isEndingEdge());
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
        for (Constraint constraint : test_constraints) { test_constraintsLogicErrors.put(constraint, new HashSet<>(List.of(new ConstraintHasNotEnoughConnectsLogicError(constraint, constraint.getIncidentElements(Edge.class)
                .collect(Collectors.toCollection(ArrayList::new)))))); }
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
        Assertions.assertFalse(test_roleConstraintRelationEdges.get(0).isEndingEdge());
        Assertions.assertTrue(test_roleConstraintRelationEdges.get(1).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(2).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(3).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(4).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(5).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(6).isEndingEdge());
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
        test_constraintsLogicErrors.put(test_constraints.get(2), new HashSet<>(List.of(new ConstraintHasNotEnoughConnectsLogicError(test_constraints.get(2), test_constraints.get(2).getIncidentElements(Edge.class)
                .collect(Collectors.toCollection(ArrayList::new))))));
        Assertions.assertFalse(test_roleConstraintRelationEdges.get(0).isEndingEdge());
        Assertions.assertTrue(test_roleConstraintRelationEdges.get(1).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(2).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(3).isEndingEdge());
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
        for (Constraint constraint : test_constraints) { test_constraintsLogicErrors.put(constraint, new HashSet<>(List.of(new ConstraintHasNotEnoughConnectsLogicError(constraint, constraint.getIncidentElements(Edge.class)
                .collect(Collectors.toCollection(ArrayList::new)))))); }
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
        for (Constraint constraint : test_constraints) { test_constraintsLogicErrors.put(constraint, new HashSet<>(List.of(new ConstraintHasNotEnoughConnectsLogicError(constraint, constraint.getIncidentElements(Edge.class)
                .collect(Collectors.toCollection(ArrayList::new)))))); }
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
        test_constraintsLogicErrors.put(test_constraints.get(0), new HashSet<>(List.of(new ConstraintHasNotEnoughConnectsLogicError(test_constraints.get(0), test_constraints.get(0).getIncidentElements(Edge.class).collect(Collectors.toCollection(ArrayList::new))))));
        test_constraintsLogicErrors.put(test_constraints.get(1), new HashSet<>(List.of(new ConstraintHasNotEnoughConnectsLogicError(test_constraints.get(1), test_constraints.get(1).getIncidentElements(Edge.class).collect(Collectors.toCollection(ArrayList::new))))));
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
        Assertions.assertFalse(roleConstraintRelationEdges.get(0).isEndingEdge());
        Assertions.assertTrue(roleConstraintRelationEdges.get(1).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> roleConstraintRelationEdges.get(2).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> roleConstraintRelationEdges.get(3).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> roleConstraintRelationEdges.get(4).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> roleConstraintRelationEdges.get(5).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> roleConstraintRelationEdges.get(6).isEndingEdge());
        for (Constraint constraint : test_constraints) { test_constraintsLogicErrors.put(constraint, new HashSet<>(List.of(new ConstraintHasNotEnoughConnectsLogicError(constraint, constraint.getIncidentElements(Edge.class)
                .collect(Collectors.toCollection(ArrayList::new)))))); }
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
        Assertions.assertFalse(roleConstraintRelationEdges.get(0).isEndingEdge());
        Assertions.assertTrue(roleConstraintRelationEdges.get(1).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> roleConstraintRelationEdges.get(2).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> roleConstraintRelationEdges.get(3).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> roleConstraintRelationEdges.get(4).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> roleConstraintRelationEdges.get(5).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> roleConstraintRelationEdges.get(6).isEndingEdge());
        for (Constraint constraint : test_constraints) { test_constraintsLogicErrors.put(constraint, new HashSet<>(List.of(new ConstraintHasNotEnoughConnectsLogicError(constraint, constraint.getIncidentElements(Edge.class)
                .collect(Collectors.toCollection(ArrayList::new)))))); }
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
        test_constraintsLogicErrors.put(test_constraints.get(1), new HashSet<>(List.of(new ConstraintHasNotEnoughConnectsLogicError(test_constraints.get(1), test_constraints.get(1).getIncidentElements(Edge.class)
                .collect(Collectors.toCollection(ArrayList::new))))));
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(0).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(1).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(2).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(3).isEndingEdge());
        test_constraintsLogicErrors.put(test_constraints.get(0), new HashSet<>(List.of(new ConstraintHasNotEnoughConnectsLogicError(test_constraints.get(0), test_constraints.get(0).getIncidentElements(Edge.class).collect(Collectors.toCollection(ArrayList::new))))));
        test_constraintsLogicErrors.put(test_constraints.get(1), new HashSet<>(List.of(new ConstraintHasNotEnoughConnectsLogicError(test_constraints.get(1), test_constraints.get(1).getIncidentElements(Edge.class).collect(Collectors.toCollection(ArrayList::new))))));
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
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(1).anchorPoint(AnchorPosition.RIGHT), test_objectifiedPredicates.get(0).rightAnchorPoint()));

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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
        test_constraintsLogicErrors.put(test_constraints.get(0), new HashSet<>(List.of(new ConstraintHasNotEnoughConnectsLogicError(test_constraints.get(0), test_constraints.get(0).getIncidentElements(Edge.class).collect(Collectors.toCollection(ArrayList::new))))));
        test_constraintsLogicErrors.put(test_constraints.get(1), new HashSet<>(List.of(new ConstraintHasNotEnoughConnectsLogicError(test_constraints.get(1), test_constraints.get(1).getIncidentElements(Edge.class).collect(Collectors.toCollection(ArrayList::new))))));
    }

    /*
    // ---------------- RECONNECTION ----------------
    // * Subtyping relation
    @Test
    void subtypingRelation_connect() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint()));

        // Check result
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
    }

    @Test
    void subtypingRelation_undoReconnect() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint());
        this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint());
        this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint());

        for (int i=0; i<3; i++) { this._diagram.undoState(); }

        // Check result
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
    }

    @Test
    void subtypingRelation_redoReconnect() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint()));

        for (int i=0; i<3; i++) { this._diagram.undoState(); }
        for (int i=0; i<3; i++) { this._diagram.redoState(); }

        // Check result
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
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

        this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_subtypingRelationEdges.get(0).anchorPoint());
        this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_subtypingRelationEdges.get(1).anchorPoint());
        this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_subtypingRelationEdges.get(2).anchorPoint());
        this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_subtypingRelationEdges.get(3).anchorPoint());

        for (int i=0; i<4; i++) { this._diagram.undoState(); }

        // Check result
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
        for (Constraint constraint : test_constraints) { test_constraintsLogicErrors.put(constraint, new HashSet<>(List.of(new ConstraintHasNotEnoughReconnectsLogicError(constraint, constraint.getIncidentElements(Edge.class)
                .collect(Collectors.toCollection(ArrayList::new)))))); }
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

        test_addDiagramElement(this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_subtypingRelationEdges.get(0).anchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_subtypingRelationEdges.get(1).anchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_subtypingRelationEdges.get(2).anchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_subtypingRelationEdges.get(3).anchorPoint()));

        for (int i=0; i<4; i++) { this._diagram.undoState(); }
        for (int i=0; i<4; i++) { this._diagram.redoState(); }

        // Check result
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
        test_constraintsLogicErrors.put(test_constraints.get(1), new HashSet<>(List.of(new ConstraintHasNotEnoughReconnectsLogicError(test_constraints.get(1), test_constraints.get(1).getIncidentElements(Edge.class)
                .collect(Collectors.toCollection(ArrayList::new))))));
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
        Assertions.assertFalse(test_roleConstraintRelationEdges.get(0).isEndingEdge());
        Assertions.assertTrue(test_roleConstraintRelationEdges.get(1).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(2).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(3).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(4).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(5).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(6).isEndingEdge());
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
        for (Constraint constraint : test_constraints) { test_constraintsLogicErrors.put(constraint, new HashSet<>(List.of(new ConstraintHasNotEnoughReconnectsLogicError(constraint, constraint.getIncidentElements(Edge.class)
                .collect(Collectors.toCollection(ArrayList::new)))))); }
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
        Assertions.assertFalse(test_roleConstraintRelationEdges.get(0).isEndingEdge());
        Assertions.assertTrue(test_roleConstraintRelationEdges.get(1).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(2).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(3).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(4).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(5).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(6).isEndingEdge());
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
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }
        test_constraintsLogicErrors.put(test_constraints.get(2), new HashSet<>(List.of(new ConstraintHasNotEnoughReconnectsLogicError(test_constraints.get(2), test_constraints.get(2).getIncidentElements(Edge.class)
                .collect(Collectors.toCollection(ArrayList::new))))));
        Assertions.assertFalse(test_roleConstraintRelationEdges.get(0).isEndingEdge());
        Assertions.assertTrue(test_roleConstraintRelationEdges.get(1).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(2).isEndingEdge());
        Assertions.assertThrows(RuntimeException.class, () -> test_roleConstraintRelationEdges.get(3).isEndingEdge());
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
