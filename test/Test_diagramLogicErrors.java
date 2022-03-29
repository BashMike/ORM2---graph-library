import com.orm2_graph_library.core.LogicError;
import com.orm2_graph_library.edges.RoleConstraintRelationEdge;
import com.orm2_graph_library.edges.RoleRelationEdge;
import com.orm2_graph_library.edges.SubtypingConstraintRelationEdge;
import com.orm2_graph_library.edges.SubtypingRelationEdge;
import com.orm2_graph_library.logic_errors.EntityTypeWithNoneRefModeLogicError;
import com.orm2_graph_library.logic_errors.ObjectTypesNameDuplicationLogicError;
import com.orm2_graph_library.logic_errors.SubtypingCycleLogicError;
import com.orm2_graph_library.nodes.common.EntityType;
import com.orm2_graph_library.nodes.common.ValueType;
import com.orm2_graph_library.nodes.constraints.Constraint;
import com.orm2_graph_library.nodes.predicates.ObjectifiedPredicate;
import com.orm2_graph_library.nodes.predicates.Predicate;
import com.orm2_graph_library.nodes.predicates.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.Test_globalTest;

import java.util.*;
import java.util.stream.Collectors;

public class Test_diagramLogicErrors extends Test_globalTest {
    // ==================== INIT ====================
    private Set<LogicError> test_expLogicErrors = new HashSet<>();

    protected Map<EntityType, Set<LogicError>>           test_entityTypesLogicErrors           = new HashMap<>();
    protected Map<ValueType, Set<LogicError>>            test_valueTypesLogicErrors            = new HashMap<>();
    protected Map<Predicate, Set<LogicError>>            test_predicatesLogicErrors            = new HashMap<>();
    protected Map<Role, Set<LogicError>>                 test_rolesLogicErrors                 = new HashMap<>();
    protected Map<ObjectifiedPredicate, Set<LogicError>> test_objectifiedPredicatesLogicErrors = new HashMap<>();
    protected Map<Constraint, Set<LogicError>>           test_constraintsLogicErrors           = new HashMap<>();

    protected Map<SubtypingRelationEdge, Set<LogicError>>           test_subtypingRelationEdgesLogicErrors           = new HashMap<>();
    protected Map<RoleRelationEdge, Set<LogicError>>                test_roleRelationEdgesLogicErrors                = new HashMap<>();
    protected Map<RoleConstraintRelationEdge, Set<LogicError>>      test_roleConstraintRelationEdgesLogicErrors      = new HashMap<>();
    protected Map<SubtypingConstraintRelationEdge, Set<LogicError>> test_subtypingConstraintRelationEdgesLogicErrors = new HashMap<>();

    @BeforeEach
    private void testBegin() {
        this.test_expLogicErrors.clear();
    }

