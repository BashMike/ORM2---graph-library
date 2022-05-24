import com.orm2_graph_library.anchor_points.AnchorPosition;
import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.Edge;
import com.orm2_graph_library.core.LogicError;
import com.orm2_graph_library.edges.RoleRelationEdge;
import com.orm2_graph_library.edges.SubtypingRelationEdge;
import com.orm2_graph_library.logic_errors.ConstraintHasExtraConnectsLogicError;
import com.orm2_graph_library.logic_errors.EntityTypeWithNoneRefModeLogicError;
import com.orm2_graph_library.logic_errors.RoleHasNoTextSetLogicError;
import com.orm2_graph_library.nodes.common.EntityType;
import com.orm2_graph_library.nodes.common.ObjectType;
import com.orm2_graph_library.nodes.common.ValueType;
import com.orm2_graph_library.nodes.constraints.Constraint;
import com.orm2_graph_library.nodes.constraints.SubsetConstraint;
import com.orm2_graph_library.nodes.constraints.UniquenessConstraint;
import com.orm2_graph_library.nodes.predicates.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import utils.Test_globalTest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Test_diagramReading extends Test_globalTest {
    // ==================== TEST ====================
    // ---------------- OBJECT TYPES ----------------
    @Test
    void objectTypesWithConstrainedPredicates() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));

        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new ValueType()));
        test_addDiagramElement(this._diagram.addNode(new ValueType()));

        test_addDiagramElement(this._diagram.addNode(new SubsetConstraint()));

        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(0).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(0).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(2).getRole(1).anchorPoint(AnchorPosition.UP),    test_valueTypes.get(0).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(0).getRole(2).anchorPoint(AnchorPosition.RIGHT), test_entityTypes.get(1).centerAnchorPoint()));

        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(0).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(1).anchorPoint(AnchorPosition.DOWN),  test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(2).anchorPoint(AnchorPosition.RIGHT), test_valueTypes.get(0).centerAnchorPoint()));

        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(2).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(2).getRole(2).anchorPoint(AnchorPosition.RIGHT), test_valueTypes.get(1).centerAnchorPoint()));

        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0, 1)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(1).rolesSequence(0, 2)));

        test_addDiagramElement(test_predicates.get(0).rolesSequence(0, 1));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(0, 2));

        Set<ObjectType> real_objectTypes = this._diagram.getElements(ObjectType.class)
                .filter(e -> e.getIncidentElements(Predicate.class)
                        .anyMatch(e1 -> e1.hasIncidentElements(Constraint.class)))
                .collect(Collectors.toCollection(HashSet::new));

        // Check result
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }

        for (Predicate predicate : test_predicates) {
            for (Role role : predicate.roles().collect(Collectors.toCollection(ArrayList::new))) {
                LogicError logicError = new RoleHasNoTextSetLogicError(role);

                test_predicatesLogicErrors.computeIfAbsent(role.ownerPredicate(), k -> new HashSet<>()).add(logicError);
                test_rolesLogicErrors.computeIfAbsent(role, k -> new HashSet<>()).add(logicError);
            }
        }
    }

    // --------------- ROLES SEQUENCE ---------------
    @Test
    void rolesSequencesWithSeveralConstrainsOnIt() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(2)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));

        test_addDiagramElement(this._diagram.addNode(new SubsetConstraint()));
        test_addDiagramElement(this._diagram.addNode(new UniquenessConstraint()));

        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(0).rolesSequence(1, 2)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0, 1, 2)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0, 1)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_predicates.get(1).rolesSequence(1)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(1).centerAnchorPoint(), test_predicates.get(2).rolesSequence(0, 2)));

        test_addDiagramElement(test_predicates.get(0).rolesSequence(1, 2));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0, 1, 2));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0, 1));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(1));
        test_addDiagramElement(test_predicates.get(2).rolesSequence(0, 2));

        Set<RolesSequence> real_rolesSequences = this._diagram.getElements(RolesSequence.class)
                .filter(e -> e.hasIncidentElements(UniquenessConstraint.class))
                .collect(Collectors.toCollection(HashSet::new));

        ArrayList<LogicError> logicErrors = this._diagram.getLogicErrorsFor(test_constraints.get(0)).collect(Collectors.toCollection(ArrayList::new));

        // Check result
        Set<RolesSequence> exp_rolesSequences = new HashSet<>();
        exp_rolesSequences.add(test_predicates.get(0).rolesSequence(0, 1));
        exp_rolesSequences.add(test_predicates.get(1).rolesSequence(1));
        exp_rolesSequences.add(test_predicates.get(2).rolesSequence(0, 2));

        Assertions.assertEquals(exp_rolesSequences, real_rolesSequences);

        for (Predicate predicate : test_predicates) {
            for (Role role : predicate.roles().collect(Collectors.toCollection(ArrayList::new))) {
                LogicError logicError = new RoleHasNoTextSetLogicError(role);

                test_predicatesLogicErrors.computeIfAbsent(role.ownerPredicate(), k -> new HashSet<>()).add(logicError);
                test_rolesLogicErrors.computeIfAbsent(role, k -> new HashSet<>()).add(logicError);
            }
        }
    }

    // ----------------- PREDICATES -----------------
    @Test
    void binPredicate_entityTypeAndEntityType() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(2)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(2)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(2)));

        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));

        test_addDiagramElement(this._diagram.addNode(new ValueType()));
        test_addDiagramElement(this._diagram.addNode(new ValueType()));
        test_addDiagramElement(this._diagram.addNode(new ValueType()));

        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(0).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_valueTypes.get(0).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(0).getRole(1).anchorPoint(AnchorPosition.RIGHT), test_entityTypes.get(0).centerAnchorPoint()));

        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(0).anchorPoint(AnchorPosition.LEFT), test_entityTypes.get(0).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(1).anchorPoint(AnchorPosition.UP), test_valueTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(2).anchorPoint(AnchorPosition.RIGHT), test_valueTypes.get(2).centerAnchorPoint()));

        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(2).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(2).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(2).getRole(1).anchorPoint(AnchorPosition.RIGHT), test_entityTypes.get(3).centerAnchorPoint()));

        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(1).anchorPoint(AnchorPosition.RIGHT), test_valueTypes.get(1).centerAnchorPoint()));

        Set<Predicate> real_predicates = this._diagram.getElements(Predicate.class)
                .filter(e -> e.arity() == 2
                          && e.getIncidentElements(EntityType.class).findAny().isPresent()
                          && e.getIncidentElements(ValueType.class).findAny().isPresent())
                .collect(Collectors.toCollection(HashSet::new));

        // Check result
        Set<Predicate> exp_predicates = new HashSet<>();
        exp_predicates.add(test_predicates.get(0));
        exp_predicates.add(test_predicates.get(3));

        Assertions.assertEquals(exp_predicates, real_predicates);

        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }

        for (Predicate predicate : test_predicates) {
            for (Role role : predicate.roles().collect(Collectors.toCollection(ArrayList::new))) {
                LogicError logicError = new RoleHasNoTextSetLogicError(role);

                test_predicatesLogicErrors.computeIfAbsent(role.ownerPredicate(), k -> new HashSet<>()).add(logicError);
                test_rolesLogicErrors.computeIfAbsent(role, k -> new HashSet<>()).add(logicError);
            }
        }
    }

    @Test
    void binPredicate_entityTypeAndValueType() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(2)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(2)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(2)));

        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));

        test_addDiagramElement(this._diagram.addNode(new ValueType()));
        test_addDiagramElement(this._diagram.addNode(new ValueType()));
        test_addDiagramElement(this._diagram.addNode(new ValueType()));

        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(0).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_valueTypes.get(0).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(0).getRole(1).anchorPoint(AnchorPosition.RIGHT), test_entityTypes.get(0).centerAnchorPoint()));

        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(0).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(1).anchorPoint(AnchorPosition.UP),    test_valueTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(2).anchorPoint(AnchorPosition.RIGHT), test_valueTypes.get(2).centerAnchorPoint()));

        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(2).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(2).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(2).getRole(1).anchorPoint(AnchorPosition.RIGHT), test_entityTypes.get(3).centerAnchorPoint()));

        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(3).getRole(1).anchorPoint(AnchorPosition.RIGHT), test_valueTypes.get(1).centerAnchorPoint()));

        Set<Predicate> real_predicates = this._diagram.getElements(Predicate.class)
                .filter(e -> e.arity() == 2)
                .filter(e -> e.getIncidentElements(RoleRelationEdge.class).anyMatch(e1 -> e1.begin() == e.getRole(0) && e1.end() instanceof EntityType))
                .filter(e -> e.getIncidentElements(RoleRelationEdge.class).anyMatch(e1 -> e1.begin() == e.getRole(1) && e1.end() instanceof ValueType))
                .collect(Collectors.toCollection(HashSet::new));

        // Check result
        Set<Predicate> exp_predicates = new HashSet<>();
        exp_predicates.add(test_predicates.get(3));

        Assertions.assertEquals(exp_predicates, real_predicates);

        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }

        for (Predicate predicate : test_predicates) {
            for (Role role : predicate.roles().collect(Collectors.toCollection(ArrayList::new))) {
                LogicError logicError = new RoleHasNoTextSetLogicError(role);

                test_predicatesLogicErrors.computeIfAbsent(role.ownerPredicate(), k -> new HashSet<>()).add(logicError);
                test_rolesLogicErrors.computeIfAbsent(role, k -> new HashSet<>()).add(logicError);
            }
        }
    }

    @Test
    void predicate_constraintedWithOtherPredicate() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));

        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new ValueType()));
        test_addDiagramElement(this._diagram.addNode(new ValueType()));

        test_addDiagramElement(this._diagram.addNode(new SubsetConstraint()));

        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(0).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(0).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(2).getRole(1).anchorPoint(AnchorPosition.UP),    test_valueTypes.get(0).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(0).getRole(2).anchorPoint(AnchorPosition.RIGHT), test_entityTypes.get(1).centerAnchorPoint()));

        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(0).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(1).anchorPoint(AnchorPosition.DOWN),  test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(3).anchorPoint(AnchorPosition.RIGHT), test_valueTypes.get(0).centerAnchorPoint()));

        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(2).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(2).getRole(2).anchorPoint(AnchorPosition.RIGHT), test_valueTypes.get(1).centerAnchorPoint()));

        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0, 1)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(1).rolesSequence(0, 2)));

        test_addDiagramElement(test_predicates.get(0).rolesSequence(0, 1));
        test_addDiagramElement(test_predicates.get(1).rolesSequence(0, 2));

        Set<Predicate> real_predicates = this._diagram.getElements(Predicate.class)
                .filter(e -> e.getIncidentElements(Constraint.class)
                        .anyMatch(e1 -> e1.getIncidentElements(Predicate.class)
                                .anyMatch(e2 -> e2 != e && e2.getRole(0).hasOnlyIncidentElementsWith(EntityType.class) &&
                                        e2.getRole(e2.arity()-1).hasOnlyIncidentElementsWith(ValueType.class))))
                .collect(Collectors.toCollection(HashSet::new));

        // Check result
        Set<Predicate> exp_predicates = new HashSet<>();
        exp_predicates.add(test_predicates.get(0));

        Assertions.assertEquals(exp_predicates, real_predicates);

        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }

        for (Predicate predicate : test_predicates) {
            for (Role role : predicate.roles().collect(Collectors.toCollection(ArrayList::new))) {
                LogicError logicError = new RoleHasNoTextSetLogicError(role);

                test_predicatesLogicErrors.computeIfAbsent(role.ownerPredicate(), k -> new HashSet<>()).add(logicError);
                test_rolesLogicErrors.computeIfAbsent(role, k -> new HashSet<>()).add(logicError);
            }
        }
    }

    @Test
    void predicate_constraintedWithObjectifiedPredicate() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(test_predicates.get(1))));

        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new ValueType()));
        test_addDiagramElement(this._diagram.addNode(new ValueType()));

        test_addDiagramElement(this._diagram.addNode(new SubsetConstraint()));

        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(0).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(0).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(1).anchorPoint(AnchorPosition.UP),    test_valueTypes.get(0).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(0).getRole(2).anchorPoint(AnchorPosition.RIGHT), test_entityTypes.get(1).centerAnchorPoint()));

        test_addDiagramElement(this._diagram.connectByRoleRelation(test_objectifiedPredicates.get(0).innerPredicate().getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(0).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_objectifiedPredicates.get(0).innerPredicate().getRole(1).anchorPoint(AnchorPosition.DOWN),  test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_objectifiedPredicates.get(0).innerPredicate().getRole(2).anchorPoint(AnchorPosition.RIGHT), test_valueTypes.get(0).centerAnchorPoint()));

        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(0).anchorPoint(AnchorPosition.LEFT),  test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectByRoleRelation(test_predicates.get(1).getRole(2).anchorPoint(AnchorPosition.RIGHT), test_valueTypes.get(1).centerAnchorPoint()));

        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_predicates.get(0).rolesSequence(0, 1)));
        test_addDiagramElement(this._diagram.connectByRoleConstraintRelation(test_constraints.get(0).centerAnchorPoint(), test_objectifiedPredicates.get(0).innerPredicate().rolesSequence(0, 2)));

        test_addDiagramElement(test_predicates.get(0).rolesSequence(0, 1));
        test_addDiagramElement(test_objectifiedPredicates.get(0).innerPredicate().rolesSequence(0, 2));

        Set<Predicate> real_predicates = this._diagram.getElements(Predicate.class)
                .filter(e -> e.getIncidentElements(Constraint.class)
                        .anyMatch(e1 -> e1.getIncidentElements(ObjectifiedPredicate.class)
                                .anyMatch(e2 -> e2.innerPredicate() != e && e2.innerPredicate().getRole(0).hasOnlyIncidentElementsWith(EntityType.class) &&
                                        e2.innerPredicate().getRole(e2.innerPredicate().arity()-1).hasOnlyIncidentElementsWith(ValueType.class))))
                .collect(Collectors.toCollection(HashSet::new));

        // Check result
        Set<Predicate> exp_predicates = new HashSet<>();
        exp_predicates.add(test_predicates.get(0));

        Assertions.assertEquals(exp_predicates, real_predicates);

        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }

        for (Predicate predicate : test_predicates) {
            for (Role role : predicate.roles().collect(Collectors.toCollection(ArrayList::new))) {
                LogicError logicError = new RoleHasNoTextSetLogicError(role);

                test_predicatesLogicErrors.computeIfAbsent(role.ownerPredicate(), k -> new HashSet<>()).add(logicError);
                test_rolesLogicErrors.computeIfAbsent(role, k -> new HashSet<>()).add(logicError);
            }
        }
    }

    @Test
    void rolesSequence_cashingGottenRolesSequence() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));

        test_addDiagramElement(test_predicates.get(0).rolesSequence(0));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(1));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(2));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0, 1));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0, 2));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(1, 2));
        test_addDiagramElement(test_predicates.get(0).rolesSequence(0, 1, 2));

        // Check result
        Set<Predicate> exp_predicates = new HashSet<>();
        exp_predicates.add(test_predicates.get(0));

        Assertions.assertEquals(test_predicates.get(0).rolesSequence(0), test_predicates.get(0).rolesSequence(0));
        Assertions.assertEquals(test_predicates.get(0).rolesSequence(1), test_predicates.get(0).rolesSequence(1));
        Assertions.assertEquals(test_predicates.get(0).rolesSequence(2), test_predicates.get(0).rolesSequence(2));
        Assertions.assertEquals(test_predicates.get(0).rolesSequence(0, 1), test_predicates.get(0).rolesSequence(0, 1));
        Assertions.assertEquals(test_predicates.get(0).rolesSequence(0, 2), test_predicates.get(0).rolesSequence(0, 2));
        Assertions.assertEquals(test_predicates.get(0).rolesSequence(1, 2), test_predicates.get(0).rolesSequence(1, 2));
        Assertions.assertEquals(test_predicates.get(0).rolesSequence(0, 1, 2), test_predicates.get(0).rolesSequence(0, 1, 2));

        Assertions.assertTrue(test_predicates.get(0).roles().allMatch(e -> e.ownerDiagram() == this._diagram));
        Assertions.assertTrue(this._diagram.getElements(Role.class).findAny().isPresent());

        for (Predicate predicate : test_predicates) {
            for (Role role : predicate.roles().collect(Collectors.toCollection(ArrayList::new))) {
                LogicError logicError = new RoleHasNoTextSetLogicError(role);

                test_predicatesLogicErrors.computeIfAbsent(role.ownerPredicate(), k -> new HashSet<>()).add(logicError);
                test_rolesLogicErrors.computeIfAbsent(role, k -> new HashSet<>()).add(logicError);
            }
        }
    }
}
