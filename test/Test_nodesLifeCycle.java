import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.DiagramElement;
import com.orm2_graph_library.nodes.common.EntityType;
import com.orm2_graph_library.nodes.common.ObjectType;
import com.orm2_graph_library.nodes.constraints.Constraint;
import com.orm2_graph_library.nodes.constraints.SubsetConstraint;
import com.orm2_graph_library.nodes.predicates.ObjectifiedPredicate;
import com.orm2_graph_library.nodes.predicates.Predicate;
import com.orm2_graph_library.nodes.predicates.Role;
import com.orm2_graph_library.nodes.predicates.StandalonePredicate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

class Test_nodesLifeCycle {
    // ================ ENTITY TYPES ================
    @Test
    void entityType_creating() {
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

    // ================== PREDICATE =================
    @Test
    void predicate_creating() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        Predicate predicate = new StandalonePredicate(3);
        diagram.addNode(predicate);

        // Check result
        Assertions.assertEquals(3, predicate.roles().size());

        Assertions.assertEquals(Role.width(),  predicate.roles().get(0).borderWidth());
        Assertions.assertEquals(Role.height(), predicate.roles().get(0).borderHeight());

        Assertions.assertEquals(predicate.roles().get(0).borderHeight(), predicate.roles().get(1).borderHeight());
        Assertions.assertEquals(predicate.roles().get(0).borderWidth(),  predicate.roles().get(1).borderWidth());
        Assertions.assertEquals(predicate.roles().get(0).borderHeight(), predicate.roles().get(1).borderHeight());
        Assertions.assertEquals(predicate.roles().get(0).borderWidth(),  predicate.roles().get(2).borderWidth());
        Assertions.assertEquals(predicate.roles().get(0).borderHeight(), predicate.roles().get(2).borderHeight());
        Assertions.assertEquals(predicate.roles().get(1).borderWidth(),  predicate.roles().get(2).borderWidth());
        Assertions.assertEquals(predicate.roles().get(1).borderHeight(), predicate.roles().get(2).borderHeight());

        Assertions.assertEquals(predicate.roles().get(0).borderWidth() * 3, predicate.borderWidth());
        Assertions.assertEquals(predicate.roles().get(0).borderHeight(),    predicate.borderHeight());
    }

    // =========== OBJECTIFIED PREDICATES ===========
    @Test
    void objectifiedPredicate_creating() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        ObjectifiedPredicate objectifiedPredicate = new ObjectifiedPredicate(3);
        diagram.addNode(objectifiedPredicate);

        objectifiedPredicate.innerPredicate().setOrientation(DiagramElement.Orientation.HORIZONTAL);

        // Check result
        Assertions.assertEquals(3, objectifiedPredicate.innerPredicate().roles().size());

        Assertions.assertEquals(Role.width(),  objectifiedPredicate.innerPredicate().roles().get(0).borderWidth());
        Assertions.assertEquals(Role.height(), objectifiedPredicate.innerPredicate().roles().get(0).borderHeight());

        Assertions.assertEquals(objectifiedPredicate.innerPredicate().roles().get(0).borderHeight(), objectifiedPredicate.innerPredicate().roles().get(1).borderHeight());
        Assertions.assertEquals(objectifiedPredicate.innerPredicate().roles().get(0).borderWidth(),  objectifiedPredicate.innerPredicate().roles().get(1).borderWidth());
        Assertions.assertEquals(objectifiedPredicate.innerPredicate().roles().get(0).borderHeight(), objectifiedPredicate.innerPredicate().roles().get(1).borderHeight());
        Assertions.assertEquals(objectifiedPredicate.innerPredicate().roles().get(0).borderWidth(),  objectifiedPredicate.innerPredicate().roles().get(2).borderWidth());
        Assertions.assertEquals(objectifiedPredicate.innerPredicate().roles().get(0).borderHeight(), objectifiedPredicate.innerPredicate().roles().get(2).borderHeight());
        Assertions.assertEquals(objectifiedPredicate.innerPredicate().roles().get(1).borderWidth(),  objectifiedPredicate.innerPredicate().roles().get(2).borderWidth());
        Assertions.assertEquals(objectifiedPredicate.innerPredicate().roles().get(1).borderHeight(), objectifiedPredicate.innerPredicate().roles().get(2).borderHeight());

        Assertions.assertEquals(objectifiedPredicate.innerPredicate().roles().get(0).borderWidth() * 3, objectifiedPredicate.innerPredicate().borderWidth());
        Assertions.assertEquals(objectifiedPredicate.innerPredicate().roles().get(0).borderHeight(),    objectifiedPredicate.innerPredicate().borderHeight());

        Assertions.assertEquals(objectifiedPredicate.innerPredicate().borderWidth()  + ObjectifiedPredicate.horizontalEmptyGap() * 2, objectifiedPredicate.borderWidth());
        Assertions.assertEquals(objectifiedPredicate.innerPredicate().borderHeight() + ObjectifiedPredicate.horizontalEmptyGap() * 2, objectifiedPredicate.borderHeight());
    }

    // ================= CONSTRAINTS ================
    @Test
    void constraints_creating() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        Constraint constraint = new SubsetConstraint();
        diagram.addNode(constraint);

        // Check result
        Assertions.assertTrue(constraint.isOwner(diagram));
        Assertions.assertEquals(Constraint.size(), constraint.borderWidth());
        Assertions.assertEquals(Constraint.size(), constraint.borderHeight());
    }
}