    @AfterEach
    private void testEnd() {
        ArrayList<EntityType>           allEntityTypes           = this._diagram.getElements(EntityType.class).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<ValueType>            allValueTypes            = this._diagram.getElements(ValueType.class).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Predicate>            allPredicates            = this._diagram.getElements(Predicate.class).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Role>                 allRoles                 = this._diagram.getElements(Role.class).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<ObjectifiedPredicate> allObjectifiedPredicates = this._diagram.getElements(ObjectifiedPredicate.class).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Constraint>           allConstraints           = this._diagram.getElements(Constraint.class).collect(Collectors.toCollection(ArrayList::new));

        ArrayList<SubtypingRelationEdge>           allSubtypingRelationEdges           = this._diagram.getElements(SubtypingRelationEdge.class).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<RoleRelationEdge>                allRoleRelationEdges                = this._diagram.getElements(RoleRelationEdge.class).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<SubtypingConstraintRelationEdge> allSubtypingConstraintRelationEdges = this._diagram.getElements(SubtypingConstraintRelationEdge.class).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<RoleConstraintRelationEdge>      allRoleConstraintRelationEdges      = this._diagram.getElements(RoleConstraintRelationEdge.class).collect(Collectors.toCollection(ArrayList::new));

        // Set empty set of logic errors for diagram elements with no logic errors
        for (EntityType de : allEntityTypes)                     { test_entityTypesLogicErrors.computeIfAbsent(de, k -> new HashSet<>()); }
        for (ValueType de : allValueTypes)                       { test_valueTypesLogicErrors.computeIfAbsent(de, k -> new HashSet<>()); }
        for (Predicate de : allPredicates)                       { test_predicatesLogicErrors.computeIfAbsent(de, k -> new HashSet<>()); }
        for (Role de : allRoles)                                 { test_rolesLogicErrors.computeIfAbsent(de, k -> new HashSet<>()); }
        for (ObjectifiedPredicate de : allObjectifiedPredicates) { test_objectifiedPredicatesLogicErrors.computeIfAbsent(de, k -> new HashSet<>()); }
        for (Constraint de : allConstraints)                     { test_constraintsLogicErrors.computeIfAbsent(de, k -> new HashSet<>()); }

        for (SubtypingRelationEdge de : allSubtypingRelationEdges)                     { test_subtypingRelationEdgesLogicErrors.computeIfAbsent(de, k -> new HashSet<>()); }
        for (RoleRelationEdge de : allRoleRelationEdges)                               { test_roleRelationEdgesLogicErrors.computeIfAbsent(de, k -> new HashSet<>()); }
        for (SubtypingConstraintRelationEdge de : allSubtypingConstraintRelationEdges) { test_subtypingConstraintRelationEdgesLogicErrors.computeIfAbsent(de, k -> new HashSet<>()); }
        for (RoleConstraintRelationEdge de : allRoleConstraintRelationEdges)           { test_roleConstraintRelationEdgesLogicErrors.computeIfAbsent(de, k -> new HashSet<>()); }

        // Check logic errors for each diagram element of the diagram
        for (EntityType de : allEntityTypes)                     { Assertions.assertEquals(test_entityTypesLogicErrors.get(de),           this._diagram.getLogicErrorsFor(de).collect(Collectors.toCollection(HashSet::new))); }
        for (ValueType de : allValueTypes)                       { Assertions.assertEquals(test_valueTypesLogicErrors.get(de),            this._diagram.getLogicErrorsFor(de).collect(Collectors.toCollection(HashSet::new))); }
        for (Predicate de : allPredicates)                       { Assertions.assertEquals(test_predicatesLogicErrors.get(de),            this._diagram.getLogicErrorsFor(de).collect(Collectors.toCollection(HashSet::new))); }
        for (Role de : allRoles)                                 { Assertions.assertEquals(test_rolesLogicErrors.get(de),                 this._diagram.getLogicErrorsFor(de).collect(Collectors.toCollection(HashSet::new))); }
        for (ObjectifiedPredicate de : allObjectifiedPredicates) { Assertions.assertEquals(test_objectifiedPredicatesLogicErrors.get(de), this._diagram.getLogicErrorsFor(de).collect(Collectors.toCollection(HashSet::new))); }
        for (Constraint de : allConstraints)                     { Assertions.assertEquals(test_constraintsLogicErrors.get(de),           this._diagram.getLogicErrorsFor(de).collect(Collectors.toCollection(HashSet::new))); }

        for (SubtypingRelationEdge de : allSubtypingRelationEdges)                     { Assertions.assertEquals(test_subtypingRelationEdgesLogicErrors.get(de),           this._diagram.getLogicErrorsFor(de).collect(Collectors.toCollection(HashSet::new))); }
        for (RoleRelationEdge de : allRoleRelationEdges)                               { Assertions.assertEquals(test_roleRelationEdgesLogicErrors.get(de),                this._diagram.getLogicErrorsFor(de).collect(Collectors.toCollection(HashSet::new))); }
        for (SubtypingConstraintRelationEdge de : allSubtypingConstraintRelationEdges) { Assertions.assertEquals(test_subtypingConstraintRelationEdgesLogicErrors.get(de), this._diagram.getLogicErrorsFor(de).collect(Collectors.toCollection(HashSet::new))); }
        for (RoleConstraintRelationEdge de : allRoleConstraintRelationEdges)           { Assertions.assertEquals(test_roleConstraintRelationEdgesLogicErrors.get(de),      this._diagram.getLogicErrorsFor(de).collect(Collectors.toCollection(HashSet::new))); }

        // Check logic errors for the whole diagram
        for (var c : test_entityTypesLogicErrors.values())           { test_expLogicErrors.addAll(c); }
        for (var c : test_valueTypesLogicErrors.values())            { test_expLogicErrors.addAll(c); }
        for (var c : test_predicatesLogicErrors.values())            { test_expLogicErrors.addAll(c); }
        for (var c : test_rolesLogicErrors.values())                 { test_expLogicErrors.addAll(c); }
        for (var c : test_objectifiedPredicatesLogicErrors.values()) { test_expLogicErrors.addAll(c); }
        for (var c : test_constraintsLogicErrors.values())           { test_expLogicErrors.addAll(c); }

        for (var c : test_subtypingRelationEdgesLogicErrors.values())           { test_expLogicErrors.addAll(c); }
        for (var c : test_roleRelationEdgesLogicErrors.values())                { test_expLogicErrors.addAll(c); }
        for (var c : test_subtypingConstraintRelationEdgesLogicErrors.values()) { test_expLogicErrors.addAll(c); }
        for (var c : test_roleConstraintRelationEdgesLogicErrors.values())      { test_expLogicErrors.addAll(c); }

        Assertions.assertEquals(test_expLogicErrors.size(), this._diagram.logicErrors().count());
        Assertions.assertEquals(test_expLogicErrors, this._diagram.logicErrors().collect(Collectors.toCollection(HashSet::new)));
    }

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

    // TODO - @test :: Setting reference mode and data type for entity type.
}
