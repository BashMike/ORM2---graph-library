import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.nodes.common.EntityType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Test_nodesModification {
    // ==================== TEST ====================
    // ---------------- ENTITY TYPES ----------------
    // * Name
    @Test
    void entityType_setName() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        EntityType entityType = new EntityType();

        diagram.addNode(entityType);
        entityType.setName("Hello");

        // Check result
        Assertions.assertEquals("Hello", entityType.name());
    }

    @Test
    void entityType_undoSetName() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        EntityType entityType = new EntityType();

        diagram.addNode(entityType);
        entityType.setName("Hello");

        diagram.undoState();

        // Check result
        Assertions.assertEquals(entityType.basicName() + " " + 1, entityType.name());
    }

    @Test
    void entityType_setTheSameName() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        EntityType entityType = new EntityType();

        diagram.addNode(entityType);
        entityType.setName("Hello");
        entityType.setName("Hello");
        entityType.setName("Hello");
        entityType.setName("Hello");
        entityType.setName("Hello");
        entityType.setName("Hello");

        // Check result
        Assertions.assertEquals("Hello", entityType.name());
    }

    @Test
    void entityType_undoSetTheSameName() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        EntityType entityType = new EntityType();

        diagram.addNode(entityType);
        entityType.setName("Hello");
        entityType.setName("Hello");
        entityType.setName("Hello");
        entityType.setName("Hello");
        entityType.setName("Hello");
        entityType.setName("Hello");

        diagram.undoState();

        // Check result
        Assertions.assertEquals(entityType.basicName() + " " + 1, entityType.name());
    }

    @Test
    void entityType_setNameWhenNotInDiagram() {
        // Prepare data and start testing
        EntityType entityType = new EntityType();

        // Check result
        Assertions.assertThrows(NullPointerException.class, () -> entityType.setName("Hello"));
    }

    // * IsPersonal flag
    @Test
    void entityType_setIsPersonal() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        EntityType entityType = new EntityType();

        diagram.addNode(entityType);
        entityType.setIsPersonal(true);

        // Check result
        Assertions.assertTrue(entityType.isPersonal());
    }

    @Test
    void entityType_undoSetIsPersonal() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        EntityType entityType = new EntityType();

        diagram.addNode(entityType);
        entityType.setIsPersonal(true);

        diagram.undoState();

        // Check result
        Assertions.assertFalse(entityType.isPersonal());
    }

    @Test
    void entityType_setTheSameIsPersonal() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        EntityType entityType = new EntityType();

        diagram.addNode(entityType);
        entityType.setIsPersonal(true);
        entityType.setIsPersonal(true);
        entityType.setIsPersonal(true);
        entityType.setIsPersonal(true);
        entityType.setIsPersonal(true);
        entityType.setIsPersonal(true);

        // Check result
        Assertions.assertTrue(entityType.isPersonal());
    }

    @Test
    void entityType_undoSetTheSameIsPersonal() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        EntityType entityType = new EntityType();

        diagram.addNode(entityType);
        entityType.setIsPersonal(true);
        entityType.setIsPersonal(true);
        entityType.setIsPersonal(true);
        entityType.setIsPersonal(true);
        entityType.setIsPersonal(true);
        entityType.setIsPersonal(true);

        diagram.undoState();

        // Check result
        Assertions.assertFalse(entityType.isPersonal());
    }

    @Test
    void entityType_setIsPersonalWhenNotInDiagram() {
        // Prepare data and start testing
        EntityType entityType = new EntityType();

        // Check result
        Assertions.assertThrows(NullPointerException.class, () -> entityType.setIsPersonal(false));
    }

    // * IsIndependent flag
    @Test
    void entityType_setIsIndependent() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        EntityType entityType = new EntityType();

        diagram.addNode(entityType);
        entityType.setIsIndependent(true);

        // Check result
        Assertions.assertTrue(entityType.isIndependent());
    }

    @Test
    void entityType_undoSetIsIndependent() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        EntityType entityType = new EntityType();

        diagram.addNode(entityType);
        entityType.setIsIndependent(true);

        diagram.undoState();

        // Check result
        Assertions.assertFalse(entityType.isIndependent());
    }

    @Test
    void entityType_setTheSameIsIndependent() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        EntityType entityType = new EntityType();

        diagram.addNode(entityType);
        entityType.setIsIndependent(true);
        entityType.setIsIndependent(true);
        entityType.setIsIndependent(true);
        entityType.setIsIndependent(true);
        entityType.setIsIndependent(true);
        entityType.setIsIndependent(true);

        // Check result
        Assertions.assertTrue(entityType.isIndependent());
    }

    @Test
    void entityType_undoSetTheSameIsIndependent() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        EntityType entityType = new EntityType();

        diagram.addNode(entityType);
        entityType.setIsIndependent(true);
        entityType.setIsIndependent(true);
        entityType.setIsIndependent(true);
        entityType.setIsIndependent(true);
        entityType.setIsIndependent(true);
        entityType.setIsIndependent(true);

        diagram.undoState();

        // Check result
        Assertions.assertFalse(entityType.isIndependent());
    }

    @Test
    void entityType_setIsIndependentWhenNotInDiagram() {
        // Prepare data and start testing
        EntityType entityType = new EntityType();

        // Check result
        Assertions.assertThrows(NullPointerException.class, () -> entityType.setIsIndependent(false));
    }

    // TODO - @test :: Setting reference mode and data type for entity type.

    // * Complex modification
}
