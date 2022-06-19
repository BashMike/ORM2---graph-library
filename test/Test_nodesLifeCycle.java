import com.orm2_graph_library.core.DiagramElement;
import com.orm2_graph_library.core.LogicError;
import com.orm2_graph_library.logic_errors.ConstraintHasNotEnoughConnectsLogicError;
import com.orm2_graph_library.logic_errors.EntityTypeWithNoneRefModeLogicError;
import com.orm2_graph_library.logic_errors.RoleHasNoTextSetLogicError;
import com.orm2_graph_library.logic_errors.ValueTypeWithNoneDataTypeLogicError;
import com.orm2_graph_library.nodes.common.EntityType;
import com.orm2_graph_library.nodes.common.ValueType;
import com.orm2_graph_library.nodes.common.ref_modes.NoneRefMode;
import com.orm2_graph_library.nodes.constraints.*;
import com.orm2_graph_library.nodes.predicates.ObjectifiedPredicate;
import com.orm2_graph_library.nodes.predicates.Predicate;
import com.orm2_graph_library.nodes.predicates.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import utils.Test_globalTest;

import java.util.ArrayList;
import java.util.stream.Collectors;

class Test_nodesLifeCycle extends Test_globalTest {
    // ==================== INIT ====================
    @AfterEach
    private void testEnd_nodesLifeCycle() {
        for (EntityType entityType : test_entityTypes) { test_addLogicErrorTo(entityType, new EntityTypeWithNoneRefModeLogicError(entityType)); }
        for (ValueType valueType : test_valueTypes) { test_addLogicErrorTo(valueType, new ValueTypeWithNoneDataTypeLogicError(valueType)); }
        for (Constraint constraint : test_constraints) { test_addLogicErrorTo(constraint, new ConstraintHasNotEnoughConnectsLogicError(constraint, new ArrayList<>())); }

        for (Predicate predicate : test_predicates) {
            for (Role role : predicate.roles().collect(Collectors.toCollection(ArrayList::new))) {
                LogicError logicError = new RoleHasNoTextSetLogicError(role);

                test_addLogicErrorTo(role.ownerPredicate(), logicError);
                test_addLogicErrorTo(role, logicError);
            }
        }
    }

