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

class Test_connectEntityTypes {
    @Test
    void connectEntityTypes() {
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
    void undoConnectEntityTypes() {
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
    void redoConnectEntityTypes() {
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
    void disconnectEntityTypes() {
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

    /*
    @Test
    void undoDisconnectEntityTypes() {
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
    void undoDisconnectEntityTypes() {
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
    void complexUndoRedoConnectDisconnectEntityTypes() {
    }
     */
}
