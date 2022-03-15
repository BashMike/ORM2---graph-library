import com.orm2_graph_library.core.AnchorPoint;
import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.DiagramElement;
import com.orm2_graph_library.edges.SubtypingRelationEdge;
import com.orm2_graph_library.nodes.common.EntityType;
import com.orm2_graph_library.nodes.common.ObjectType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.stream.Collectors;

class Test_nodesConnection {
    @Test
    void connectEntityTypes() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        ArrayList<EntityType> createdEntityTypes = new ArrayList<>();

        for (int i=0; i<5; i++) { createdEntityTypes.add(diagram.addNode(new EntityType())); }
        diagram.connectBySubtypeRelation(createdEntityTypes.get(0).centerAnchorPoint(), createdEntityTypes.get(1).centerAnchorPoint());
        diagram.connectBySubtypeRelation(createdEntityTypes.get(0).centerAnchorPoint(), createdEntityTypes.get(2).centerAnchorPoint());
        diagram.connectBySubtypeRelation(createdEntityTypes.get(1).centerAnchorPoint(), createdEntityTypes.get(3).centerAnchorPoint());

        ArrayList<EntityType> gottenEntityTypes = diagram.getElements(EntityType.class).collect(Collectors.toCollection(ArrayList::new));

        // Check result
        Assertions.assertEquals(gottenEntityTypes, createdEntityTypes);
        Assertions.assertEquals(3, diagram.getElements(SubtypingRelationEdge.class).collect(Collectors.toCollection(ArrayList::new)).size());

        Assertions.assertTrue(diagram.getElements(SubtypingRelationEdge.class).anyMatch (e -> e.beginAnchorPoint().owner() == createdEntityTypes.get(0) && e.endAnchorPoint().owner() == createdEntityTypes.get(1)));
        Assertions.assertNotNull(diagram.getElements(SubtypingRelationEdge.class).filter(e -> e.beginAnchorPoint().owner() == createdEntityTypes.get(0) && e.endAnchorPoint().owner() == createdEntityTypes.get(1)).findFirst().get());
        Assertions.assertTrue(diagram.getElements(SubtypingRelationEdge.class).anyMatch (e -> e.beginAnchorPoint().owner() == createdEntityTypes.get(0) && e.endAnchorPoint().owner() == createdEntityTypes.get(2)));
        Assertions.assertNotNull(diagram.getElements(SubtypingRelationEdge.class).filter(e -> e.beginAnchorPoint().owner() == createdEntityTypes.get(0) && e.endAnchorPoint().owner() == createdEntityTypes.get(2)).findFirst().get());
        Assertions.assertTrue(diagram.getElements(SubtypingRelationEdge.class).anyMatch (e -> e.beginAnchorPoint().owner() == createdEntityTypes.get(1) && e.endAnchorPoint().owner() == createdEntityTypes.get(3)));
        Assertions.assertNotNull(diagram.getElements(SubtypingRelationEdge.class).filter(e -> e.beginAnchorPoint().owner() == createdEntityTypes.get(1) && e.endAnchorPoint().owner() == createdEntityTypes.get(3)).findFirst().get());
    }

    /*
    @Test
    void undoConnectEntityTypes() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        ArrayList<EntityType> createdEntityTypes = new ArrayList<>();

        for (int i=0; i<5; i++) { createdEntityTypes.add(diagram.addNode(new EntityType())); }
        diagram.connectBySubtypeRelation(createdEntityTypes.get(0), createdEntityTypes.get(1));
        diagram.connectBySubtypeRelation(createdEntityTypes.get(0), createdEntityTypes.get(2));
        diagram.connectBySubtypeRelation(createdEntityTypes.get(1), createdEntityTypes.get(3));

        diagram.undoState();

        ArrayList<EntityType> gottenEntityTypes = diagram.getElements(EntityType.class).collect(Collectors.toCollection(ArrayList::new));

        // Check result
        Assertions.assertEquals(gottenEntityTypes, createdEntityTypes);
        Assertions.assertEquals(2, diagram.getElements(SubtypingRelationEdge.class).collect(Collectors.toCollection(ArrayList::new)).size());

        Assertions.assertTrue(diagram.getElements(SubtypingRelationEdge.class).anyMatch(e -> e.begin() == createdEntityTypes.get(0) && e.end() == createdEntityTypes.get(1)));
        Assertions.assertNotNull(diagram.getElements(SubtypingRelationEdge.class).filter(e -> e.begin() == createdEntityTypes.get(0) && e.end() == createdEntityTypes.get(1)).findFirst().get());
        Assertions.assertTrue(diagram.getElements(SubtypingRelationEdge.class).anyMatch(e -> e.begin() == createdEntityTypes.get(0) && e.end() == createdEntityTypes.get(2)));
        Assertions.assertNotNull(diagram.getElements(SubtypingRelationEdge.class).filter(e -> e.begin() == createdEntityTypes.get(0) && e.end() == createdEntityTypes.get(2)).findFirst().get());
        Assertions.assertTrue(diagram.getElements(SubtypingRelationEdge.class).noneMatch(e -> e.begin() == createdEntityTypes.get(1) && e.end() == createdEntityTypes.get(3)));
    }

    @Test
    void redoConnectEntityTypes() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        ArrayList<EntityType> createdEntityTypes = new ArrayList<>();

        for (int i=0; i<5; i++) { createdEntityTypes.add(diagram.addNode(new EntityType())); }
        diagram.connectBySubtypeRelation(createdEntityTypes.get(0), createdEntityTypes.get(1));
        diagram.connectBySubtypeRelation(createdEntityTypes.get(0), createdEntityTypes.get(2));
        diagram.connectBySubtypeRelation(createdEntityTypes.get(1), createdEntityTypes.get(3));

        diagram.undoState();
        diagram.undoState();
        diagram.redoState();

        ArrayList<EntityType> gottenEntityTypes = diagram.getElements(EntityType.class).collect(Collectors.toCollection(ArrayList::new));

        // Check result
        Assertions.assertEquals(gottenEntityTypes, createdEntityTypes);
        Assertions.assertEquals(2, diagram.getElements(SubtypingRelationEdge.class).collect(Collectors.toCollection(ArrayList::new)).size());

        Assertions.assertTrue(diagram.getElements(SubtypingRelationEdge.class).anyMatch(e -> e.begin() == createdEntityTypes.get(0) && e.end() == createdEntityTypes.get(1)));
        Assertions.assertNotNull(diagram.getElements(SubtypingRelationEdge.class).filter(e -> e.begin() == createdEntityTypes.get(0) && e.end() == createdEntityTypes.get(1)).findFirst().get());
        Assertions.assertTrue(diagram.getElements(SubtypingRelationEdge.class).anyMatch(e -> e.begin() == createdEntityTypes.get(0) && e.end() == createdEntityTypes.get(2)));
        Assertions.assertNotNull(diagram.getElements(SubtypingRelationEdge.class).filter(e -> e.begin() == createdEntityTypes.get(0) && e.end() == createdEntityTypes.get(2)).findFirst().get());
        Assertions.assertTrue(diagram.getElements(SubtypingRelationEdge.class).noneMatch(e -> e.begin() == createdEntityTypes.get(1) && e.end() == createdEntityTypes.get(3)));
    }

    @Test
    void disconnectEntityTypes() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        ArrayList<EntityType> createdEntityTypes = new ArrayList<>();
        ArrayList<EntityType> gottenEntityTypes;

        for (int i=0; i<5; i++) { createdEntityTypes.add( diagram.addNode(new EntityType()) ); }
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