    // ==================== TEST ====================
    // ---------------- ENTITY TYPES ----------------
    @Test
    void entityType_adding() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }

        // Check result
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof EntityType));
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).allMatch(e -> e.refMode().equals(new NoneRefMode())));
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
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).allMatch(e -> e.refMode().equals(new NoneRefMode())));
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
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).allMatch(e -> e.refMode().equals(new NoneRefMode())));
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
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).allMatch(e -> e.refMode().equals(new NoneRefMode())));
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
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).allMatch(e -> e.refMode().equals(new NoneRefMode())));
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).anyMatch(e -> e.name().equals("Entity Type 2")));
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).anyMatch(e -> e.name().equals("Entity Type 4")));
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).anyMatch(e -> e.name().equals("Entity Type 5")));
    }

    @Test
    void entityType_removingNotInDiagram() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }

        EntityType entityType = new EntityType();
        this._diagram.removeNode(test_entityTypes.get(2));
        this._diagram.removeNode(test_entityTypes.get(0));
        test_removeDiagramElement(test_entityTypes.get(2));
        test_removeDiagramElement(test_entityTypes.get(0));

        // Check result
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof EntityType));
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).allMatch(e -> e.refMode().equals(new NoneRefMode())));
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).anyMatch(e -> e.name().equals("Entity Type 2")));
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).anyMatch(e -> e.name().equals("Entity Type 4")));
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).anyMatch(e -> e.name().equals("Entity Type 5")));

        Assertions.assertThrows(IllegalArgumentException.class, () -> this._diagram.removeNode(entityType));
    }

    @Test
    void entityType_undoRemoving() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        for (int i=0; i<3; i++) { this._diagram.removeNode(test_entityTypes.get(i)); }

        for (int i=0; i<3; i++) { this._diagram.undoState(); }

        // Check result
        Assertions.assertTrue(this._diagram.canRedoState());
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).anyMatch(e -> e.name().equals("Entity Type 1")));
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).anyMatch(e -> e.name().equals("Entity Type 2")));
    }

    @Test
    void entityType_redoRemoving() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }

        for (int i=0; i<3; i++) {
            this._diagram.removeNode(test_entityTypes.get(0));
            test_removeDiagramElement(test_entityTypes.get(0));
        }

        for (int i=0; i<3; i++) { this._diagram.undoState(); }
        for (int i=0; i<3; i++) { this._diagram.redoState(); }

        // Check result
        Assertions.assertFalse(this._diagram.canRedoState());
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof EntityType));
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).allMatch(e -> e.refMode().equals(new NoneRefMode())));
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).anyMatch(e -> e.name().equals("Entity Type 4")));
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).anyMatch(e -> e.name().equals("Entity Type 5")));
    }

    @Test
    void entityType_removingAfterUndo() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        for (int i=2; i<5; i++) { this._diagram.removeNode(test_entityTypes.get(i)); }

        for (int i=0; i<3; i++) { this._diagram.undoState(); }

        this._diagram.removeNode(test_entityTypes.get(3));
        this._diagram.removeNode(test_entityTypes.get(0));
        test_removeDiagramElement(test_entityTypes.get(3));
        test_removeDiagramElement(test_entityTypes.get(0));

        // Check result
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof EntityType));
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).allMatch(e -> e.refMode().equals(new NoneRefMode())));
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).anyMatch(e -> e.name().equals("Entity Type 2")));
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).anyMatch(e -> e.name().equals("Entity Type 3")));
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).anyMatch(e -> e.name().equals("Entity Type 5")));
    }

    @Test
    void entityType_undoWholeActionHistory() {
        // Prepare data and start testing
        for (int i=0; i<10; i++) {
            for (int j=0; j<i; j++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }

            for (int j=0; j<i-5; j++) {
                this._diagram.removeNode(test_entityTypes.get(0));
                test_removeDiagramElement(test_entityTypes.get(0));
            }
        }

        while (this._diagram.canUndoState()) { this._diagram.undoState(); }
        test_removeAllDiagramElements();

        // Check result
        Assertions.assertFalse(this._diagram.canUndoState());
        Assertions.assertTrue(this._diagram.canRedoState());
        Assertions.assertEquals(0, this._diagram.getElements(DiagramElement.class).count());
    }

    @Test
    void entityType_undoRedoWholeActionHistory() {
        // Prepare data and start testing
        for (int i=0; i<10; i++) {
            for (int j=0; j<i; j++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }

            for (int j=0; j<i-5; j++) {
                this._diagram.removeNode(test_entityTypes.get(0));
                test_removeDiagramElement(test_entityTypes.get(0));
            }
        }

        while (this._diagram.canUndoState()) { this._diagram.undoState(); }
        while (this._diagram.canRedoState()) { this._diagram.redoState(); }

        // Check result
        Assertions.assertFalse(this._diagram.canRedoState());
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof EntityType));
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).allMatch(e -> e.refMode().equals(new NoneRefMode())));
        Assertions.assertTrue(this._diagram.getElements(EntityType.class).anyMatch(e -> e.name().startsWith("Entity Type ")));
    }

    @Test
    void entityType_addAddedNode() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EntityType()));

        // Check result
        Assertions.assertThrows(RuntimeException.class, () -> this._diagram.addNode(test_entityTypes.get(0)));
    }

    @Test
    void entityType_removeRemovedNode() {
        // Prepare data and start testing
        EntityType entityType = this._diagram.addNode(new EntityType());
        this._diagram.removeNode(entityType);

        // Check result
        Assertions.assertThrows(RuntimeException.class, () -> this._diagram.removeNode(entityType));
    }

    // ----------------- VALUE TYPES ----------------
    @Test
    void valueType_adding() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new ValueType())); }

        // Check result
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof ValueType));
        Assertions.assertTrue(this._diagram.getElements(ValueType.class).anyMatch(e -> e.name().equals("Value Type 1")));
        Assertions.assertTrue(this._diagram.getElements(ValueType.class).anyMatch(e -> e.name().equals("Value Type 2")));
        Assertions.assertTrue(this._diagram.getElements(ValueType.class).anyMatch(e -> e.name().equals("Value Type 3")));
        Assertions.assertTrue(this._diagram.getElements(ValueType.class).anyMatch(e -> e.name().equals("Value Type 4")));
        Assertions.assertTrue(this._diagram.getElements(ValueType.class).anyMatch(e -> e.name().equals("Value Type 5")));
    }

    @Test
    void valueType_undoAdding() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new ValueType()));
        test_addDiagramElement(this._diagram.addNode(new ValueType()));
        this._diagram.addNode(new ValueType());
        this._diagram.addNode(new ValueType());
        this._diagram.addNode(new ValueType());

        for (int i=0; i<3; i++) { this._diagram.undoState(); }

        // Check result
        Assertions.assertTrue(this._diagram.canRedoState());
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof ValueType));
        Assertions.assertTrue(this._diagram.getElements(ValueType.class).anyMatch(e -> e.name().equals("Value Type 1")));
        Assertions.assertTrue(this._diagram.getElements(ValueType.class).anyMatch(e -> e.name().equals("Value Type 2")));
    }

    @Test
    void valueType_redoAdding() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new ValueType())); }

        for (int i=0; i<3; i++) { this._diagram.undoState(); }
        for (int i=0; i<3; i++) { this._diagram.redoState(); }

        // Check result
        Assertions.assertFalse(this._diagram.canRedoState());
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof ValueType));
        Assertions.assertTrue(this._diagram.getElements(ValueType.class).anyMatch(e -> e.name().equals("Value Type 1")));
        Assertions.assertTrue(this._diagram.getElements(ValueType.class).anyMatch(e -> e.name().equals("Value Type 2")));
        Assertions.assertTrue(this._diagram.getElements(ValueType.class).anyMatch(e -> e.name().equals("Value Type 3")));
        Assertions.assertTrue(this._diagram.getElements(ValueType.class).anyMatch(e -> e.name().equals("Value Type 4")));
        Assertions.assertTrue(this._diagram.getElements(ValueType.class).anyMatch(e -> e.name().equals("Value Type 5")));
    }

    @Test
    void valueType_addingAfterUndo() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new ValueType()));
        test_addDiagramElement(this._diagram.addNode(new ValueType()));
        this._diagram.addNode(new ValueType());
        this._diagram.addNode(new ValueType());
        this._diagram.addNode(new ValueType());

        for (int i=0; i<3; i++) { this._diagram.undoState(); }

        test_addDiagramElement(this._diagram.addNode(new ValueType()));
        test_addDiagramElement(this._diagram.addNode(new ValueType()));

        // Check result
        Assertions.assertFalse(this._diagram.canRedoState());
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof ValueType));
        Assertions.assertTrue(this._diagram.getElements(ValueType.class).anyMatch(e -> e.name().equals("Value Type 1")));
        Assertions.assertTrue(this._diagram.getElements(ValueType.class).anyMatch(e -> e.name().equals("Value Type 2")));
        Assertions.assertTrue(this._diagram.getElements(ValueType.class).anyMatch(e -> e.name().equals("Value Type 3")));
        Assertions.assertTrue(this._diagram.getElements(ValueType.class).anyMatch(e -> e.name().equals("Value Type 4")));
    }

    @Test
    void valueType_removing() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new ValueType())); }

        this._diagram.removeNode(test_valueTypes.get(2));
        this._diagram.removeNode(test_valueTypes.get(0));
        test_removeDiagramElement(test_valueTypes.get(2));
        test_removeDiagramElement(test_valueTypes.get(0));

        // Check result
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof ValueType));
        Assertions.assertTrue(this._diagram.getElements(ValueType.class).anyMatch(e -> e.name().equals("Value Type 2")));
        Assertions.assertTrue(this._diagram.getElements(ValueType.class).anyMatch(e -> e.name().equals("Value Type 4")));
        Assertions.assertTrue(this._diagram.getElements(ValueType.class).anyMatch(e -> e.name().equals("Value Type 5")));
    }

    @Test
    void valueType_undoRemoving() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new ValueType())); }
        for (int i=0; i<3; i++) { this._diagram.removeNode(test_valueTypes.get(i)); }

        for (int i=0; i<3; i++) { this._diagram.undoState(); }

        // Check result
        Assertions.assertTrue(this._diagram.canRedoState());
        Assertions.assertTrue(this._diagram.getElements(ValueType.class).anyMatch(e -> e.name().equals("Value Type 1")));
        Assertions.assertTrue(this._diagram.getElements(ValueType.class).anyMatch(e -> e.name().equals("Value Type 2")));
    }

    @Test
    void valueType_redoRemoving() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new ValueType())); }

        for (int i=0; i<3; i++) {
            this._diagram.removeNode(test_valueTypes.get(0));
            test_removeDiagramElement(test_valueTypes.get(0));
        }

        for (int i=0; i<3; i++) { this._diagram.undoState(); }
        for (int i=0; i<3; i++) { this._diagram.redoState(); }

        // Check result
        Assertions.assertFalse(this._diagram.canRedoState());
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof ValueType));
        Assertions.assertTrue(this._diagram.getElements(ValueType.class).anyMatch(e -> e.name().equals("Value Type 4")));
        Assertions.assertTrue(this._diagram.getElements(ValueType.class).anyMatch(e -> e.name().equals("Value Type 5")));
    }

    @Test
    void valueType_removingAfterUndo() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new ValueType())); }
        for (int i=2; i<5; i++) { this._diagram.removeNode(test_valueTypes.get(i)); }

        for (int i=0; i<3; i++) { this._diagram.undoState(); }

        this._diagram.removeNode(test_valueTypes.get(3));
        this._diagram.removeNode(test_valueTypes.get(0));
        test_removeDiagramElement(test_valueTypes.get(3));
        test_removeDiagramElement(test_valueTypes.get(0));

        // Check result
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof ValueType));
        Assertions.assertTrue(this._diagram.getElements(ValueType.class).anyMatch(e -> e.name().equals("Value Type 2")));
        Assertions.assertTrue(this._diagram.getElements(ValueType.class).anyMatch(e -> e.name().equals("Value Type 3")));
        Assertions.assertTrue(this._diagram.getElements(ValueType.class).anyMatch(e -> e.name().equals("Value Type 5")));
    }

    @Test
    void valueType_undoWholeActionHistory() {
        // Prepare data and start testing
        for (int i=0; i<10; i++) {
            for (int j=0; j<i; j++) { test_addDiagramElement(this._diagram.addNode(new ValueType())); }

            for (int j=0; j<i-5; j++) {
                this._diagram.removeNode(test_valueTypes.get(0));
                test_removeDiagramElement(test_valueTypes.get(0));
            }
        }

        while (this._diagram.canUndoState()) { this._diagram.undoState(); }
        test_removeAllDiagramElements();

        // Check result
        Assertions.assertFalse(this._diagram.canUndoState());
        Assertions.assertTrue(this._diagram.canRedoState());
        Assertions.assertEquals(0, this._diagram.getElements(DiagramElement.class).count());
    }

    @Test
    void valueType_undoRedoWholeActionHistory() {
        // Prepare data and start testing
        for (int i=0; i<10; i++) {
            for (int j=0; j<i; j++) { test_addDiagramElement(this._diagram.addNode(new ValueType())); }

            for (int j=0; j<i-5; j++) {
                this._diagram.removeNode(test_valueTypes.get(0));
                test_removeDiagramElement(test_valueTypes.get(0));
            }
        }

        while (this._diagram.canUndoState()) { this._diagram.undoState(); }
        while (this._diagram.canRedoState()) { this._diagram.redoState(); }

        // Check result
        Assertions.assertFalse(this._diagram.canRedoState());
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof ValueType));
        Assertions.assertTrue(this._diagram.getElements(ValueType.class).anyMatch(e -> e.name().startsWith("Value Type ")));
    }

    @Test
    void valueType_addAddedNode() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new ValueType()));

        // Check result
        Assertions.assertThrows(RuntimeException.class, () -> this._diagram.addNode(test_valueTypes.get(0)));
    }

    @Test
    void valueType_removeRemovedNode() {
        // Prepare data and start testing
        ValueType valueType = this._diagram.addNode(new ValueType());
        this._diagram.removeNode(valueType);

        // Check result
        Assertions.assertThrows(RuntimeException.class, () -> this._diagram.removeNode(valueType));
    }

    // ------------------ PREDICATE -----------------
    @Test
    void predicate_adding() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new Predicate(i+1))); }

        // Check result
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof Predicate || e instanceof Role));
        Assertions.assertTrue(this._diagram.getElements(Predicate.class).allMatch(e -> e.roles().allMatch(e1 -> e1.ownerPredicate() == e)));
        Assertions.assertTrue(this._diagram.getElements(Predicate.class).allMatch(e -> e.arity() == 1 || e.arity() == 2 || e.arity() == 3 || e.arity() == 4 || e.arity() == 5));
    }

    @Test
    void predicate_undoAdding() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(1)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(2)));
        this._diagram.addNode(new Predicate(3));
        this._diagram.addNode(new Predicate(4));
        this._diagram.addNode(new Predicate(5));

        for (int i=0; i<3; i++) { this._diagram.undoState(); }

        // Check result
        Assertions.assertTrue(this._diagram.canRedoState());
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof Predicate || e instanceof Role));
        Assertions.assertTrue(this._diagram.getElements(Predicate.class).allMatch(e -> e.roles().allMatch(e1 -> e1.ownerPredicate() == e)));
        Assertions.assertTrue(this._diagram.getElements(Predicate.class).allMatch(e -> e.arity() == 1 || e.arity() == 2));
    }

    @Test
    void predicate_redoAdding() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new Predicate(i+1))); }

        for (int i=0; i<3; i++) { this._diagram.undoState(); }
        for (int i=0; i<3; i++) { this._diagram.redoState(); }

        // Check result
        Assertions.assertFalse(this._diagram.canRedoState());
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof Predicate || e instanceof Role));
        Assertions.assertTrue(this._diagram.getElements(Predicate.class).allMatch(e -> e.roles().allMatch(e1 -> e1.ownerPredicate() == e)));
        Assertions.assertTrue(this._diagram.getElements(Predicate.class).allMatch(e -> e.arity() == 1 || e.arity() == 2 || e.arity() == 3 || e.arity() == 4 || e.arity() == 5));
    }

    @Test
    void predicate_addingAfterUndo() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(1)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(2)));
        this._diagram.addNode(new Predicate(3));
        this._diagram.addNode(new Predicate(4));
        this._diagram.addNode(new Predicate(5));

        for (int i=0; i<3; i++) { this._diagram.undoState(); }

        test_addDiagramElement(this._diagram.addNode(new Predicate(6)));
        test_addDiagramElement(this._diagram.addNode(new Predicate(7)));

        // Check result
        Assertions.assertFalse(this._diagram.canRedoState());
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof Predicate || e instanceof Role));
        Assertions.assertTrue(this._diagram.getElements(Predicate.class).allMatch(e -> e.roles().allMatch(e1 -> e1.ownerPredicate() == e)));
        Assertions.assertTrue(this._diagram.getElements(Predicate.class).allMatch(e -> e.arity() == 1 || e.arity() == 2 || e.arity() == 6 || e.arity() == 7));
    }

    @Test
    void predicate_removing() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new Predicate(i+1))); }

        this._diagram.removeNode(test_predicates.get(2));
        this._diagram.removeNode(test_predicates.get(0));
        test_removeDiagramElement(test_predicates.get(2));
        test_removeDiagramElement(test_predicates.get(0));

        // Check result
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof Predicate || e instanceof Role));
        Assertions.assertTrue(this._diagram.getElements(Predicate.class).allMatch(e -> e.roles().allMatch(e1 -> e1.ownerPredicate() == e)));
        Assertions.assertTrue(this._diagram.getElements(Predicate.class).allMatch(e -> e.arity() == 2 || e.arity() == 4 || e.arity() == 5));
    }

    @Test
    void predicate_undoRemoving() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new Predicate(i+1))); }
        for (int i=0; i<3; i++) { this._diagram.removeNode(test_predicates.get(i)); }

        for (int i=0; i<3; i++) { this._diagram.undoState(); }

        // Check result
        Assertions.assertTrue(this._diagram.canRedoState());
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof Predicate || e instanceof Role));
        Assertions.assertTrue(this._diagram.getElements(Predicate.class).allMatch(e -> e.roles().allMatch(e1 -> e1.ownerPredicate() == e)));
        Assertions.assertTrue(this._diagram.getElements(Predicate.class).allMatch(e -> e.arity() == 1 || e.arity() == 2 || e.arity() == 3 || e.arity() == 4 || e.arity() == 5));
    }

    @Test
    void predicate_redoRemoving() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new Predicate(i+1))); }

        for (int i=0; i<3; i++) {
            this._diagram.removeNode(test_predicates.get(0));
            test_removeDiagramElement(test_predicates.get(0));
        }

        for (int i=0; i<3; i++) { this._diagram.undoState(); }
        for (int i=0; i<3; i++) { this._diagram.redoState(); }

        // Check result
        Assertions.assertFalse(this._diagram.canRedoState());
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof Predicate || e instanceof Role));
        Assertions.assertTrue(this._diagram.getElements(Predicate.class).allMatch(e -> e.roles().allMatch(e1 -> e1.ownerPredicate() == e)));
        Assertions.assertTrue(this._diagram.getElements(Predicate.class).allMatch(e -> e.arity() == 1 || e.arity() == 2 || e.arity() == 3 || e.arity() == 4 || e.arity() == 5));
    }

    @Test
    void predicate_removingAfterUndo() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new Predicate(i+1))); }
        for (int i=2; i<5; i++) { this._diagram.removeNode(test_predicates.get(i)); }

        for (int i=0; i<3; i++) { this._diagram.undoState(); }

        this._diagram.removeNode(test_predicates.get(3));
        this._diagram.removeNode(test_predicates.get(0));
        test_removeDiagramElement(test_predicates.get(3));
        test_removeDiagramElement(test_predicates.get(0));

        // Check result
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof Predicate || e instanceof Role));
        Assertions.assertTrue(this._diagram.getElements(Predicate.class).allMatch(e -> e.roles().allMatch(e1 -> e1.ownerPredicate() == e)));
        Assertions.assertTrue(this._diagram.getElements(Predicate.class).allMatch(e -> e.arity() == 1 || e.arity() == 2 || e.arity() == 3 || e.arity() == 4 || e.arity() == 5));
    }

    @Test
    void predicate_undoWholeActionHistory() {
        // Prepare data and start testing
        for (int i=0; i<10; i++) {
            for (int j=0; j<i; j++) { test_addDiagramElement(this._diagram.addNode(new Predicate(j+1))); }

            for (int j=0; j<i-5; j++) {
                this._diagram.removeNode(test_predicates.get(0));
                test_removeDiagramElement(test_predicates.get(0));
            }
        }

        while (this._diagram.canUndoState()) {
            this._diagram.undoState();
        }

        test_removeAllDiagramElements();

        // Check result
        Assertions.assertFalse(this._diagram.canUndoState());
        Assertions.assertTrue(this._diagram.canRedoState());
        Assertions.assertEquals(0, this._diagram.getElements(DiagramElement.class).count());
    }

    @Test
    void predicate_undoRedoWholeActionHistory() {
        // Prepare data and start testing
        for (int i=0; i<10; i++) {
            for (int j=0; j<i; j++) { test_addDiagramElement(this._diagram.addNode(new Predicate(j+1))); }

            for (int j=0; j<i-5; j++) {
                this._diagram.removeNode(test_predicates.get(0));
                test_removeDiagramElement(test_predicates.get(0));
            }
        }

        while (this._diagram.canUndoState()) { this._diagram.undoState(); }
        while (this._diagram.canRedoState()) { this._diagram.redoState(); }

        // Check result
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof Predicate || e instanceof Role));
        Assertions.assertTrue(this._diagram.getElements(Predicate.class).allMatch(e -> e.roles().allMatch(e1 -> e1.ownerPredicate() == e)));
        Assertions.assertTrue(this._diagram.getElements(Predicate.class).allMatch(e -> e.arity() > 0));
    }

    @Test
    void predicate_errorWhenPredicateArityIsZero() {
        // Prepare data, start testing and check result
        Assertions.assertThrows(RuntimeException.class, () -> this._diagram.addNode(new Predicate(0)));
    }

    @Test
    void predicate_errorWhenPredicateArityIsNegative() {
        // Prepare data, start testing and check result
        Assertions.assertThrows(RuntimeException.class, () -> this._diagram.addNode(new Predicate(-4)));
    }

    @Test
    void predicate_errorWhenPredicateArityIsTheLowestNegative() {
        // Prepare data, start testing and check result
        Assertions.assertThrows(RuntimeException.class, () -> this._diagram.addNode(new Predicate(Integer.MIN_VALUE)));
    }

    @Test
    void predicate_errorWhenPredicateArityIsOverflowedTheLowestNegative() {
        // Prepare data, start testing and check result
        // Assertions.assertThrows(OutOfMemoryError.class, () -> this._diagram.addNode(new Predicate(Integer.MIN_VALUE-1)));
    }

    @Test
    void predicate_errorWhenPredicateArityIsTheHighestPositive() {
        // Prepare data, start testing and check result
        // Assertions.assertThrows(OutOfMemoryError.class, () -> this._diagram.addNode(new Predicate(Integer.MAX_VALUE)));
    }

    @Test
    void predicate_errorWhenPredicateArityIsOverflowedTheHighestPositive() {
        // Prepare data, start testing and check result
        Assertions.assertThrows(RuntimeException.class, () -> this._diagram.addNode(new Predicate(Integer.MAX_VALUE+1)));
    }

    @Test
    void predicate_errorWhenRemovingOnlyRole() {
        // Prepare data, start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));

        // Check result
        Assertions.assertThrows(RuntimeException.class, () -> this._diagram.removeNode(test_predicates.get(0).getRole(0)));
    }

    @Test
    void predicate_addAddedNodeAndItsComponents() {
        // Prepare data and start testing
        Predicate predicate = test_addDiagramElement(this._diagram.addNode(new Predicate(3)));

        // Check result
        Assertions.assertThrows(RuntimeException.class, () -> this._diagram.addNode(predicate));
        predicate.roles().forEach(e -> Assertions.assertThrows(RuntimeException.class, () -> this._diagram.removeNode(e)));
    }

    @Test
    void predicate_removeRemovedNodeAndItsComponents() {
        // Prepare data and start testing
        Predicate predicate = this._diagram.addNode(new Predicate(3));
        this._diagram.removeNode(predicate);

        // Check result
        Assertions.assertThrows(RuntimeException.class, () -> this._diagram.removeNode(predicate));
        predicate.roles().forEach(e -> Assertions.assertThrows(RuntimeException.class, () -> this._diagram.removeNode(e)));
    }

    // ----------- OBJECTIFIED PREDICATES -----------
    @Test
    void objectifiedPredicate_adding() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(i+1)))); }

        // Check result
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof ObjectifiedPredicate ||
                e instanceof Predicate || e instanceof Role));

        Assertions.assertTrue(this._diagram.getElements(ObjectifiedPredicate.class).allMatch(e -> e.innerPredicate().ownerObjectifiedPredicate() == e));
        Assertions.assertTrue(this._diagram.getElements(ObjectifiedPredicate.class).allMatch(e -> e.innerPredicate().roles()
                .allMatch(e1 -> e1.ownerPredicate().ownerObjectifiedPredicate() == e)));
        Assertions.assertTrue(this._diagram.getElements(ObjectifiedPredicate.class)
                .allMatch(e -> e.innerPredicate().arity() == 1 ||
                               e.innerPredicate().arity() == 2 ||
                               e.innerPredicate().arity() == 3 ||
                               e.innerPredicate().arity() == 4 ||
                               e.innerPredicate().arity() == 5));

        Assertions.assertTrue(this._diagram.getElements(Predicate.class).allMatch(e -> e.roles().allMatch(e1 -> e1.ownerPredicate() == e)));
        Assertions.assertTrue(this._diagram.getElements(Predicate.class).allMatch(e -> e.arity() == 1 || e.arity() == 2 || e.arity() == 3 || e.arity() == 4 || e.arity() == 5));
    }

    @Test
    void objectifiedPredicate_undoAdding() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(1))));
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(2))));
        this._diagram.addNode(new ObjectifiedPredicate(new Predicate(3)));
        this._diagram.addNode(new ObjectifiedPredicate(new Predicate(4)));
        this._diagram.addNode(new ObjectifiedPredicate(new Predicate(5)));

        for (int i=0; i<3; i++) { this._diagram.undoState(); }

        // Check result
        Assertions.assertTrue(this._diagram.canRedoState());
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof ObjectifiedPredicate ||
                e instanceof Predicate || e instanceof Role));

        Assertions.assertTrue(this._diagram.getElements(ObjectifiedPredicate.class).allMatch(e -> e.innerPredicate().ownerObjectifiedPredicate() == e));
        Assertions.assertTrue(this._diagram.getElements(ObjectifiedPredicate.class).allMatch(e -> e.innerPredicate().roles()
                .allMatch(e1 -> e1.ownerPredicate().ownerObjectifiedPredicate() == e)));
        Assertions.assertTrue(this._diagram.getElements(ObjectifiedPredicate.class).allMatch(e -> e.innerPredicate().arity() == 1 || e.innerPredicate().arity() == 2));

        Assertions.assertTrue(this._diagram.getElements(Predicate.class).allMatch(e -> e.roles().allMatch(e1 -> e1.ownerPredicate() == e)));
        Assertions.assertTrue(this._diagram.getElements(Predicate.class).allMatch(e -> e.arity() == 1 || e.arity() == 2));
    }

    @Test
    void objectifiedPredicate_redoAdding() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(i+1)))); }

        for (int i=0; i<3; i++) { this._diagram.undoState(); }
        for (int i=0; i<3; i++) { this._diagram.redoState(); }

        // Check result
        Assertions.assertFalse(this._diagram.canRedoState());
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof ObjectifiedPredicate ||
                e instanceof Predicate || e instanceof Role));

        Assertions.assertTrue(this._diagram.getElements(ObjectifiedPredicate.class).allMatch(e -> e.innerPredicate().ownerObjectifiedPredicate() == e));
        Assertions.assertTrue(this._diagram.getElements(ObjectifiedPredicate.class).allMatch(e -> e.innerPredicate().roles()
                .allMatch(e1 -> e1.ownerPredicate().ownerObjectifiedPredicate() == e)));
        Assertions.assertTrue(this._diagram.getElements(ObjectifiedPredicate.class)
                .allMatch(e -> e.innerPredicate().arity() == 1 ||
                               e.innerPredicate().arity() == 2 ||
                               e.innerPredicate().arity() == 3 ||
                               e.innerPredicate().arity() == 4 ||
                               e.innerPredicate().arity() == 5));

        Assertions.assertTrue(this._diagram.getElements(Predicate.class).allMatch(e -> e.roles().allMatch(e1 -> e1.ownerPredicate() == e)));
        Assertions.assertTrue(this._diagram.getElements(Predicate.class).allMatch(e -> e.arity() == 1 || e.arity() == 2 || e.arity() == 3 || e.arity() == 4 || e.arity() == 5));
    }

    @Test
    void objectifiedPredicate_addingAfterUndo() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(1))));
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(2))));
        this._diagram.addNode(new ObjectifiedPredicate(new Predicate(3)));
        this._diagram.addNode(new ObjectifiedPredicate(new Predicate(4)));
        this._diagram.addNode(new ObjectifiedPredicate(new Predicate(5)));

        for (int i=0; i<3; i++) { this._diagram.undoState(); }

        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(6))));
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(7))));

        // Check result
        Assertions.assertFalse(this._diagram.canRedoState());
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof ObjectifiedPredicate ||
                e instanceof Predicate || e instanceof Role));

        Assertions.assertTrue(this._diagram.getElements(ObjectifiedPredicate.class).allMatch(e -> e.innerPredicate().ownerObjectifiedPredicate() == e));
        Assertions.assertTrue(this._diagram.getElements(ObjectifiedPredicate.class).allMatch(e -> e.innerPredicate().roles()
                .allMatch(e1 -> e1.ownerPredicate().ownerObjectifiedPredicate() == e)));
        Assertions.assertTrue(this._diagram.getElements(ObjectifiedPredicate.class)
                .allMatch(e -> e.innerPredicate().arity() == 1 ||
                               e.innerPredicate().arity() == 2 ||
                               e.innerPredicate().arity() == 6 ||
                               e.innerPredicate().arity() == 7));

        Assertions.assertTrue(this._diagram.getElements(Predicate.class).allMatch(e -> e.roles().allMatch(e1 -> e1.ownerPredicate() == e)));
        Assertions.assertTrue(this._diagram.getElements(Predicate.class).allMatch(e -> e.arity() == 1 || e.arity() == 2 || e.arity() == 6 || e.arity() == 7));
    }

    @Test
    void objectifiedPredicate_removing() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(i+1)))); }

        this._diagram.removeNode(test_objectifiedPredicates.get(2));
        this._diagram.removeNode(test_objectifiedPredicates.get(0));
        test_removeDiagramElement(test_objectifiedPredicates.get(2));
        test_removeDiagramElement(test_objectifiedPredicates.get(0));

        // Check result
        Assertions.assertFalse(this._diagram.canRedoState());
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof ObjectifiedPredicate ||
                e instanceof Predicate || e instanceof Role));

        Assertions.assertTrue(this._diagram.getElements(ObjectifiedPredicate.class).allMatch(e -> e.innerPredicate().ownerObjectifiedPredicate() == e));
        Assertions.assertTrue(this._diagram.getElements(ObjectifiedPredicate.class).allMatch(e -> e.innerPredicate().roles()
                .allMatch(e1 -> e1.ownerPredicate().ownerObjectifiedPredicate() == e)));
        Assertions.assertTrue(this._diagram.getElements(ObjectifiedPredicate.class)
                .allMatch(e -> e.innerPredicate().arity() == 2 ||
                        e.innerPredicate().arity() == 4 ||
                        e.innerPredicate().arity() == 5));

        Assertions.assertTrue(this._diagram.getElements(Predicate.class).allMatch(e -> e.roles().allMatch(e1 -> e1.ownerPredicate() == e)));
        Assertions.assertTrue(this._diagram.getElements(Predicate.class).allMatch(e -> e.arity() == 2 || e.arity() == 4 || e.arity() == 5));
    }

    @Test
    void objectifiedPredicate_undoRemoving() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(i+1)))); }
        for (int i=0; i<3; i++) { this._diagram.removeNode(test_objectifiedPredicates.get(i)); }

        for (int i=0; i<3; i++) { this._diagram.undoState(); }

        // Check result
        Assertions.assertTrue(this._diagram.canRedoState());
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof ObjectifiedPredicate ||
                e instanceof Predicate || e instanceof Role));

        Assertions.assertTrue(this._diagram.getElements(ObjectifiedPredicate.class).allMatch(e -> e.innerPredicate().ownerObjectifiedPredicate() == e));
        Assertions.assertTrue(this._diagram.getElements(ObjectifiedPredicate.class).allMatch(e -> e.innerPredicate().roles()
                .allMatch(e1 -> e1.ownerPredicate().ownerObjectifiedPredicate() == e)));
        Assertions.assertTrue(this._diagram.getElements(ObjectifiedPredicate.class)
                .allMatch(e -> e.innerPredicate().arity() == 1 ||
                               e.innerPredicate().arity() == 2 ||
                               e.innerPredicate().arity() == 3 ||
                               e.innerPredicate().arity() == 4 ||
                               e.innerPredicate().arity() == 5));

        Assertions.assertTrue(this._diagram.getElements(Predicate.class).allMatch(e -> e.roles().allMatch(e1 -> e1.ownerPredicate() == e)));
        Assertions.assertTrue(this._diagram.getElements(Predicate.class).allMatch(e -> e.arity() == 1 || e.arity() == 2 || e.arity() == 3 || e.arity() == 4 || e.arity() == 5));
    }

    @Test
    void objectifiedPredicate_redoRemoving() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(i+1)))); }

        for (int i=0; i<3; i++) {
            this._diagram.removeNode(test_objectifiedPredicates.get(0));
            test_removeDiagramElement(test_objectifiedPredicates.get(0));
        }

        for (int i=0; i<3; i++) { this._diagram.undoState(); }
        for (int i=0; i<3; i++) { this._diagram.redoState(); }

        // Check result
        Assertions.assertFalse(this._diagram.canRedoState());
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof ObjectifiedPredicate ||
                e instanceof Predicate || e instanceof Role));

        Assertions.assertTrue(this._diagram.getElements(ObjectifiedPredicate.class).allMatch(e -> e.innerPredicate().ownerObjectifiedPredicate() == e));
        Assertions.assertTrue(this._diagram.getElements(ObjectifiedPredicate.class).allMatch(e -> e.innerPredicate().roles()
                .allMatch(e1 -> e1.ownerPredicate().ownerObjectifiedPredicate() == e)));
        Assertions.assertTrue(this._diagram.getElements(ObjectifiedPredicate.class)
                .allMatch(e -> e.innerPredicate().arity() == 2 ||
                        e.innerPredicate().arity() == 4 ||
                        e.innerPredicate().arity() == 5));

        Assertions.assertTrue(this._diagram.getElements(Predicate.class).allMatch(e -> e.roles().allMatch(e1 -> e1.ownerPredicate() == e)));
        Assertions.assertTrue(this._diagram.getElements(Predicate.class).allMatch(e -> e.arity() == 2 || e.arity() == 4 || e.arity() == 5));
    }

    @Test
    void objectifiedPredicate_removingAfterUndo() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(i+1)))); }
        for (int i=0; i<3; i++) { this._diagram.removeNode(test_objectifiedPredicates.get(i)); }

        for (int i=0; i<3; i++) { this._diagram.undoState(); }

        this._diagram.removeNode(test_objectifiedPredicates.get(3));
        this._diagram.removeNode(test_objectifiedPredicates.get(0));
        test_removeDiagramElement(test_objectifiedPredicates.get(3));
        test_removeDiagramElement(test_objectifiedPredicates.get(0));

        // Check result
        Assertions.assertFalse(this._diagram.canRedoState());
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof ObjectifiedPredicate ||
                e instanceof Predicate || e instanceof Role));

        Assertions.assertTrue(this._diagram.getElements(ObjectifiedPredicate.class).allMatch(e -> e.innerPredicate().ownerObjectifiedPredicate() == e));
        Assertions.assertTrue(this._diagram.getElements(ObjectifiedPredicate.class).allMatch(e -> e.innerPredicate().roles()
                .allMatch(e1 -> e1.ownerPredicate().ownerObjectifiedPredicate() == e)));
        Assertions.assertTrue(this._diagram.getElements(ObjectifiedPredicate.class)
                .allMatch(e -> e.innerPredicate().arity() == 2 ||
                        e.innerPredicate().arity() == 3 ||
                        e.innerPredicate().arity() == 5));

        Assertions.assertTrue(this._diagram.getElements(Predicate.class).allMatch(e -> e.roles().allMatch(e1 -> e1.ownerPredicate() == e)));
        Assertions.assertTrue(this._diagram.getElements(Predicate.class).allMatch(e -> e.arity() == 2 || e.arity() == 3 || e.arity() == 5));
    }

    @Test
    void objectifiedPredicate_undoWholeActionHistory() {
        // Prepare data and start testing
        for (int i=0; i<10; i++) {
            for (int j=0; j<i; j++) { test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(j+1)))); }

            for (int j=0; j<i-5; j++) {
                this._diagram.removeNode(test_objectifiedPredicates.get(0));
                test_removeDiagramElement(test_objectifiedPredicates.get(0));
            }
        }

        while (this._diagram.canUndoState()) { this._diagram.undoState(); }
        test_removeAllDiagramElements();

        // Check result
        Assertions.assertFalse(this._diagram.canUndoState());
        Assertions.assertTrue(this._diagram.canRedoState());
        Assertions.assertEquals(0, this._diagram.getElements(DiagramElement.class).count());
    }

    @Test
    void objectifiedPredicate_undoRedoWholeActionHistory() {
        // Prepare data and start testing
        for (int i=0; i<10; i++) {
            for (int j=0; j<i; j++) { test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(j+1)))); }

            for (int j=0; j<i-5; j++) {
                this._diagram.removeNode(test_objectifiedPredicates.get(0));
                test_removeDiagramElement(test_objectifiedPredicates.get(0));
            }
        }

        while (this._diagram.canUndoState()) { this._diagram.undoState(); }
        while (this._diagram.canRedoState()) { this._diagram.redoState(); }

        // Check result
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof ObjectifiedPredicate ||
                e instanceof Predicate || e instanceof Role));

        Assertions.assertTrue(this._diagram.getElements(ObjectifiedPredicate.class).allMatch(e -> e.innerPredicate().ownerObjectifiedPredicate() == e));
        Assertions.assertTrue(this._diagram.getElements(ObjectifiedPredicate.class).allMatch(e -> e.innerPredicate().roles()
                .allMatch(e1 -> e1.ownerPredicate().ownerObjectifiedPredicate() == e)));
        Assertions.assertTrue(this._diagram.getElements(ObjectifiedPredicate.class).allMatch(e -> e.innerPredicate().arity() > 0));

        Assertions.assertTrue(this._diagram.getElements(Predicate.class).allMatch(e -> e.roles().allMatch(e1 -> e1.ownerPredicate() == e)));
        Assertions.assertTrue(this._diagram.getElements(Predicate.class).allMatch(e -> e.arity() > 0));
    }

    @Test
    void objectifiedPredicate_errorWhenPredicateArityIsZero() {
        // Prepare data, start testing and check result
        Assertions.assertThrows(RuntimeException.class, () -> this._diagram.addNode(new ObjectifiedPredicate(new Predicate(0))));
    }

    @Test
    void objectifiedPredicate_errorWhenPredicateArityIsNegative() {
        // Prepare data, start testing and check result
        Assertions.assertThrows(RuntimeException.class, () -> this._diagram.addNode(new ObjectifiedPredicate(new Predicate(-4))));
    }

    @Test
    void objectifiedPredicate_errorWhenPredicateArityIsTheLowestNegative() {
        // Prepare data, start testing and check result
        Assertions.assertThrows(RuntimeException.class, () -> this._diagram.addNode(new ObjectifiedPredicate(new Predicate(Integer.MIN_VALUE))));
    }

    @Test
    void objectifiedPredicate_errorWhenPredicateArityIsOverflowedTheLowestNegative() {
        // Prepare data, start testing and check result
        // Assertions.assertThrows(OutOfMemoryError.class, () -> this._diagram.addNode(new Predicate(Integer.MIN_VALUE-1)));
    }

    @Test
    void objectifiedPredicate_errorWhenPredicateArityIsTheHighestPositive() {
        // Prepare data, start testing and check result
        // Assertions.assertThrows(OutOfMemoryError.class, () -> this._diagram.addNode(new Predicate(Integer.MAX_VALUE)));
    }

    @Test
    void objectifiedPredicate_errorWhenPredicateArityIsOverflowedTheHighestPositive() {
        // Prepare data, start testing and check result
        Assertions.assertThrows(RuntimeException.class, () -> this._diagram.addNode(new ObjectifiedPredicate(new Predicate(Integer.MAX_VALUE+1))));
    }

    @Test
    void objectifiedPredicate_errorWhenRemovingOnlyInnerPredicate() {
        // Prepare data, start testing
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(3))));

        // Check result
        Assertions.assertThrows(RuntimeException.class, () -> this._diagram.removeNode(test_objectifiedPredicates.get(0).innerPredicate()));
    }

    @Test
    void objectifiedPredicate_errorWhenRemovingOnlyInnerPredicateRole() {
        // Prepare data, start testing
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(3))));

        // Check result
        Assertions.assertThrows(RuntimeException.class, () -> this._diagram.removeNode(test_objectifiedPredicates.get(0).innerPredicate().getRole(0)));
    }

    @Test
    void objectifiedPredicate_addAddedNodeAndItsComponents() {
        // Prepare data and start testing
        ObjectifiedPredicate objectifiedPredicate = test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(3))));

        // Check result
        Assertions.assertThrows(RuntimeException.class, () -> this._diagram.addNode(objectifiedPredicate));
        Assertions.assertThrows(RuntimeException.class, () -> this._diagram.addNode(objectifiedPredicate.innerPredicate()));
        objectifiedPredicate.innerPredicate().roles().forEach(e -> Assertions.assertThrows(RuntimeException.class, () -> this._diagram.removeNode(e)));
    }

    @Test
    void objectifiedPredicate_removeRemovedNodeAndItsComponents() {
        // Prepare data and start testing
        ObjectifiedPredicate objectifiedPredicate = this._diagram.addNode(new ObjectifiedPredicate(new Predicate(3)));
        this._diagram.removeNode(objectifiedPredicate);

        // Check result
        Assertions.assertThrows(RuntimeException.class, () -> this._diagram.removeNode(objectifiedPredicate));
        Assertions.assertThrows(RuntimeException.class, () -> this._diagram.removeNode(objectifiedPredicate.innerPredicate()));
        objectifiedPredicate.innerPredicate().roles().forEach(e -> Assertions.assertThrows(RuntimeException.class, () -> this._diagram.removeNode(e)));
    }

    @Test
    void objectifiedPredicate_unobjectifyAndRemoveNodeAndAddInnerPredicateWithItsRoles() {
        // Prepare data and start testing
        ObjectifiedPredicate objectifiedPredicate = this._diagram.addNode(new ObjectifiedPredicate(new Predicate(3)));
        Predicate predicate = objectifiedPredicate.becomeUnobjectified();
        test_addDiagramElement(predicate);

        // Check result
        Assertions.assertThrows(RuntimeException.class, () -> this._diagram.removeNode(objectifiedPredicate));
        Assertions.assertThrows(RuntimeException.class, () -> this._diagram.addNode(predicate));
        predicate.roles().forEach(e -> Assertions.assertThrows(RuntimeException.class, () -> this._diagram.addNode(e)));
    }

    // ----------------- CONSTRAINTS ----------------
    @Test
    void constraint_adding() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EqualityConstraint()));
        test_addDiagramElement(this._diagram.addNode(new SubsetConstraint()));
        test_addDiagramElement(this._diagram.addNode(new UniquenessConstraint()));
        test_addDiagramElement(this._diagram.addNode(new InclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new ExclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new ExclusionConstraint()));

        // Check result
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof EqualityConstraint ||
                e instanceof SubsetConstraint ||
                e instanceof UniquenessConstraint ||
                e instanceof InclusiveOrConstraint ||
                e instanceof ExclusiveOrConstraint ||
                e instanceof ExclusionConstraint));
    }

    @Test
    void constraint_undoAdding() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EqualityConstraint()));
        test_addDiagramElement(this._diagram.addNode(new SubsetConstraint()));
        this._diagram.addNode(new UniquenessConstraint());
        this._diagram.addNode(new InclusiveOrConstraint());
        this._diagram.addNode(new ExclusiveOrConstraint());
        this._diagram.addNode(new ExclusionConstraint());

        for (int i=0; i<4; i++) { this._diagram.undoState(); }

        // Check result
        Assertions.assertTrue(this._diagram.canRedoState());
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof EqualityConstraint || e instanceof SubsetConstraint));
    }

    @Test
    void constraint_redoAdding() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EqualityConstraint()));
        test_addDiagramElement(this._diagram.addNode(new SubsetConstraint()));
        test_addDiagramElement(this._diagram.addNode(new UniquenessConstraint()));
        test_addDiagramElement(this._diagram.addNode(new InclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new ExclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new ExclusionConstraint()));

        for (int i=0; i<3; i++) { this._diagram.undoState(); }
        for (int i=0; i<3; i++) { this._diagram.redoState(); }

        // Check result
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof EqualityConstraint ||
                e instanceof SubsetConstraint ||
                e instanceof UniquenessConstraint ||
                e instanceof InclusiveOrConstraint ||
                e instanceof ExclusiveOrConstraint ||
                e instanceof ExclusionConstraint));
    }

    @Test
    void constraint_addingAfterUndo() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EqualityConstraint()));
        test_addDiagramElement(this._diagram.addNode(new SubsetConstraint()));
        this._diagram.addNode(new UniquenessConstraint());
        this._diagram.addNode(new InclusiveOrConstraint());
        this._diagram.addNode(new ExclusiveOrConstraint());
        this._diagram.addNode(new ExclusionConstraint());

        for (int i=0; i<4; i++) { this._diagram.undoState(); }

        test_addDiagramElement(this._diagram.addNode(new InclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new ExclusionConstraint()));

        // Check result
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof EqualityConstraint ||
                e instanceof SubsetConstraint ||
                e instanceof InclusiveOrConstraint ||
                e instanceof ExclusionConstraint));
    }

    @Test
    void constraint_removing() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EqualityConstraint()));
        test_addDiagramElement(this._diagram.addNode(new SubsetConstraint()));
        test_addDiagramElement(this._diagram.addNode(new UniquenessConstraint()));
        test_addDiagramElement(this._diagram.addNode(new InclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new ExclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new ExclusionConstraint()));

        this._diagram.removeNode(test_constraints.get(2));
        test_removeDiagramElement(test_constraints.get(2));
        this._diagram.removeNode(test_constraints.get(1));
        test_removeDiagramElement(test_constraints.get(1));

        // Check result
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof EqualityConstraint ||
                e instanceof InclusiveOrConstraint ||
                e instanceof ExclusiveOrConstraint ||
                e instanceof ExclusionConstraint));
    }

    @Test
    void constraint_undoRemoving() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EqualityConstraint()));
        test_addDiagramElement(this._diagram.addNode(new SubsetConstraint()));
        test_addDiagramElement(this._diagram.addNode(new UniquenessConstraint()));
        test_addDiagramElement(this._diagram.addNode(new InclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new ExclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new ExclusionConstraint()));
        for (int i=0; i<3; i++) { this._diagram.removeNode(test_constraints.get(i)); }

        for (int i=0; i<3; i++) { this._diagram.undoState(); }

        // Check result
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof EqualityConstraint ||
                e instanceof SubsetConstraint ||
                e instanceof UniquenessConstraint ||
                e instanceof InclusiveOrConstraint ||
                e instanceof ExclusiveOrConstraint ||
                e instanceof ExclusionConstraint));
    }

    @Test
    void constraint_redoRemoving() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EqualityConstraint()));
        test_addDiagramElement(this._diagram.addNode(new SubsetConstraint()));
        test_addDiagramElement(this._diagram.addNode(new UniquenessConstraint()));
        test_addDiagramElement(this._diagram.addNode(new InclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new ExclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new ExclusionConstraint()));

        for (int i=0; i<3; i++) {
            this._diagram.removeNode(test_constraints.get(0));
            test_removeDiagramElement(test_constraints.get(0));
        }

        for (int i=0; i<3; i++) { this._diagram.undoState(); }
        for (int i=0; i<3; i++) { this._diagram.redoState(); }

        // Check result
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof EqualityConstraint ||
                e instanceof InclusiveOrConstraint ||
                e instanceof ExclusiveOrConstraint ||
                e instanceof ExclusionConstraint));
    }

    @Test
    void constraint_removingAfterUndo() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EqualityConstraint()));
        test_addDiagramElement(this._diagram.addNode(new SubsetConstraint()));
        test_addDiagramElement(this._diagram.addNode(new UniquenessConstraint()));
        test_addDiagramElement(this._diagram.addNode(new InclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new ExclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new ExclusionConstraint()));
        for (int i=0; i<3; i++) { this._diagram.removeNode(test_constraints.get(i)); }

        for (int i=0; i<3; i++) { this._diagram.undoState(); }

        this._diagram.removeNode(test_constraints.get(1));
        this._diagram.removeNode(test_constraints.get(0));
        test_removeDiagramElement(test_constraints.get(1));
        test_removeDiagramElement(test_constraints.get(0));

        // Check result
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof UniquenessConstraint ||
                e instanceof InclusiveOrConstraint ||
                e instanceof ExclusiveOrConstraint ||
                e instanceof ExclusionConstraint));
    }

    @Test
    void constraint_undoWholeActionHistory() {
        // Prepare data and start testing
        for (int i=0; i<10; i++) {
            test_addDiagramElement(this._diagram.addNode(new EqualityConstraint()));
            test_addDiagramElement(this._diagram.addNode(new SubsetConstraint()));
            test_addDiagramElement(this._diagram.addNode(new UniquenessConstraint()));
            test_addDiagramElement(this._diagram.addNode(new InclusiveOrConstraint()));
            test_addDiagramElement(this._diagram.addNode(new ExclusiveOrConstraint()));
            test_addDiagramElement(this._diagram.addNode(new ExclusionConstraint()));

            for (int j=0; j<i-5; j++) {
                this._diagram.removeNode(test_constraints.get(0));
                test_removeDiagramElement(test_constraints.get(0));
            }
        }

        while (this._diagram.canUndoState()) { this._diagram.undoState(); }
        test_removeAllDiagramElements();

        // Check result
        Assertions.assertFalse(this._diagram.canUndoState());
        Assertions.assertTrue(this._diagram.canRedoState());
        Assertions.assertEquals(0, this._diagram.getElements(DiagramElement.class).count());
    }

    @Test
    void constraint_undoRedoWholeActionHistory() {
        // Prepare data and start testing
        for (int i=0; i<10; i++) {
            test_addDiagramElement(this._diagram.addNode(new EqualityConstraint()));
            test_addDiagramElement(this._diagram.addNode(new SubsetConstraint()));
            test_addDiagramElement(this._diagram.addNode(new UniquenessConstraint()));
            test_addDiagramElement(this._diagram.addNode(new InclusiveOrConstraint()));
            test_addDiagramElement(this._diagram.addNode(new ExclusiveOrConstraint()));
            test_addDiagramElement(this._diagram.addNode(new ExclusionConstraint()));

            for (int j=0; j<i-5; j++) {
                this._diagram.removeNode(test_constraints.get(0));
                test_removeDiagramElement(test_constraints.get(0));
            }
        }

        while (this._diagram.canUndoState()) { this._diagram.undoState(); }
        while (this._diagram.canRedoState()) { this._diagram.redoState(); }

        // Check result
        Assertions.assertFalse(this._diagram.canRedoState());
        Assertions.assertTrue(this._diagram.getElements(DiagramElement.class).allMatch(e -> e instanceof Constraint));
    }

    @Test
    void constraint_addAddedNodes() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EqualityConstraint()));
        test_addDiagramElement(this._diagram.addNode(new ExclusionConstraint()));
        test_addDiagramElement(this._diagram.addNode(new ExclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new InclusiveOrConstraint()));
        test_addDiagramElement(this._diagram.addNode(new SubsetConstraint()));
        test_addDiagramElement(this._diagram.addNode(new UniquenessConstraint()));

        // Check result
        for (Constraint constraint : test_constraints) { Assertions.assertThrows(RuntimeException.class, () -> this._diagram.addNode(constraint)); }
    }

    @Test
    void constraint_removeRemovedNodes() {
        // Prepare data and start testing
        ArrayList<Constraint> removedConstraints = new ArrayList<>();

        removedConstraints.add(this._diagram.addNode(new EqualityConstraint()));
        removedConstraints.add(this._diagram.addNode(new ExclusionConstraint()));
        removedConstraints.add(this._diagram.addNode(new ExclusiveOrConstraint()));
        removedConstraints.add(this._diagram.addNode(new InclusiveOrConstraint()));
        removedConstraints.add(this._diagram.addNode(new SubsetConstraint()));
        removedConstraints.add(this._diagram.addNode(new UniquenessConstraint()));

        for (Constraint constraint : removedConstraints) { this._diagram.removeNode(constraint); }

        // Check result
        for (Constraint constraint : removedConstraints) { Assertions.assertThrows(RuntimeException.class, () -> this._diagram.removeNode(constraint)); }
    }
}
