import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.DiagramElement;
import com.orm2_graph_library.nodes.common.EntityType;
import com.orm2_graph_library.nodes.predicates.Predicate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.*;

public class Test_nodesGeometry {
    // ================ ENTITY TYPES ================
    @Test
    void entityType_moveToPos() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        EntityType entityType = new EntityType();

        diagram.addNode(entityType);
        entityType.moveTo(new Point(20, 30));

        // Check result
        Assertions.assertEquals(entityType.borderLeftTop().x, 20);
        Assertions.assertEquals(entityType.borderLeftTop().y, 30);
    }

    @Test
    void entityType_moveByShiftValue() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        EntityType entityType = new EntityType();

        diagram.addNode(entityType);
        entityType.moveTo(new Point(20, 30));
        entityType.moveBy(15, -34);

        // Check result
        Assertions.assertEquals(entityType.borderLeftTop().x, 35);
        Assertions.assertEquals(entityType.borderLeftTop().y, -4);
    }

    @Test
    void entityType_undoMoveToPos() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        EntityType entityType = new EntityType();

        diagram.addNode(entityType);
        entityType.moveTo(new Point(20, 30));

        diagram.undoState();

        // Check result
        Assertions.assertNotEquals(entityType.borderLeftTop().x, 20);
        Assertions.assertNotEquals(entityType.borderLeftTop().y, 30);
        Assertions.assertTrue(diagram.canRedoState());
    }

    @Test
    void entityType_undoMoveByShiftValue() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        EntityType entityType = new EntityType();

        diagram.addNode(entityType);
        entityType.moveTo(new Point(20, 30));
        entityType.moveBy(15, -34);

        diagram.undoState();

        // Check result
        Assertions.assertEquals(entityType.borderLeftTop().x, 20);
        Assertions.assertEquals(entityType.borderLeftTop().y, 30);
        Assertions.assertTrue(diagram.canRedoState());
    }

    @Test
    void entityType_redoMoveToPos() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        EntityType entityType = new EntityType();

        diagram.addNode(entityType);
        entityType.moveTo(new Point(20, 30));

        diagram.undoState();
        diagram.redoState();

        // Check result
        Assertions.assertEquals(entityType.borderLeftTop().x, 20);
        Assertions.assertEquals(entityType.borderLeftTop().y, 30);
        Assertions.assertFalse(diagram.canRedoState());
    }

    @Test
    void entityType_redoMoveByShiftValue() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        EntityType entityType = new EntityType();

        diagram.addNode(entityType);
        entityType.moveTo(new Point(20, 30));
        entityType.moveBy(15, -34);

        diagram.undoState();
        diagram.redoState();

        // Check result
        Assertions.assertEquals(entityType.borderLeftTop().x, 35);
        Assertions.assertEquals(entityType.borderLeftTop().y, -4);
        Assertions.assertFalse(diagram.canRedoState());
    }

    @Test
    void entityType_moveToPosAfterUndo() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        EntityType entityType = new EntityType();

        diagram.addNode(entityType);
        entityType.moveTo(new Point(20, 30));
        entityType.moveTo(new Point(30, -30));
        entityType.moveTo(new Point(40, 30));
        entityType.moveTo(new Point(50, -30));

        diagram.undoState();
        diagram.undoState();

        entityType.moveBy(15, 15);

        // Check result
        Assertions.assertEquals(entityType.borderLeftTop().x, 45);
        Assertions.assertEquals(entityType.borderLeftTop().y, -15);
        Assertions.assertFalse(diagram.canRedoState());
    }

    @Test
    void entityType_complexMovement() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        EntityType entityType = new EntityType();

        diagram.addNode(entityType);
        entityType.moveTo(new Point(20, 30));
        entityType.moveBy(-100, 3);
        entityType.moveBy(-100, 20);
        entityType.moveBy(-149, -1001);
        entityType.moveBy(10, 3);
        entityType.moveTo(new Point(10, 3));

        diagram.undoState();
        diagram.undoState();
        diagram.redoState();
        diagram.redoState();
        diagram.undoState();

        // Check result
        Assertions.assertEquals(entityType.borderLeftTop().x, -319);
        Assertions.assertEquals(entityType.borderLeftTop().y, -945);
        Assertions.assertTrue(diagram.canRedoState());
    }

    // ================= VALUE TYPES ================
    // ================== PREDICATE =================
    @Test
    void predicate_changeOrientationToVertical() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        Predicate predicate = new Predicate(3);
        diagram.addNode(predicate);

        predicate.setOrientation(DiagramElement.Orientation.VERTICAL);

        // Check result
        Assertions.assertEquals(predicate.getRole(0).borderWidth(),      predicate.borderWidth());
        Assertions.assertEquals(predicate.getRole(0).borderHeight() * 3, predicate.borderHeight());
    }

    @Test
    void predicate_changeOrientationBackToHorizontal() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        Predicate predicate = new Predicate(3);
        diagram.addNode(predicate);

        predicate.setOrientation(DiagramElement.Orientation.VERTICAL);
        predicate.setOrientation(DiagramElement.Orientation.HORIZONTAL);

        // Check result
        Assertions.assertEquals(predicate.getRole(0).borderWidth() * 3, predicate.borderWidth());
        Assertions.assertEquals(predicate.getRole(0).borderHeight(),    predicate.borderHeight());
    }

    @Test
    void predicate_changeOrientationToHorizontalTwice() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        Predicate predicate = new Predicate(3);
        diagram.addNode(predicate);

        predicate.setOrientation(DiagramElement.Orientation.HORIZONTAL);
        predicate.setOrientation(DiagramElement.Orientation.HORIZONTAL);

        // Check result
        Assertions.assertEquals(predicate.getRole(0).borderWidth() * 3, predicate.borderWidth());
        Assertions.assertEquals(predicate.getRole(0).borderHeight(),    predicate.borderHeight());
    }

    @Test
    void predicate_changeOrientationToVerticalTwice() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        Predicate predicate = new Predicate(3);
        diagram.addNode(predicate);

        predicate.setOrientation(DiagramElement.Orientation.VERTICAL);
        predicate.setOrientation(DiagramElement.Orientation.VERTICAL);

        // Check result
        Assertions.assertEquals(predicate.getRole(0).borderWidth(),      predicate.borderWidth());
        Assertions.assertEquals(predicate.getRole(0).borderHeight() * 3, predicate.borderHeight());
    }

    @Test
    void predicate_complexChangingOrientation() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        Predicate predicate = new Predicate(3);
        diagram.addNode(predicate);

        predicate.setOrientation(DiagramElement.Orientation.VERTICAL);
        predicate.setOrientation(DiagramElement.Orientation.HORIZONTAL);
        predicate.setOrientation(DiagramElement.Orientation.VERTICAL);
        predicate.setOrientation(DiagramElement.Orientation.HORIZONTAL);
        predicate.setOrientation(DiagramElement.Orientation.VERTICAL);
        predicate.setOrientation(DiagramElement.Orientation.VERTICAL);
        predicate.setOrientation(DiagramElement.Orientation.HORIZONTAL);

        // Check result
        Assertions.assertEquals(predicate.getRole(0).borderWidth() * 3, predicate.borderWidth());
        Assertions.assertEquals(predicate.getRole(0).borderHeight(),    predicate.borderHeight());
    }

    // =========== OBJECTIFIED PREDICATES ===========
    // ================= CONSTRAINTS ================
}
