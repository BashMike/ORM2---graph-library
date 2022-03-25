import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.edges.SubtypingRelationEdge;
import com.orm2_graph_library.logic_errors.EntityTypeWithNoneRefModeLogicError;
import com.orm2_graph_library.logic_errors.ObjectTypesNameDuplicationLogicError;
import com.orm2_graph_library.logic_errors.SubtypingCycleLogicError;
import com.orm2_graph_library.nodes.common.EntityType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Test_diagramLogicErrors {
    // ==================== TEST ====================
    // ---------------- ENTITY TYPES ----------------
    @Test
    void entityType_NameDuplication() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        ArrayList<EntityType> entityTypes = new ArrayList<>();

        for (int i=0; i<3; i++) {
            entityTypes.add(diagram.addNode(new EntityType()));
            entityTypes.add(diagram.addNode(new EntityType()));
            entityTypes.add(diagram.addNode(new EntityType()));

            EntityType sameNameEntityType = new EntityType();
            diagram.addNode(sameNameEntityType).setName("Hello");
            entityTypes.add(sameNameEntityType);
        }

        Set<EntityType> sameNameObjectTypes = new HashSet<>();
        for (EntityType entityType : entityTypes) {
            if (entityType.name().equals("Hello")) {
                sameNameObjectTypes.add(entityType);
            }
        }

        // Check result
        // Got one name duplication logic error and logic errors for each entity type because they don't have reference mode set.
        Assertions.assertEquals(entityTypes.size() + 1, diagram.logicErrors().size());
        Assertions.assertTrue(diagram.logicErrors().get(entityTypes.size()) instanceof ObjectTypesNameDuplicationLogicError);
        Assertions.assertEquals("Hello", ((ObjectTypesNameDuplicationLogicError)diagram.logicErrors().get(entityTypes.size())).name());
        Assertions.assertEquals(sameNameObjectTypes, new HashSet<>( ((ObjectTypesNameDuplicationLogicError)diagram.logicErrors().get(entityTypes.size())).objectTypes() ));
    }

    @Test
    void entityType_severalNameDuplications() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        ArrayList<EntityType> entityTypes = new ArrayList<>();

        for (int i=0; i<3; i++) {
            entityTypes.add(diagram.addNode(new EntityType()));
            entityTypes.add(diagram.addNode(new EntityType()));

            EntityType sameNameEntityType = new EntityType();
            diagram.addNode(sameNameEntityType).setName("Hello");
            entityTypes.add(sameNameEntityType);

            sameNameEntityType = new EntityType();
            diagram.addNode(sameNameEntityType).setName("Super");
            entityTypes.add(sameNameEntityType);

            sameNameEntityType = new EntityType();
            diagram.addNode(sameNameEntityType).setName("Super");
            entityTypes.add(sameNameEntityType);
        }

        Set<EntityType> sameNameObjectTypes0 = new HashSet<>();
        for (EntityType entityType : entityTypes) {
            if (entityType.name().equals("Super")) { sameNameObjectTypes0.add(entityType); }
        }

        Set<EntityType> sameNameObjectTypes1 = new HashSet<>();
        for (EntityType entityType : entityTypes) {
            if (entityType.name().equals("Hello")) {
                sameNameObjectTypes1.add(entityType);
            }
        }

        // Check result
        // Got two name duplication logic error and logic errors for each entity type because they don't have reference mode set.
        Assertions.assertEquals(entityTypes.size() + 2, diagram.logicErrors().size());

        ArrayList<ObjectTypesNameDuplicationLogicError> nameDuplicationLogicErrors = diagram.logicErrors().stream()
                .filter(e -> e instanceof ObjectTypesNameDuplicationLogicError)
                .map(ObjectTypesNameDuplicationLogicError.class::cast)
                .collect(Collectors.toCollection(ArrayList::new));

        Assertions.assertEquals(2, nameDuplicationLogicErrors.size());

        Assertions.assertEquals("Hello", nameDuplicationLogicErrors.get(0).name());
        Assertions.assertEquals(sameNameObjectTypes1, new HashSet<>( nameDuplicationLogicErrors.get(0).objectTypes() ));
        Assertions.assertEquals("Super", nameDuplicationLogicErrors.get(1).name());
        Assertions.assertEquals(sameNameObjectTypes0, new HashSet<>( nameDuplicationLogicErrors.get(1).objectTypes() ));
    }

    @Test
    void entityType_cycleConnection() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        EntityType entityType0 = diagram.addNode(new EntityType());
        EntityType entityType1 = diagram.addNode(new EntityType());
        EntityType entityType2 = diagram.addNode(new EntityType());

        diagram.connectBySubtypingRelation(entityType0.centerAnchorPoint(), entityType1.centerAnchorPoint());
        diagram.connectBySubtypingRelation(entityType1.centerAnchorPoint(), entityType2.centerAnchorPoint());
        diagram.connectBySubtypingRelation(entityType2.centerAnchorPoint(), entityType0.centerAnchorPoint());

        // Check result
        Assertions.assertEquals(3, diagram.getElements(SubtypingRelationEdge.class).count());

        Assertions.assertEquals(4, diagram.logicErrors().size());
        Assertions.assertEquals(3, diagram.logicErrors().stream().filter(e -> e instanceof EntityTypeWithNoneRefModeLogicError).count());
        Assertions.assertEquals(1, diagram.logicErrors().stream().filter(e -> e instanceof SubtypingCycleLogicError).count());

        SubtypingCycleLogicError subtypingCyclelogicError = (SubtypingCycleLogicError)diagram.logicErrors().stream().filter(e -> e instanceof SubtypingCycleLogicError).findFirst().get();
        ArrayList<EntityType> entityTypeCycle = new ArrayList<>(List.of(entityType2, entityType0, entityType1));
        Assertions.assertEquals(entityTypeCycle, subtypingCyclelogicError.entityTypeCycle());
    }

    @Test
    void entityType_twoCycleConnection() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        EntityType entityType0 = diagram.addNode(new EntityType());
        EntityType entityType1 = diagram.addNode(new EntityType());
        EntityType entityType2 = diagram.addNode(new EntityType());
        EntityType entityType3 = diagram.addNode(new EntityType());
        EntityType entityType4 = diagram.addNode(new EntityType());

        diagram.connectBySubtypingRelation(entityType0.centerAnchorPoint(), entityType1.centerAnchorPoint());
        diagram.connectBySubtypingRelation(entityType1.centerAnchorPoint(), entityType2.centerAnchorPoint());
        diagram.connectBySubtypingRelation(entityType2.centerAnchorPoint(), entityType0.centerAnchorPoint());

        diagram.connectBySubtypingRelation(entityType3.centerAnchorPoint(), entityType4.centerAnchorPoint());
        diagram.connectBySubtypingRelation(entityType4.centerAnchorPoint(), entityType3.centerAnchorPoint());

        // Check result
        Assertions.assertEquals(5, diagram.getElements(SubtypingRelationEdge.class).count());

        Assertions.assertEquals(7, diagram.logicErrors().size());
        Assertions.assertEquals(5, diagram.logicErrors().stream().filter(e -> e instanceof EntityTypeWithNoneRefModeLogicError).count());
        Assertions.assertEquals(2, diagram.logicErrors().stream().filter(e -> e instanceof SubtypingCycleLogicError).count());

        ArrayList<SubtypingCycleLogicError> subtypingCycleLogicErrors = new ArrayList<>();
        diagram.logicErrors().stream().filter(e -> e instanceof SubtypingCycleLogicError).forEach(e -> subtypingCycleLogicErrors.add((SubtypingCycleLogicError)e));

        ArrayList<EntityType> entityTypeCycle0 = new ArrayList<>(List.of(entityType2, entityType0, entityType1));
        ArrayList<EntityType> entityTypeCycle1 = new ArrayList<>(List.of(entityType4, entityType3));

        Assertions.assertEquals(entityTypeCycle0, subtypingCycleLogicErrors.get(0).entityTypeCycle());
        Assertions.assertEquals(entityTypeCycle1, subtypingCycleLogicErrors.get(1).entityTypeCycle());

        Assertions.assertEquals(2, diagram.getLogicErrorsFor(entityType0).size());
    }

    // TODO - @test :: Setting reference mode and data type for entity type.
}
