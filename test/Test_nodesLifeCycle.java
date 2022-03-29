import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.DiagramElement;
import com.orm2_graph_library.core.LogicError;
import com.orm2_graph_library.edges.RoleConstraintRelationEdge;
import com.orm2_graph_library.edges.RoleRelationEdge;
import com.orm2_graph_library.edges.SubtypingConstraintRelationEdge;
import com.orm2_graph_library.edges.SubtypingRelationEdge;
import com.orm2_graph_library.nodes.common.EntityType;
import com.orm2_graph_library.nodes.common.ValueType;
import com.orm2_graph_library.nodes.constraints.Constraint;
import com.orm2_graph_library.nodes.constraints.SubsetConstraint;
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

class Test_nodesLifeCycle extends Test_globalTest {
    // ==================== TEST ====================
    // ---------------- ENTITY TYPES ----------------
    @Test
    void entityType_adding() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }

        // Check result
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof EntityType));
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).anyMatch(e -> e.name().equals("Entity Type 1")));
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).anyMatch(e -> e.name().equals("Entity Type 2")));
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).anyMatch(e -> e.name().equals("Entity Type 3")));
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).anyMatch(e -> e.name().equals("Entity Type 4")));
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).anyMatch(e -> e.name().equals("Entity Type 5")));
    }

    @Test
    void entityType_undoAdding() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        this._diagram.addNode(new EntityType());
        this._diagram.addNode(new EntityType());
        this._diagram.addNode(new EntityType());

        for (int i=0; i<3; i++) { this._diagram.undoState(); }

        // Check result
        Assertions.assertTrue(this._diagram.canRedoState());
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof EntityType));
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).anyMatch(e -> e.name().equals("Entity Type 1")));
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).anyMatch(e -> e.name().equals("Entity Type 2")));
    }

    @Test
    void entityType_redoAdding() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }

        for (int i=0; i<3; i++) { this._diagram.undoState(); }
        for (int i=0; i<3; i++) { this._diagram.redoState(); }

        // Check result
        Assertions.assertFalse(this._diagram.canRedoState());
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof EntityType));
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).anyMatch(e -> e.name().equals("Entity Type 1")));
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).anyMatch(e -> e.name().equals("Entity Type 2")));
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).anyMatch(e -> e.name().equals("Entity Type 3")));
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).anyMatch(e -> e.name().equals("Entity Type 4")));
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).anyMatch(e -> e.name().equals("Entity Type 5")));
    }

    @Test
    void entityType_addingAfterUndo() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        this._diagram.addNode(new EntityType());
        this._diagram.addNode(new EntityType());
        this._diagram.addNode(new EntityType());

        for (int i=0; i<3; i++) { this._diagram.undoState(); }

        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_addDiagramElement(this._diagram.addNode(new EntityType()));

        // Check result
        Assertions.assertFalse(this._diagram.canRedoState());
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof EntityType));
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).anyMatch(e -> e.name().equals("Entity Type 1")));
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).anyMatch(e -> e.name().equals("Entity Type 2")));
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).anyMatch(e -> e.name().equals("Entity Type 3")));
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).anyMatch(e -> e.name().equals("Entity Type 4")));
    }

    @Test
    void entityType_removing() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }

        this._diagram.removeNode(test_entityTypes.get(2));
        this._diagram.removeNode(test_entityTypes.get(0));
        test_removeDiagramElement(test_entityTypes.get(2));
        test_removeDiagramElement(test_entityTypes.get(0));

        // Check result
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof EntityType));
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).anyMatch(e -> e.name().equals("Entity Type 2")));
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).anyMatch(e -> e.name().equals("Entity Type 4")));
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).anyMatch(e -> e.name().equals("Entity Type 5")));
    }

    @Test
    void entityType_undoRemoving() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }

        for (int i=0; i<3; i++) {
            this._diagram.removeNode(test_entityTypes.get(i));
        }

        for (int i=0; i<3; i++) { this._diagram.undoState(); }

        // Check result
        Assertions.assertTrue(this._diagram.canRedoState());
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
        Assertions.assertTrue(diagram.getElements(DiagramElement.class).allMatch(DiagramElement::hasOwnerDiagram));
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
        Assertions.assertTrue(diagram.getElements(DiagramElement.class).allMatch(DiagramElement::hasOwnerDiagram));
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
        Assertions.assertTrue(diagram.getElements(DiagramElement.class).allMatch(DiagramElement::hasOwnerDiagram));
        Assertions.assertFalse(diagram.canRedoState());
    }

    // ================== PREDICATE =================
    @Test
    void predicate_creating() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        Predicate predicate = new Predicate(3);
        diagram.addNode(predicate);

        // Check result
        Assertions.assertEquals(3, predicate.arity());

        Assertions.assertEquals(predicate.getRole(0).borderHeight(), predicate.getRole(1).borderHeight());
        Assertions.assertEquals(predicate.getRole(0).borderWidth(),  predicate.getRole(1).borderWidth());
        Assertions.assertEquals(predicate.getRole(0).borderHeight(), predicate.getRole(1).borderHeight());
        Assertions.assertEquals(predicate.getRole(0).borderWidth(),  predicate.getRole(2).borderWidth());
        Assertions.assertEquals(predicate.getRole(0).borderHeight(), predicate.getRole(2).borderHeight());
        Assertions.assertEquals(predicate.getRole(1).borderWidth(),  predicate.getRole(2).borderWidth());
        Assertions.assertEquals(predicate.getRole(1).borderHeight(), predicate.getRole(2).borderHeight());

        Assertions.assertEquals(predicate.getRole(0).borderWidth() * 3, predicate.borderWidth());
        Assertions.assertEquals(predicate.getRole(0).borderHeight(),    predicate.borderHeight());
    }

    // =========== OBJECTIFIED PREDICATES ===========
    @Test
    void objectifiedPredicate_creating() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        ObjectifiedPredicate objectifiedPredicate = diagram.addNode(new ObjectifiedPredicate(new Predicate(3)));

        objectifiedPredicate.innerPredicate().setOrientation(DiagramElement.Orientation.HORIZONTAL);

        // Check result
        Assertions.assertEquals(3, objectifiedPredicate.innerPredicate().arity());

        Assertions.assertEquals(objectifiedPredicate.innerPredicate().getRole(0).borderHeight(), objectifiedPredicate.innerPredicate().getRole(1).borderHeight());
        Assertions.assertEquals(objectifiedPredicate.innerPredicate().getRole(0).borderWidth(),  objectifiedPredicate.innerPredicate().getRole(1).borderWidth());
        Assertions.assertEquals(objectifiedPredicate.innerPredicate().getRole(0).borderHeight(), objectifiedPredicate.innerPredicate().getRole(1).borderHeight());
        Assertions.assertEquals(objectifiedPredicate.innerPredicate().getRole(0).borderWidth(),  objectifiedPredicate.innerPredicate().getRole(2).borderWidth());
        Assertions.assertEquals(objectifiedPredicate.innerPredicate().getRole(0).borderHeight(), objectifiedPredicate.innerPredicate().getRole(2).borderHeight());
        Assertions.assertEquals(objectifiedPredicate.innerPredicate().getRole(1).borderWidth(),  objectifiedPredicate.innerPredicate().getRole(2).borderWidth());
        Assertions.assertEquals(objectifiedPredicate.innerPredicate().getRole(1).borderHeight(), objectifiedPredicate.innerPredicate().getRole(2).borderHeight());

        Assertions.assertEquals(objectifiedPredicate.innerPredicate().getRole(0).borderWidth() * 3, objectifiedPredicate.innerPredicate().borderWidth());
        Assertions.assertEquals(objectifiedPredicate.innerPredicate().getRole(0).borderHeight(),    objectifiedPredicate.innerPredicate().borderHeight());
    }

    // ================= CONSTRAINTS ================
    @Test
    void constraints_creating() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        Constraint constraint = new SubsetConstraint();
        diagram.addNode(constraint);

        // Check result
        Assertions.assertTrue(constraint.isOwnerDiagram(diagram));
    }
}
