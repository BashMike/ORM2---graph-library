import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.logic_errors.ObjectTypesNameDuplicationLogicError;
import com.orm2_graph_library.nodes.common.EntityType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Test_diagramLogicErrors {
    @Test
    void entityTypesNameDuplication() {
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
        Assertions.assertEquals(1, diagram.logicErrors().size());
        Assertions.assertTrue(diagram.logicErrors().get(0) instanceof ObjectTypesNameDuplicationLogicError);
        Assertions.assertEquals("Hello", ((ObjectTypesNameDuplicationLogicError)diagram.logicErrors().get(0)).name());
        Assertions.assertEquals(sameNameObjectTypes, new HashSet<>( ((ObjectTypesNameDuplicationLogicError)diagram.logicErrors().get(0)).objectTypes() ));
    }

    @Test
    void severalEntityTypesNameDuplications() {
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
        Assertions.assertEquals(2, diagram.logicErrors().size());

        Assertions.assertTrue(diagram.logicErrors().get(0) instanceof ObjectTypesNameDuplicationLogicError);
        Assertions.assertEquals("Super", ((ObjectTypesNameDuplicationLogicError)diagram.logicErrors().get(0)).name());
        Assertions.assertEquals(sameNameObjectTypes0, new HashSet<>( ((ObjectTypesNameDuplicationLogicError)diagram.logicErrors().get(0)).objectTypes() ));

        Assertions.assertTrue(diagram.logicErrors().get(1) instanceof ObjectTypesNameDuplicationLogicError);
        Assertions.assertEquals("Hello", ((ObjectTypesNameDuplicationLogicError)diagram.logicErrors().get(1)).name());
        Assertions.assertEquals(sameNameObjectTypes1, new HashSet<>( ((ObjectTypesNameDuplicationLogicError)diagram.logicErrors().get(1)).objectTypes() ));
    }
}
