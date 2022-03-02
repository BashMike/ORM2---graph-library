import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.nodes.common.EntityType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Test_nodesGeometry {
    // ================ ENTITY TYPES ================
    @Test
    void moveEntityTypeToPos() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        EntityType entityType = new EntityType();

        diagram.addNode(entityType);
        entityType.moveTo(20, 30);

        // Check result
        Assertions.assertEquals(entityType.geometry().borderArea().leftTop().x, 20);
        Assertions.assertEquals(entityType.geometry().borderArea().leftTop().y, 30);
    }

    @Test
    void moveEntityTypeByValue() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        EntityType entityType = new EntityType();

        diagram.addNode(entityType);
        entityType.moveTo(20, 30);
        entityType.moveBy(15, -34);

        // Check result
        Assertions.assertEquals(entityType.geometry().borderArea().leftTop().x, 35);
        Assertions.assertEquals(entityType.geometry().borderArea().leftTop().y, -4);
    }

    @Test
    void undoMoveEntityTypeToPos() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        EntityType entityType = new EntityType();

        diagram.addNode(entityType);
        entityType.moveTo(20, 30);

        diagram.undoState();

        // Check result
        Assertions.assertNotEquals(entityType.geometry().borderArea().leftTop().x, 20);
        Assertions.assertNotEquals(entityType.geometry().borderArea().leftTop().y, 30);
        Assertions.assertTrue(diagram.canRedoState());
    }

    @Test
    void undoMoveEntityTypeByValue() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        EntityType entityType = new EntityType();

        diagram.addNode(entityType);
        entityType.moveTo(20, 30);
        entityType.moveBy(15, -34);

        diagram.undoState();

        // Check result
        Assertions.assertEquals(entityType.geometry().borderArea().leftTop().x, 20);
        Assertions.assertEquals(entityType.geometry().borderArea().leftTop().y, 30);
        Assertions.assertTrue(diagram.canRedoState());
    }

    @Test
    void redoMoveEntityTypeToPos() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        EntityType entityType = new EntityType();

        diagram.addNode(entityType);
        entityType.moveTo(20, 30);

        diagram.undoState();
        diagram.redoState();

        // Check result
        Assertions.assertEquals(entityType.geometry().borderArea().leftTop().x, 20);
        Assertions.assertEquals(entityType.geometry().borderArea().leftTop().y, 30);
        Assertions.assertFalse(diagram.canRedoState());
    }

    @Test
    void redoMoveEntityTypeByValue() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        EntityType entityType = new EntityType();

        diagram.addNode(entityType);
        entityType.moveTo(20, 30);
        entityType.moveBy(15, -34);

        diagram.undoState();
        diagram.redoState();

        // Check result
        Assertions.assertEquals(entityType.geometry().borderArea().leftTop().x, 35);
        Assertions.assertEquals(entityType.geometry().borderArea().leftTop().y, -4);
        Assertions.assertFalse(diagram.canRedoState());
    }

    @Test
    void moveEntityTypeToPosAfterUndo() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        EntityType entityType = new EntityType();

        diagram.addNode(entityType);
        entityType.moveTo(20, 30);
        entityType.moveTo(30, -30);
        entityType.moveTo(40, 30);
        entityType.moveTo(50, -30);

        diagram.undoState();
        diagram.undoState();

        entityType.moveBy(15, 15);

        // Check result
        Assertions.assertEquals(entityType.geometry().borderArea().leftTop().x, 45);
        Assertions.assertEquals(entityType.geometry().borderArea().leftTop().y, -15);
        Assertions.assertFalse(diagram.canRedoState());
    }

    @Test
    void complexMoveEntityTypeToPos() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        EntityType entityType = new EntityType();

        diagram.addNode(entityType);
        entityType.moveTo(20, 30);
        entityType.moveBy(-100, 3);
        entityType.moveBy(-100, 20);
        entityType.moveBy(-149, -1001);
        entityType.moveBy(10, 3);
        entityType.moveTo(10, 3);

        diagram.undoState();
        diagram.undoState();
        diagram.redoState();
        diagram.redoState();
        diagram.undoState();

        // Check result
        Assertions.assertEquals(entityType.geometry().borderArea().leftTop().x, -319);
        Assertions.assertEquals(entityType.geometry().borderArea().leftTop().y, -945);
        Assertions.assertTrue(diagram.canRedoState());
    }

    // ================= VALUE TYPES ================
    // ============ OBJECTIFIED PREDICATE ===========
    // ================= CONSTRAINTS ================
}
