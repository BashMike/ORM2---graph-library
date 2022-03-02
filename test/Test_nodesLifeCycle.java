import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.DiagramElement;
import com.orm2_graph_library.nodes.common.EntityType;
import com.orm2_graph_library.nodes.common.ObjectType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

class Test_nodesLifeCycle {
    @Test
    void addEntityTypes() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        ArrayList<EntityType> createdEntityTypes = new ArrayList<>();
        ArrayList<EntityType> gottenEntityTypes;

        for (int i=0; i<5; i++) { createdEntityTypes.add(diagram.addNode(new EntityType())); }
        gottenEntityTypes = diagram.getElements(EntityType.class).collect(Collectors.toCollection(ArrayList::new));

        // Check result
        Assertions.assertEquals(gottenEntityTypes, createdEntityTypes);
        Assertions.assertTrue(diagram.getElements(DiagramElement.class).allMatch(DiagramElement::hasOwner));

        Set<String> names     = new HashSet<>();
        Set<String> exp_names = new HashSet<>();

        diagram.getElements(ObjectType.class).forEach(e -> names.add(e.name()));
        for (int i=0; i<5; i++) { exp_names.add("Entity Type " + (i+1)); }

        Assertions.assertEquals(names, exp_names);
    }

    @Test
    void undoAddingEntityTypes() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        ArrayList<EntityType> createdEntityTypes = new ArrayList<>();

        for (int i=0; i<5; i++) {
            createdEntityTypes.add(diagram.addNode(new EntityType()));
        }

        diagram.undoState();
        diagram.undoState();
        diagram.undoState();

        createdEntityTypes = new ArrayList<>(createdEntityTypes.subList(0, 2));
        ArrayList<EntityType> gottenEntityTypes = diagram.getElements(EntityType.class).collect(Collectors.toCollection(ArrayList::new));

        // Check result
        Assertions.assertEquals(gottenEntityTypes, createdEntityTypes);
        Assertions.assertTrue(diagram.getElements(DiagramElement.class).allMatch(DiagramElement::hasOwner));
        Assertions.assertTrue(diagram.canRedoState());
    }

    @Test
    void redoAddingEntityTypes() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        ArrayList<EntityType> createdEntityTypes = new ArrayList<>();

        for (int i=0; i<5; i++) { createdEntityTypes.add(diagram.addNode(new EntityType())); }

        diagram.undoState();
        diagram.undoState();
        diagram.undoState();
        diagram.redoState();
        diagram.redoState();
        diagram.redoState();

        ArrayList<EntityType> gottenEntityTypes = diagram.getElements(EntityType.class).collect(Collectors.toCollection(ArrayList::new));

        // Check result
        Assertions.assertEquals(gottenEntityTypes, createdEntityTypes);
        Assertions.assertTrue(diagram.getElements(DiagramElement.class).allMatch(DiagramElement::hasOwner));
        Assertions.assertFalse(diagram.canRedoState());
    }

    @Test
    void addEntityTypesAfterUndo() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        ArrayList<EntityType> createdEntityTypes = new ArrayList<>();

        for (int i=0; i<5; i++) {
            createdEntityTypes.add(diagram.addNode(new EntityType()));
        }

        diagram.undoState();
        diagram.undoState();
        diagram.undoState();

        createdEntityTypes = new ArrayList<>(createdEntityTypes.subList(0, 2));
        createdEntityTypes.add(diagram.addNode(new EntityType()));

        ArrayList<EntityType> gottenEntityTypes = diagram.getElements(EntityType.class).collect(Collectors.toCollection(ArrayList::new));

        // Check result
        Assertions.assertEquals(gottenEntityTypes, createdEntityTypes);
        Assertions.assertTrue(diagram.getElements(DiagramElement.class).allMatch(DiagramElement::hasOwner));
        Assertions.assertFalse(diagram.canRedoState());
    }

    @Test
    void removeEntityType() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        ArrayList<EntityType> createdEntityTypes = new ArrayList<>();

        for (int i=0; i<5; i++) { createdEntityTypes.add(diagram.addNode(new EntityType())); }

        EntityType entityType = diagram.getElements(EntityType.class).findFirst().get();
        createdEntityTypes.remove(entityType);
        diagram.removeNode(entityType);

        createdEntityTypes = new ArrayList<>(createdEntityTypes.subList(0, 4));
        ArrayList<EntityType> gottenEntityTypes = diagram.getElements(EntityType.class).collect(Collectors.toCollection(ArrayList::new));

        // Check result
        Assertions.assertEquals(gottenEntityTypes, createdEntityTypes);
        Assertions.assertTrue(diagram.getElements(DiagramElement.class).allMatch(DiagramElement::hasOwner));
    }

    @Test
    void undoRemovingEntityTypes() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        ArrayList<EntityType> createdEntityTypes = new ArrayList<>();
        ArrayList<EntityType> gottenEntityTypes;

        for (int i=0; i<5; i++) { createdEntityTypes.add(diagram.addNode(new EntityType())); }

        diagram.removeNode(diagram.getElements(EntityType.class).findFirst().get());
        diagram.removeNode(diagram.getElements(EntityType.class).findFirst().get());
        diagram.removeNode(diagram.getElements(EntityType.class).findFirst().get());

        diagram.undoState();
        diagram.undoState();
        diagram.undoState();

        gottenEntityTypes = diagram.getElements(EntityType.class).collect(Collectors.toCollection(ArrayList::new));

        // Check result
        Assertions.assertEquals(new HashSet<>(gottenEntityTypes), new HashSet<>(createdEntityTypes));
        Assertions.assertTrue(diagram.getElements(DiagramElement.class).allMatch(DiagramElement::hasOwner));
        Assertions.assertTrue(diagram.canRedoState());
    }

    @Test
    void redoRemovingEntityTypes() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        ArrayList<EntityType> createdEntityTypes = new ArrayList<>();

        for (int i=0; i<5; i++) { createdEntityTypes.add(diagram.addNode(new EntityType())); }

        for (int i=0; i<3; i++) {
            createdEntityTypes.remove(diagram.getElements(EntityType.class).findFirst().get());
            diagram.removeNode(diagram.getElements(EntityType.class).findFirst().get());
        }

        diagram.undoState();
        diagram.undoState();
        diagram.undoState();
        diagram.redoState();
        diagram.redoState();
        diagram.redoState();

        ArrayList<EntityType> gottenEntityTypes = diagram.getElements(EntityType.class).collect(Collectors.toCollection(ArrayList::new));

        // Check result
        Assertions.assertEquals(gottenEntityTypes, createdEntityTypes);
        Assertions.assertTrue(diagram.getElements(DiagramElement.class).allMatch(DiagramElement::hasOwner));
        Assertions.assertFalse(diagram.canRedoState());
    }

    @Test
    void removeEntityTypesAfterUndo() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        ArrayList<EntityType> createdEntityTypes = new ArrayList<>();
        ArrayList<EntityType> gottenEntityTypes;

        for (int i=0; i<5; i++) { createdEntityTypes.add(diagram.addNode(new EntityType())); }

        diagram.removeNode(diagram.getElements(EntityType.class).findFirst().get());
        diagram.removeNode(diagram.getElements(EntityType.class).findFirst().get());
        diagram.removeNode(diagram.getElements(EntityType.class).findFirst().get());

        diagram.undoState();
        diagram.undoState();
        diagram.undoState();

        createdEntityTypes.remove(diagram.getElements(EntityType.class).findFirst().get());
        diagram.removeNode(diagram.getElements(EntityType.class).findFirst().get());

        gottenEntityTypes = diagram.getElements(EntityType.class).collect(Collectors.toCollection(ArrayList::new));

        // Check result
        Assertions.assertEquals(new HashSet<>(gottenEntityTypes), new HashSet<>(createdEntityTypes));
        Assertions.assertTrue(diagram.getElements(DiagramElement.class).allMatch(DiagramElement::hasOwner));
        Assertions.assertFalse(diagram.canRedoState());
    }

    @Test
    void complexUndoRedoAddRemoveEntityTypes() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        ArrayList<EntityType> createdEntityTypes = new ArrayList<>();

        for (int i=0; i<10; i++) {
            for (int j=0; j<i; j++) {
                createdEntityTypes.add(diagram.addNode(new EntityType()));
            }

            for (int j=0; j<i-5; j++) {
                createdEntityTypes.remove(diagram.getElements(EntityType.class).findFirst().get());
                diagram.removeNode(diagram.getElements(EntityType.class).findFirst().get());
            }
        }

        while (diagram.canUndoState()) { diagram.undoState(); }
        while (diagram.canRedoState()) { diagram.redoState(); }

        ArrayList<EntityType> gottenEntityTypes = diagram.getElements(EntityType.class).collect(Collectors.toCollection(ArrayList::new));

        // Check result
        Assertions.assertEquals(gottenEntityTypes, createdEntityTypes);
        Assertions.assertTrue(diagram.getElements(DiagramElement.class).allMatch(DiagramElement::hasOwner));
        Assertions.assertFalse(diagram.canRedoState());
    }
}
