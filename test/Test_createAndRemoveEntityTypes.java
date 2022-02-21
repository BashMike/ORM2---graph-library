import com.orm2_graph_library.attributes.StringAttribute;
import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.DiagramElement;
import com.orm2_graph_library.nodes.EntityType;
import com.orm2_graph_library.nodes.ObjectType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

class Test_createAndRemoveEntityTypes {
    @Test
    void addEntityTypes() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        ArrayList<EntityType> createdEntityTypes = new ArrayList<>();
        ArrayList<EntityType> gottenEntityTypes;

        for (int i=0; i<5; i++) { createdEntityTypes.add( diagram.addNode(new EntityType()) ); }
        gottenEntityTypes = diagram.getElements(EntityType.class);

        // Check result
        Assertions.assertEquals(gottenEntityTypes, createdEntityTypes);
        Assertions.assertTrue(diagram.getElements(DiagramElement.class).stream().allMatch(DiagramElement::hasOwner));

        Set<String> names     = new HashSet<>();
        Set<String> exp_names = new HashSet<>();

        diagram.getElements(ObjectType.class).forEach(e -> names.add(e.attributes().attribute("name", StringAttribute.class).value()));
        for (int i=0; i<5; i++) { exp_names.add("Entity Type " + (i+1)); }

        Assertions.assertEquals(names, exp_names);
    }

    @Test
    void undoAddingEntityTypes() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        ArrayList<EntityType> createdEntityTypes = new ArrayList<>();

        for (int i=0; i<5; i++) {
            createdEntityTypes.add( diagram.addNode(new EntityType()) );
        }

        diagram.undoState();
        diagram.undoState();
        diagram.undoState();

        createdEntityTypes = new ArrayList<>(createdEntityTypes.subList(0, 2));
        ArrayList<EntityType> gottenEntityTypes = diagram.getElements(EntityType.class);

        // Check result
        Assertions.assertEquals(gottenEntityTypes, createdEntityTypes);
        Assertions.assertTrue(diagram.getElements(DiagramElement.class).stream().allMatch(DiagramElement::hasOwner));
        Assertions.assertTrue(diagram.canRedoState());
    }

    @Test
    void redoAddingEntityTypes() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        ArrayList<EntityType> createdEntityTypes = new ArrayList<>();

        for (int i=0; i<5; i++) { createdEntityTypes.add( diagram.addNode(new EntityType()) ); }

        diagram.undoState();
        diagram.undoState();
        diagram.undoState();
        diagram.redoState();
        diagram.redoState();
        diagram.redoState();

        ArrayList<EntityType> gottenEntityTypes = diagram.getElements(EntityType.class);

        // Check result
        Assertions.assertEquals(gottenEntityTypes, createdEntityTypes);
        Assertions.assertTrue(diagram.getElements(DiagramElement.class).stream().allMatch(DiagramElement::hasOwner));
        Assertions.assertFalse(diagram.canRedoState());
    }

    @Test
    void addEntityTypesAfterUndo() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        ArrayList<EntityType> createdEntityTypes = new ArrayList<>();

        for (int i=0; i<5; i++) {
            createdEntityTypes.add( diagram.addNode(new EntityType()) );
        }

        diagram.undoState();
        diagram.undoState();
        diagram.undoState();

        createdEntityTypes = new ArrayList<>(createdEntityTypes.subList(0, 2));
        createdEntityTypes.add( diagram.addNode(new EntityType()) );

        ArrayList<EntityType> gottenEntityTypes = diagram.getElements(EntityType.class);

        // Check result
        Assertions.assertEquals(gottenEntityTypes, createdEntityTypes);
        Assertions.assertTrue(diagram.getElements(DiagramElement.class).stream().allMatch(DiagramElement::hasOwner));
        Assertions.assertFalse(diagram.canRedoState());
    }

    @Test
    void removeEntityType() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        ArrayList<EntityType> createdEntityTypes = new ArrayList<>();

        for (int i=0; i<5; i++) { createdEntityTypes.add( diagram.addNode(new EntityType()) ); }

        diagram.removeNode( diagram.getElements(EntityType.class).get(4) );

        createdEntityTypes = new ArrayList<>(createdEntityTypes.subList(0, 4));
        ArrayList<EntityType> gottenEntityTypes = diagram.getElements(EntityType.class);

        // Check result
        Assertions.assertEquals(gottenEntityTypes, createdEntityTypes);
        Assertions.assertTrue(diagram.getElements(DiagramElement.class).stream().allMatch(DiagramElement::hasOwner));
    }

    @Test
    void undoRemovingEntityTypes() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        ArrayList<EntityType> createdEntityTypes = new ArrayList<>();
        ArrayList<EntityType> gottenEntityTypes;

        for (int i=0; i<5; i++) { createdEntityTypes.add( diagram.addNode(new EntityType()) ); }

        diagram.removeNode(diagram.getElements(EntityType.class).get(0));
        diagram.removeNode(diagram.getElements(EntityType.class).get(0));
        diagram.removeNode(diagram.getElements(EntityType.class).get(0));

        diagram.undoState();
        diagram.undoState();
        diagram.undoState();

        gottenEntityTypes = diagram.getElements(EntityType.class);

        // Check result
        Assertions.assertEquals(new HashSet<>(gottenEntityTypes), new HashSet<>(createdEntityTypes));
        Assertions.assertTrue(diagram.getElements(DiagramElement.class).stream().allMatch(DiagramElement::hasOwner));
        Assertions.assertTrue(diagram.canRedoState());
    }

    // TODO - @test
    @Test
    void redoRemovingEntityTypes() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        ArrayList<EntityType> createdEntityTypes = new ArrayList<>();

        for (int i=0; i<5; i++) { createdEntityTypes.add( diagram.addNode(new EntityType()) ); }

        diagram.undoState();
        diagram.undoState();
        diagram.undoState();
        diagram.redoState();
        diagram.redoState();
        diagram.redoState();

        ArrayList<EntityType> gottenEntityTypes = diagram.getElements(EntityType.class);

        // Check result
        Assertions.assertEquals(gottenEntityTypes, createdEntityTypes);
        Assertions.assertTrue(diagram.getElements(DiagramElement.class).stream().allMatch(DiagramElement::hasOwner));
        Assertions.assertFalse(diagram.canRedoState());
    }

    // TODO - @test
    @Test
    void removeEntityTypesAfterUndo() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        ArrayList<EntityType> createdEntityTypes = new ArrayList<>();

        for (int i=0; i<5; i++) {
            createdEntityTypes.add( diagram.addNode(new EntityType()) );
        }

        diagram.undoState();
        diagram.undoState();
        diagram.undoState();

        createdEntityTypes = new ArrayList<>(createdEntityTypes.subList(0, 2));
        createdEntityTypes.add( diagram.addNode(new EntityType()) );

        ArrayList<EntityType> gottenEntityTypes = diagram.getElements(EntityType.class);

        // Check result
        Assertions.assertEquals(gottenEntityTypes, createdEntityTypes);
        Assertions.assertTrue(diagram.getElements(DiagramElement.class).stream().allMatch(DiagramElement::hasOwner));
        Assertions.assertFalse(diagram.canRedoState());
    }

    @Test
    void complexUndoRedoAddRemoveEntityTypes() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        ArrayList<EntityType> createdEntityTypes = new ArrayList<>();

        for (int i=0; i<10; i++) {
            for (int j=0; j<i; j++) {
                createdEntityTypes.add( diagram.addNode(new EntityType()) );
            }

            for (int j=0; j<i-5; j++) {
                createdEntityTypes.remove(diagram.getElements(EntityType.class).get(0));
                diagram.removeNode(diagram.getElements(EntityType.class).get(0));
            }
        }

        while (diagram.canUndoState()) { diagram.undoState(); }
        while (diagram.canRedoState()) { diagram.redoState(); }

        ArrayList<EntityType> gottenEntityTypes = diagram.getElements(EntityType.class);

        // Check result
        Assertions.assertEquals(gottenEntityTypes, createdEntityTypes);
        Assertions.assertTrue(diagram.getElements(DiagramElement.class).stream().allMatch(DiagramElement::hasOwner));
        Assertions.assertFalse(diagram.canRedoState());
    }

    @Test
    void addEntityTypesWithSubtypeConnections() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        ArrayList<EntityType> createdEntityTypes = new ArrayList<>();
        ArrayList<EntityType> gottenEntityTypes;

        for (int i=0; i<5; i++) { createdEntityTypes.add( diagram.addNode(new EntityType()) ); }
        gottenEntityTypes = diagram.getElements(EntityType.class);

        // Check result
        Assertions.assertEquals(gottenEntityTypes, createdEntityTypes);
        Assertions.assertTrue(diagram.getElements(DiagramElement.class).stream().allMatch(DiagramElement::hasOwner));

        Set<String> names     = new HashSet<>();
        Set<String> exp_names = new HashSet<>();

        diagram.getElements(ObjectType.class).forEach(e -> names.add(e.attributes().attribute("name", StringAttribute.class).value()));
        for (int i=0; i<5; i++) { exp_names.add("Entity Type " + (i+1)); }

        Assertions.assertEquals(names, exp_names);
    }
}
