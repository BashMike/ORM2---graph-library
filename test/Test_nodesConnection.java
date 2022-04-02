import com.orm2_graph_library.action_errors.DiagramElementSelfConnectedActionError;
import com.orm2_graph_library.action_errors.DoubleConnectionActionError;
import com.orm2_graph_library.core.*;
import com.orm2_graph_library.edges.RoleConstraintRelationEdge;
import com.orm2_graph_library.edges.RoleRelationEdge;
import com.orm2_graph_library.edges.SubtypingConstraintRelationEdge;
import com.orm2_graph_library.edges.SubtypingRelationEdge;
import com.orm2_graph_library.nodes.common.EntityType;
import com.orm2_graph_library.nodes.constraints.Constraint;
import com.orm2_graph_library.nodes.predicates.Role;
import com.orm2_graph_library.nodes.predicates.RoleParticipant;
import com.orm2_graph_library.nodes.predicates.RolesSequence;
import com.orm2_graph_library.utils.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.Test_globalTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

class Test_nodesConnection extends Test_globalTest {
    // ================== SUBTYPES ==================
    class TestActionErrorListener implements ActionErrorListener {
        private ArrayList<ActionError> _actionErrors = new ArrayList<>();
        public ArrayList<ActionError> actionErrors() { return this._actionErrors; }

        @Override
        public void handle(ActionError actionError) { this._actionErrors.add(actionError); }
    }

    // ==================== INIT ====================
    final private Map<Pair<DiagramElement, DiagramElement>, Class<? extends Edge>> _connectedDiagramElements = new HashMap<>();

    @BeforeEach
    protected void beginTest_nodesConnection() {
        this._connectedDiagramElements.clear();
    }

    @AfterEach
    protected void endTest_nodesConnection() {
        // Check that all needed diagram elements aren't connected with each other (including self connections)
        boolean areAllNecessaryDiagramElementsWithoutConnection = this._diagram.getElements(DiagramElement.class)
                .allMatch(e -> this._diagram.getElements(DiagramElement.class)
                        .filter(e1 -> !this._connectedDiagramElements.containsKey(new Pair<>(e, e1)))
                        .noneMatch(e2 -> this._diagram.hasConnectByAnyRelation(e, e2)));

        Assertions.assertTrue(areAllNecessaryDiagramElementsWithoutConnection);

        // Check that all needed diagram elements are connected with corresponding type of edge
        for (var connectPair : this._connectedDiagramElements.entrySet()) {
            Assertions.assertTrue(this._diagram.hasConnectByRelation(connectPair.getKey().first(), connectPair.getKey().second(), connectPair.getValue()));

            Edge edge = this._diagram.getConnectByRelation(connectPair.getKey().first(), connectPair.getKey().second(), connectPair.getValue());
            Assertions.assertNotNull(edge);
            Assertions.assertEquals(connectPair.getKey().first(), edge.begin());
            Assertions.assertEquals(connectPair.getKey().first(), edge.begin());
        }
    }

