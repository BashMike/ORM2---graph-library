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

public class Test_nodesModification {
    @Test
    void setNameEntityType() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        EntityType entityType = new EntityType();

        diagram.addNode(entityType);
        entityType.setName("Hello");

        // Check result
        Assertions.assertTrue(entityType.hasOwner());
        Assertions.assertEquals(entityType.name(), "Hello");
    }

    @Test
    void undoSetNameEntityType() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        EntityType entityType = new EntityType();

        diagram.addNode(entityType);
        entityType.setName("Hello");

        diagram.undoState();

        // Check result
        Assertions.assertTrue(entityType.hasOwner());
        Assertions.assertEquals(entityType.name(), entityType.basicName() + " " + 1);
    }
}
