import com.orm2_graph_library.anchor_points.AnchorPosition;
import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.edges.RoleRelationEdge;
import com.orm2_graph_library.edges.SubtypingRelationEdge;
import com.orm2_graph_library.nodes.common.EntityType;
import com.orm2_graph_library.nodes.common.ObjectType;
import com.orm2_graph_library.nodes.common.ValueType;
import com.orm2_graph_library.nodes.constraints.Constraint;
import com.orm2_graph_library.nodes.constraints.SubsetConstraint;
import com.orm2_graph_library.nodes.constraints.UniquenessConstraint;
import com.orm2_graph_library.nodes.predicates.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Test_diagramReading {
    // ==================== TEST ====================
    // ---------------- OBJECT TYPES ----------------
    @Test
    void objectTypesWithConstrainedPredicates() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        Predicate sp0 = diagram.addNode(new Predicate(3));
        Predicate sp1 = diagram.addNode(new Predicate(3));
        Predicate sp2 = diagram.addNode(new Predicate(3));

        EntityType et0 = diagram.addNode(new EntityType());
        EntityType et1 = diagram.addNode(new EntityType());
        ValueType vt0 = diagram.addNode(new ValueType());
        ValueType vt1 = diagram.addNode(new ValueType());

        SubsetConstraint c0 = diagram.addNode(new SubsetConstraint());

        diagram.connectByRoleRelation(sp0.getRole(0).anchorPoint(AnchorPosition.LEFT),  et0.centerAnchorPoint());
        diagram.connectByRoleRelation(sp2.getRole(1).anchorPoint(AnchorPosition.UP),    vt0.centerAnchorPoint());
        diagram.connectByRoleRelation(sp0.getRole(2).anchorPoint(AnchorPosition.RIGHT), et1.centerAnchorPoint());

        diagram.connectByRoleRelation(sp1.getRole(0).anchorPoint(AnchorPosition.LEFT),  et0.centerAnchorPoint());
        diagram.connectByRoleRelation(sp1.getRole(1).anchorPoint(AnchorPosition.DOWN),  et1.centerAnchorPoint());
        diagram.connectByRoleRelation(sp1.getRole(2).anchorPoint(AnchorPosition.RIGHT), vt0.centerAnchorPoint());

        diagram.connectByRoleRelation(sp2.getRole(0).anchorPoint(AnchorPosition.LEFT),  et1.centerAnchorPoint());
        diagram.connectByRoleRelation(sp2.getRole(1).anchorPoint(AnchorPosition.UP),    vt0.centerAnchorPoint());
        diagram.connectByRoleRelation(sp2.getRole(2).anchorPoint(AnchorPosition.RIGHT), vt1.centerAnchorPoint());

        diagram.connectByRoleConstraintRelation(sp0.rolesSequence(0, 1), c0.centerAnchorPoint());
        diagram.connectByRoleConstraintRelation(sp1.rolesSequence(0, 2), c0.centerAnchorPoint());

        Set<ObjectType> real_objectTypes = diagram.getElements(ObjectType.class)
                .filter(e -> e.getIncidentElements(Predicate.class).stream()
                        .anyMatch(e1 -> e1.hasIncidentElements(Constraint.class)))
                .collect(Collectors.toCollection(HashSet::new));

        // Check result
        Set<ObjectType> exp_objectTypes = new HashSet<>();
        exp_objectTypes.add(et0);
        exp_objectTypes.add(et1);
        exp_objectTypes.add(vt0);

        Assertions.assertEquals(exp_objectTypes, real_objectTypes);
    }

    // --------------- ROLES SEQUENCE ---------------
    @Test
    void rolesSequencesWithSeveralConstrainsOnIt() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        Predicate sp0 = diagram.addNode(new Predicate(4));
        Predicate sp1 = diagram.addNode(new Predicate(2));
        Predicate sp2 = diagram.addNode(new Predicate(3));

        Constraint c0 = new SubsetConstraint();
        Constraint c1 = new UniquenessConstraint();

        diagram.connectByRoleConstraintRelation(sp0.rolesSequence(1, 2), c0.centerAnchorPoint());
        diagram.connectByRoleConstraintRelation(sp0.rolesSequence(0, 1, 2), c0.centerAnchorPoint());
        diagram.connectByRoleConstraintRelation(sp0.rolesSequence(0, 1), c1.centerAnchorPoint());

        diagram.connectByRoleConstraintRelation(sp1.rolesSequence(0, 1), c0.centerAnchorPoint());
        diagram.connectByRoleConstraintRelation(sp1.rolesSequence(1), c1.centerAnchorPoint());

        diagram.connectByRoleConstraintRelation(sp2.rolesSequence(0, 2), c1.centerAnchorPoint());
        diagram.connectByRoleConstraintRelation(sp2.rolesSequence(0, 1, 2), c0.centerAnchorPoint());

        Set<RolesSequence> real_rolesSequences = diagram.getElements(RolesSequence.class)
                .filter(e -> e.hasIncidentElements(SubsetConstraint.class))
                .collect(Collectors.toCollection(HashSet::new));

        // Check result
        Set<RolesSequence> exp_rolesSequences = new HashSet<>();
        exp_rolesSequences.add(sp0.rolesSequence(1, 2));
        exp_rolesSequences.add(sp0.rolesSequence(0, 1, 2));
        exp_rolesSequences.add(sp1.rolesSequence(0, 1));
        exp_rolesSequences.add(sp2.rolesSequence(0, 1, 2));

        Assertions.assertEquals(exp_rolesSequences, real_rolesSequences);
    }

    // ----------------- PREDICATES -----------------
    @Test
    void binPredicate_entityTypeAndEntityType() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        Predicate standalonePredicate0 = diagram.addNode(new Predicate(2));
        Predicate standalonePredicate1 = diagram.addNode(new Predicate(3));
        Predicate standalonePredicate2 = diagram.addNode(new Predicate(2));
        Predicate standalonePredicate3 = diagram.addNode(new Predicate(2));

        EntityType entityType0 = diagram.addNode(new EntityType());
        EntityType entityType1 = diagram.addNode(new EntityType());
        EntityType entityType2 = diagram.addNode(new EntityType());
        EntityType entityType3 = diagram.addNode(new EntityType());

        ValueType valueType0 = diagram.addNode(new ValueType());
        ValueType valueType1 = diagram.addNode(new ValueType());
        ValueType valueType2 = diagram.addNode(new ValueType());

        diagram.connectByRoleRelation(standalonePredicate0.getRole(0).anchorPoint(AnchorPosition.LEFT),  valueType0.centerAnchorPoint());
        diagram.connectByRoleRelation(standalonePredicate0.getRole(1).anchorPoint(AnchorPosition.RIGHT), entityType0.centerAnchorPoint());

        diagram.connectByRoleRelation(standalonePredicate1.getRole(0).anchorPoint(AnchorPosition.LEFT), entityType0.centerAnchorPoint());
        diagram.connectByRoleRelation(standalonePredicate1.getRole(1).anchorPoint(AnchorPosition.UP), valueType1.centerAnchorPoint());
        diagram.connectByRoleRelation(standalonePredicate1.getRole(2).anchorPoint(AnchorPosition.RIGHT), valueType2.centerAnchorPoint());

        diagram.connectByRoleRelation(standalonePredicate2.getRole(0).anchorPoint(AnchorPosition.LEFT),  entityType2.centerAnchorPoint());
        diagram.connectByRoleRelation(standalonePredicate2.getRole(1).anchorPoint(AnchorPosition.RIGHT), entityType3.centerAnchorPoint());

        diagram.connectByRoleRelation(standalonePredicate3.getRole(0).anchorPoint(AnchorPosition.LEFT),  entityType1.centerAnchorPoint());
        diagram.connectByRoleRelation(standalonePredicate3.getRole(1).anchorPoint(AnchorPosition.RIGHT), valueType1.centerAnchorPoint());

        Set<Predicate> real_predicates = diagram.getElements(Predicate.class)
                .filter(e -> e.arity() == 2 && !e.getIncidentElements(EntityType.class).isEmpty() && !e.getIncidentElements(ValueType.class).isEmpty())
                .collect(Collectors.toCollection(HashSet::new));

        // Check result
        Set<Predicate> exp_predicates = new HashSet<>();
        exp_predicates.add(standalonePredicate0);
        exp_predicates.add(standalonePredicate3);

        Assertions.assertEquals(exp_predicates, real_predicates);
    }

    @Test
    void binPredicate_entityTypeAndValueType() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        Predicate standalonePredicate0 = diagram.addNode(new Predicate(2));
        Predicate standalonePredicate1 = diagram.addNode(new Predicate(3));
        Predicate standalonePredicate2 = diagram.addNode(new Predicate(2));
        Predicate standalonePredicate3 = diagram.addNode(new Predicate(2));

        EntityType entityType0 = diagram.addNode(new EntityType());
        EntityType entityType1 = diagram.addNode(new EntityType());
        EntityType entityType2 = diagram.addNode(new EntityType());
        EntityType entityType3 = diagram.addNode(new EntityType());

        ValueType valueType0 = diagram.addNode(new ValueType());
        ValueType valueType1 = diagram.addNode(new ValueType());
        ValueType valueType2 = diagram.addNode(new ValueType());

        diagram.connectByRoleRelation(standalonePredicate0.getRole(0).anchorPoint(AnchorPosition.LEFT),  valueType0.centerAnchorPoint());
        diagram.connectByRoleRelation(standalonePredicate0.getRole(1).anchorPoint(AnchorPosition.RIGHT), entityType0.centerAnchorPoint());

        diagram.connectByRoleRelation(standalonePredicate1.getRole(0).anchorPoint(AnchorPosition.LEFT), entityType0.centerAnchorPoint());
        diagram.connectByRoleRelation(standalonePredicate1.getRole(1).anchorPoint(AnchorPosition.UP), valueType1.centerAnchorPoint());
        diagram.connectByRoleRelation(standalonePredicate1.getRole(2).anchorPoint(AnchorPosition.RIGHT), valueType2.centerAnchorPoint());

        diagram.connectByRoleRelation(standalonePredicate2.getRole(0).anchorPoint(AnchorPosition.LEFT),  entityType2.centerAnchorPoint());
        diagram.connectByRoleRelation(standalonePredicate2.getRole(1).anchorPoint(AnchorPosition.RIGHT), entityType3.centerAnchorPoint());

        diagram.connectByRoleRelation(standalonePredicate3.getRole(0).anchorPoint(AnchorPosition.LEFT),  entityType1.centerAnchorPoint());
        diagram.connectByRoleRelation(standalonePredicate3.getRole(1).anchorPoint(AnchorPosition.RIGHT), valueType1.centerAnchorPoint());

        Set<Predicate> real_predicates = diagram.getElements(Predicate.class)
                .filter(e -> e.arity() == 2)
                .filter(e -> e.getIncidentElements(RoleRelationEdge.class).stream().anyMatch(e1 -> e1.begin() == e.getRole(0) && e1.end() instanceof EntityType))
                .filter(e -> e.getIncidentElements(RoleRelationEdge.class).stream().anyMatch(e1 -> e1.begin() == e.getRole(1) && e1.end() instanceof ValueType))
                .collect(Collectors.toCollection(HashSet::new));

        // Check result
        Set<Predicate> exp_predicates = new HashSet<>();
        exp_predicates.add(standalonePredicate3);

        Assertions.assertEquals(exp_predicates, real_predicates);
    }

    @Test
    void predicate_constraintedWithOtherPredicate() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        Predicate sp0 = diagram.addNode(new Predicate(3));
        Predicate sp1 = diagram.addNode(new Predicate(3));
        Predicate sp2 = diagram.addNode(new Predicate(3));

        EntityType et0 = diagram.addNode(new EntityType());
        EntityType et1 = diagram.addNode(new EntityType());
        ValueType vt0 = diagram.addNode(new ValueType());
        ValueType vt1 = diagram.addNode(new ValueType());

        SubsetConstraint c0 = diagram.addNode(new SubsetConstraint());

        diagram.connectByRoleRelation(sp0.getRole(0).anchorPoint(AnchorPosition.LEFT),  et0.centerAnchorPoint());
        diagram.connectByRoleRelation(sp2.getRole(1).anchorPoint(AnchorPosition.UP),    vt0.centerAnchorPoint());
        diagram.connectByRoleRelation(sp0.getRole(2).anchorPoint(AnchorPosition.RIGHT), et1.centerAnchorPoint());

        diagram.connectByRoleRelation(sp1.getRole(0).anchorPoint(AnchorPosition.LEFT),  et0.centerAnchorPoint());
        diagram.connectByRoleRelation(sp1.getRole(1).anchorPoint(AnchorPosition.DOWN),  et1.centerAnchorPoint());
        diagram.connectByRoleRelation(sp1.getRole(2).anchorPoint(AnchorPosition.RIGHT), vt0.centerAnchorPoint());

        diagram.connectByRoleRelation(sp2.getRole(0).anchorPoint(AnchorPosition.LEFT),  et1.centerAnchorPoint());
        diagram.connectByRoleRelation(sp2.getRole(1).anchorPoint(AnchorPosition.UP),    vt0.centerAnchorPoint());
        diagram.connectByRoleRelation(sp2.getRole(2).anchorPoint(AnchorPosition.RIGHT), vt1.centerAnchorPoint());

        diagram.connectByRoleConstraintRelation(sp0.rolesSequence(0, 1), c0.centerAnchorPoint());
        diagram.connectByRoleConstraintRelation(sp1.rolesSequence(0, 2), c0.centerAnchorPoint());

        Set<Predicate> real_predicates = diagram.getElements(Predicate.class)
                .filter(e -> e.getIncidentElements(Constraint.class).stream()
                        .anyMatch(e1 -> e1.getIncidentElements(Predicate.class).stream().
                                anyMatch(e2 -> e2 != e && e2.getRole(0).hasOnlyIncidentElementsWith(EntityType.class) &&
                                        e2.getRole(e2.arity()-1).hasOnlyIncidentElementsWith(ValueType.class))))
                .collect(Collectors.toCollection(HashSet::new));

        // Check result
        Set<Predicate> exp_predicates = new HashSet<>();
        exp_predicates.add(sp0);

        Assertions.assertEquals(exp_predicates, real_predicates);
    }

    @Test
    void predicate_constraintedWithObjectifiedPredicate() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        Predicate p0 = diagram.addNode(new Predicate(3));
        Predicate p1 = diagram.addNode(new Predicate(3));
        ObjectifiedPredicate op0 = diagram.addNode(new ObjectifiedPredicate(p1));

        EntityType et0 = diagram.addNode(new EntityType());
        EntityType et1 = diagram.addNode(new EntityType());
        ValueType vt0 = diagram.addNode(new ValueType());
        ValueType vt1 = diagram.addNode(new ValueType());

        SubsetConstraint c0 = diagram.addNode(new SubsetConstraint());

        diagram.connectByRoleRelation(p0.getRole(0).anchorPoint(AnchorPosition.LEFT),  et0.centerAnchorPoint());
        diagram.connectByRoleRelation(p1.getRole(1).anchorPoint(AnchorPosition.UP),    vt0.centerAnchorPoint());
        diagram.connectByRoleRelation(p0.getRole(2).anchorPoint(AnchorPosition.RIGHT), et1.centerAnchorPoint());

        diagram.connectByRoleRelation(op0.innerPredicate().getRole(0).anchorPoint(AnchorPosition.LEFT),  et0.centerAnchorPoint());
        diagram.connectByRoleRelation(op0.innerPredicate().getRole(1).anchorPoint(AnchorPosition.DOWN),  et1.centerAnchorPoint());
        diagram.connectByRoleRelation(op0.innerPredicate().getRole(2).anchorPoint(AnchorPosition.RIGHT), vt0.centerAnchorPoint());

        diagram.connectByRoleRelation(p1.getRole(0).anchorPoint(AnchorPosition.LEFT),  et1.centerAnchorPoint());
        diagram.connectByRoleRelation(p1.getRole(1).anchorPoint(AnchorPosition.UP),    vt0.centerAnchorPoint());
        diagram.connectByRoleRelation(p1.getRole(2).anchorPoint(AnchorPosition.RIGHT), vt1.centerAnchorPoint());

        diagram.connectByRoleConstraintRelation(p0.rolesSequence(0, 1), c0.centerAnchorPoint());
        diagram.connectByRoleConstraintRelation(op0.innerPredicate().rolesSequence(0, 2), c0.centerAnchorPoint());

        Set<Predicate> real_predicates = diagram.getElements(Predicate.class)
                .filter(e -> e.getIncidentElements(Constraint.class).stream()
                        .anyMatch(e1 -> e1.getIncidentElements(ObjectifiedPredicate.class).stream().
                                anyMatch(e2 -> e2.innerPredicate() != e && e2.innerPredicate().getRole(0).hasOnlyIncidentElementsWith(EntityType.class) &&
                                        e2.innerPredicate().getRole(e2.innerPredicate().arity()-1).hasOnlyIncidentElementsWith(ValueType.class))))
                .collect(Collectors.toCollection(HashSet::new));

        // Check result
        Set<Predicate> exp_predicates = new HashSet<>();
        exp_predicates.add(p0);

        Assertions.assertEquals(exp_predicates, real_predicates);
    }

    @Test
    void rolesSequence_cashingGottenRolesSequence() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        Predicate p = diagram.addNode(new Predicate(3));

        // Check result
        Set<Predicate> exp_predicates = new HashSet<>();
        exp_predicates.add(p);

        Assertions.assertEquals(p.rolesSequence(0), p.rolesSequence(0));
        Assertions.assertEquals(p.rolesSequence(1), p.rolesSequence(1));
        Assertions.assertEquals(p.rolesSequence(2), p.rolesSequence(2));
        Assertions.assertEquals(p.rolesSequence(0, 1), p.rolesSequence(0, 1));
        Assertions.assertEquals(p.rolesSequence(0, 2), p.rolesSequence(0, 2));
        Assertions.assertEquals(p.rolesSequence(1, 2), p.rolesSequence(1, 2));
        Assertions.assertEquals(p.rolesSequence(0, 1, 2), p.rolesSequence(0, 1, 2));
    }
}