    // ==================== TEST ====================
    // ------------- SUBTYPING RELATION -------------
    @Test
    void subtypingRelation_connect() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint()));
        test_addDiagramElement(this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint()));

        // Check result
        this._connectedDiagramElements.put(new Pair<>(test_entityTypes.get(0), test_entityTypes.get(1)), SubtypingRelationEdge.class);
        this._connectedDiagramElements.put(new Pair<>(test_entityTypes.get(0), test_entityTypes.get(2)), SubtypingRelationEdge.class);
        this._connectedDiagramElements.put(new Pair<>(test_entityTypes.get(1), test_entityTypes.get(3)), SubtypingRelationEdge.class);
    }

    @Test
    void entityType_undoConnect() {
        // Prepare data and start testing
        for (int i=0; i<5; i++) { test_addDiagramElement(this._diagram.addNode(new EntityType())); }
        this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(1).centerAnchorPoint());
        this._diagram.connectBySubtypingRelation(test_entityTypes.get(0).centerAnchorPoint(), test_entityTypes.get(2).centerAnchorPoint());
        this._diagram.connectBySubtypingRelation(test_entityTypes.get(1).centerAnchorPoint(), test_entityTypes.get(3).centerAnchorPoint());

        this._diagram.undoState();
        this._diagram.undoState();
        this._diagram.undoState();
    }

    @Test
    void entityType_redoConnect() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        ArrayList<EntityType> createdEntityTypes = new ArrayList<>();

        for (int i=0; i<5; i++) { createdEntityTypes.add(diagram.addNode(new EntityType())); }
        diagram.connectBySubtypingRelation(createdEntityTypes.get(0).centerAnchorPoint(), createdEntityTypes.get(1).centerAnchorPoint());
        diagram.connectBySubtypingRelation(createdEntityTypes.get(0).centerAnchorPoint(), createdEntityTypes.get(2).centerAnchorPoint());
        diagram.connectBySubtypingRelation(createdEntityTypes.get(1).centerAnchorPoint(), createdEntityTypes.get(3).centerAnchorPoint());

        ArrayList<EntityType> gottenEntityTypes = diagram.getElements(EntityType.class).collect(Collectors.toCollection(ArrayList::new));

        diagram.undoState();
        diagram.undoState();
        diagram.undoState();
        diagram.redoState();
        diagram.redoState();
        diagram.redoState();

        // Check result
        Assertions.assertEquals(gottenEntityTypes, createdEntityTypes);
        Assertions.assertEquals(3, diagram.getElements(SubtypingRelationEdge.class).count());

        Assertions.assertTrue(diagram.getElements(SubtypingRelationEdge.class).anyMatch (e -> e.beginAnchorPoint().owner() == createdEntityTypes.get(0) && e.endAnchorPoint().owner() == createdEntityTypes.get(1)));
        Assertions.assertNotNull(diagram.getElements(SubtypingRelationEdge.class).filter(e -> e.beginAnchorPoint().owner() == createdEntityTypes.get(0) && e.endAnchorPoint().owner() == createdEntityTypes.get(1)).findFirst().get());
        Assertions.assertTrue(diagram.getElements(SubtypingRelationEdge.class).anyMatch (e -> e.beginAnchorPoint().owner() == createdEntityTypes.get(0) && e.endAnchorPoint().owner() == createdEntityTypes.get(2)));
        Assertions.assertNotNull(diagram.getElements(SubtypingRelationEdge.class).filter(e -> e.beginAnchorPoint().owner() == createdEntityTypes.get(0) && e.endAnchorPoint().owner() == createdEntityTypes.get(2)).findFirst().get());
        Assertions.assertTrue(diagram.getElements(SubtypingRelationEdge.class).anyMatch (e -> e.beginAnchorPoint().owner() == createdEntityTypes.get(1) && e.endAnchorPoint().owner() == createdEntityTypes.get(3)));
        Assertions.assertNotNull(diagram.getElements(SubtypingRelationEdge.class).filter(e -> e.beginAnchorPoint().owner() == createdEntityTypes.get(1) && e.endAnchorPoint().owner() == createdEntityTypes.get(3)).findFirst().get());
    }

    @Test
    void entityType_connectWithSelf() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        EntityType entityType = diagram.addNode(new EntityType());

        TestActionErrorListener actionErrorListener = new TestActionErrorListener();
        diagram.addActionErrorListener(actionErrorListener);

        diagram.connectBySubtypingRelation(entityType.centerAnchorPoint(), entityType.centerAnchorPoint());

        // Check result
        Assertions.assertEquals(0, diagram.getElements(SubtypingRelationEdge.class).count());
        Assertions.assertEquals(1, actionErrorListener.actionErrors().size());
        Assertions.assertTrue(actionErrorListener.actionErrors().get(0) instanceof DiagramElementSelfConnectedActionError);
        Assertions.assertEquals(entityType, ((DiagramElementSelfConnectedActionError)actionErrorListener.actionErrors().get(0)).diagramElement());
    }

    @Test
    void entityType_doubleConnect() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        EntityType entityType0 = diagram.addNode(new EntityType());
        EntityType entityType1 = diagram.addNode(new EntityType());

        TestActionErrorListener actionErrorListener = new TestActionErrorListener();
        diagram.addActionErrorListener(actionErrorListener);

        diagram.connectBySubtypingRelation(entityType0.centerAnchorPoint(), entityType1.centerAnchorPoint());
        diagram.connectBySubtypingRelation(entityType0.centerAnchorPoint(), entityType1.centerAnchorPoint());

        // Check result
        Assertions.assertEquals(1, diagram.getElements(SubtypingRelationEdge.class).count());
        Assertions.assertEquals(1, actionErrorListener.actionErrors().size());
        Assertions.assertTrue(actionErrorListener.actionErrors().get(0) instanceof DoubleConnectionActionError);
        Assertions.assertEquals(entityType0, ((DoubleConnectionActionError)actionErrorListener.actionErrors().get(0)).beginDiagramElement());
        Assertions.assertEquals(entityType1, ((DoubleConnectionActionError)actionErrorListener.actionErrors().get(0)).endDiagramElement());

        SubtypingRelationEdge edge = diagram.getElements(SubtypingRelationEdge.class).findFirst().get();
        Assertions.assertEquals(edge, ((DoubleConnectionActionError)actionErrorListener.actionErrors().get(0)).existEdge());
    }

    @Test
    void entityType_doubleConnectInOppositeDirection() {
        // Prepare data and start testing
        Diagram diagram = new Diagram();
        EntityType entityType0 = diagram.addNode(new EntityType());
        EntityType entityType1 = diagram.addNode(new EntityType());

        TestActionErrorListener actionErrorListener = new TestActionErrorListener();
        diagram.addActionErrorListener(actionErrorListener);

        diagram.connectBySubtypingRelation(entityType0.centerAnchorPoint(), entityType1.centerAnchorPoint());
        diagram.connectBySubtypingRelation(entityType1.centerAnchorPoint(), entityType0.centerAnchorPoint());

        // Check result
        Assertions.assertEquals(2, diagram.getElements(SubtypingRelationEdge.class).count());
        Assertions.assertTrue(actionErrorListener.actionErrors().isEmpty());
    }
}
