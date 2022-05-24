import com.orm2_graph_library.core.AnchorPoint;
import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.DiagramElement;
import com.orm2_graph_library.edges.RoleConstraintRelationEdge;
import com.orm2_graph_library.edges.RoleRelationEdge;
import com.orm2_graph_library.edges.SubtypingConstraintRelationEdge;
import com.orm2_graph_library.edges.SubtypingRelationEdge;
import com.orm2_graph_library.logic_errors.ConstraintHasNotEnoughConnectsLogicError;
import com.orm2_graph_library.logic_errors.EntityTypeWithNoneRefModeLogicError;
import com.orm2_graph_library.nodes.common.EntityType;
import com.orm2_graph_library.nodes.common.ValueType;
import com.orm2_graph_library.nodes.constraints.Constraint;
import com.orm2_graph_library.nodes.predicates.ObjectifiedPredicate;
import com.orm2_graph_library.nodes.predicates.Predicate;
import com.orm2_graph_library.nodes.predicates.Role;
import com.orm2_graph_library.nodes.predicates.RolesSequence;
import com.orm2_graph_library.utils.Point2D;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.Test_globalTest;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Test_nodesGeometry extends Test_globalTest {
    // ==================== INIT ====================
    private Map<DiagramElement, Set<Point2D>> _diagramElementsAnchorPoints = new HashMap<>();

    private void test_anchorPoints(@NotNull DiagramElement diagramElement, @NotNull Set<Point2D> anchorPointsPositions) {
        this._diagramElementsAnchorPoints.put(diagramElement, anchorPointsPositions);
    }

    @BeforeEach
    private void testBegin_nodesGeometry() {
        this._diagramElementsAnchorPoints.clear();
    }

    @AfterEach
    private void testEnd_nodesGeometry() {
        for (EntityType entityType : test_entityTypes) { test_entityTypesLogicErrors.put(entityType, new HashSet<>(List.of(new EntityTypeWithNoneRefModeLogicError(entityType)))); }

        ArrayList<DiagramElement> diagramElements = new ArrayList<>();
        diagramElements.addAll(test_entityTypes);
        diagramElements.addAll(test_valueTypes);
        diagramElements.addAll(test_predicates);
        diagramElements.addAll(test_roles);
        diagramElements.addAll(test_objectifiedPredicates);
        diagramElements.addAll(test_constraints);
        diagramElements.addAll(test_rolesSequences);

        diagramElements.addAll(test_subtypingRelationEdges);
        diagramElements.addAll(test_roleRelationEdges);
        diagramElements.addAll(test_roleConstraintRelationEdges);
        diagramElements.addAll(test_subtypingConstraintRelationEdges);

        for (DiagramElement diagramElement : diagramElements) {
            Assertions.assertNotNull(this._diagramElementsAnchorPoints.get(diagramElement));
            Assertions.assertEquals(this._diagramElementsAnchorPoints.get(diagramElement), diagramElement.anchorPoints().map(AnchorPoint::position).collect(Collectors.toCollection(HashSet::new)), "" + diagramElement.getClass());
        }
    }

    // ==================== TEST ====================
    // ---------------- ENTITY TYPES ----------------
    @Test
    void entityType_moveToPos() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_entityTypes.get(0).moveTo(new Point2D(20, 30));

        // Check result
        Assertions.assertEquals(test_entityTypes.get(0).borderLeftTop(), new Point2D(20, 30));
        test_anchorPoints(test_entityTypes.get(0), new HashSet<>(List.of(new Point2D(20 + Test_globalTest._objectTypeSize.width/2, 30 + Test_globalTest._objectTypeSize.height/2))));
    }

    @Test
    void entityType_moveByShiftValue() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_entityTypes.get(0).moveTo(new Point2D(20, 30));
        test_entityTypes.get(0).moveBy(15, -34);
        test_entityTypes.get(0).setBorderSize(100, 50);

        // Check result
        Assertions.assertEquals(test_entityTypes.get(0).borderLeftTop(), new Point2D(35, -4));
        test_anchorPoints(test_entityTypes.get(0), new HashSet<>(List.of(new Point2D(35 + 50, -4 + 25))));
        test_anchorPoints(test_entityTypes.get(0), new HashSet<>(List.of(new Point2D(35 + Test_globalTest._objectTypeSize.width/2, -4 + Test_globalTest._objectTypeSize.height/2))));
    }

    @Test
    void entityType_undoMoveToPos() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_entityTypes.get(0).moveTo(new Point2D(20, 30));

        this._diagram.undoState();

        // Check result
        Assertions.assertTrue(this._diagram.canRedoState());
        Assertions.assertEquals(test_entityTypes.get(0).borderLeftTop(), new Point2D(0, 0));
        test_anchorPoints(test_entityTypes.get(0), new HashSet<>(List.of(new Point2D(0 + Test_globalTest._objectTypeSize.width/2, 0 + Test_globalTest._objectTypeSize.height/2))));
    }

    @Test
    void entityType_undoMoveByShiftValue() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_entityTypes.get(0).moveTo(new Point2D(20, 30));
        test_entityTypes.get(0).moveBy(15, -34);

        this._diagram.undoState();

        // Check result
        Assertions.assertTrue(this._diagram.canRedoState());
        Assertions.assertEquals(test_entityTypes.get(0).borderLeftTop(), new Point2D(20, 30));
        test_anchorPoints(test_entityTypes.get(0), new HashSet<>(List.of(new Point2D(20 + Test_globalTest._objectTypeSize.width/2, 30 + Test_globalTest._objectTypeSize.height/2))));
    }

    @Test
    void entityType_redoMoveToPos() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_entityTypes.get(0).moveTo(new Point2D(20, 30));

        this._diagram.undoState();
        this._diagram.redoState();

        // Check result
        Assertions.assertFalse(this._diagram.canRedoState());
        Assertions.assertEquals(test_entityTypes.get(0).borderLeftTop(), new Point2D(20, 30));
        test_anchorPoints(test_entityTypes.get(0), new HashSet<>(List.of(new Point2D(20 + Test_globalTest._objectTypeSize.width/2, 30 + Test_globalTest._objectTypeSize.height/2))));
    }

    @Test
    void entityType_redoMoveByShiftValue() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_entityTypes.get(0).moveTo(new Point2D(20, 30));
        test_entityTypes.get(0).moveBy(15, -34);

        this._diagram.undoState();
        this._diagram.redoState();

        // Check result
        Assertions.assertFalse(this._diagram.canRedoState());
        Assertions.assertEquals(test_entityTypes.get(0).borderLeftTop(), new Point2D(35, -4));
        test_anchorPoints(test_entityTypes.get(0), new HashSet<>(List.of(new Point2D(35 + Test_globalTest._objectTypeSize.width/2, -4 + Test_globalTest._objectTypeSize.height/2))));
    }

    @Test
    void entityType_moveToPosAfterUndo() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_entityTypes.get(0).moveTo(new Point2D(20, 30));
        test_entityTypes.get(0).moveBy(15, -34);

        test_entityTypes.get(0).moveTo(new Point2D(20, 30));
        test_entityTypes.get(0).moveTo(new Point2D(30, -30));
        test_entityTypes.get(0).moveTo(new Point2D(40, 30));
        test_entityTypes.get(0).moveTo(new Point2D(50, -30));

        this._diagram.undoState();
        this._diagram.undoState();

        test_entityTypes.get(0).moveBy(15, 15);

        // Check result
        Assertions.assertFalse(this._diagram.canRedoState());
        Assertions.assertEquals(test_entityTypes.get(0).borderLeftTop(), new Point2D(45, -15));
        test_anchorPoints(test_entityTypes.get(0), new HashSet<>(List.of(new Point2D(45 + Test_globalTest._objectTypeSize.width/2, -15 + Test_globalTest._objectTypeSize.height/2))));
    }

    @Test
    void entityType_complexMovement() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EntityType()));

        test_entityTypes.get(0).moveTo(new Point2D(20, 30));
        test_entityTypes.get(0).moveBy(-100, 3);
        test_entityTypes.get(0).moveBy(-100, 20);
        test_entityTypes.get(0).moveBy(-149, -1001);
        test_entityTypes.get(0).moveBy(10, 3);
        test_entityTypes.get(0).moveTo(new Point2D(10, 3));

        this._diagram.undoState();
        this._diagram.undoState();
        this._diagram.redoState();
        this._diagram.redoState();
        this._diagram.undoState();

        // Check result
        Assertions.assertTrue(this._diagram.canRedoState());
        Assertions.assertEquals(test_entityTypes.get(0).borderLeftTop(), new Point2D(-319, -945));
        test_anchorPoints(test_entityTypes.get(0), new HashSet<>(List.of(new Point2D(-319 + Test_globalTest._objectTypeSize.width/2, -945 + Test_globalTest._objectTypeSize.height/2))));
    }

    // ----------------- VALUE TYPES ----------------
    @Test
    void valueType_moveToPos() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new ValueType()));
        test_valueTypes.get(0).moveTo(new Point2D(20, 30));

        // Check result
        Assertions.assertEquals(test_valueTypes.get(0).borderLeftTop(), new Point2D(20, 30));
        test_anchorPoints(test_valueTypes.get(0), new HashSet<>(List.of(new Point2D(20 + Test_globalTest._objectTypeSize.width/2, 30 + Test_globalTest._objectTypeSize.height/2))));
    }

    @Test
    void valueType_moveByShiftValue() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new ValueType()));
        test_valueTypes.get(0).moveTo(new Point2D(20, 30));
        test_valueTypes.get(0).moveBy(15, -34);
        test_valueTypes.get(0).setBorderSize(100, 50);

        // Check result
        Assertions.assertEquals(test_valueTypes.get(0).borderLeftTop(), new Point2D(35, -4));
        test_anchorPoints(test_valueTypes.get(0), new HashSet<>(List.of(new Point2D(35 + 50, -4 + 25))));
        test_anchorPoints(test_valueTypes.get(0), new HashSet<>(List.of(new Point2D(35 + Test_globalTest._objectTypeSize.width/2, -4 + Test_globalTest._objectTypeSize.height/2))));
    }

    @Test
    void valueType_undoMoveToPos() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new ValueType()));
        test_valueTypes.get(0).moveTo(new Point2D(20, 30));

        this._diagram.undoState();

        // Check result
        Assertions.assertTrue(this._diagram.canRedoState());
        Assertions.assertEquals(test_valueTypes.get(0).borderLeftTop(), new Point2D(0, 0));
        test_anchorPoints(test_valueTypes.get(0), new HashSet<>(List.of(new Point2D(0 + Test_globalTest._objectTypeSize.width/2, 0 + Test_globalTest._objectTypeSize.height/2))));
    }

    @Test
    void valueType_undoMoveByShiftValue() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new ValueType()));
        test_valueTypes.get(0).moveTo(new Point2D(20, 30));
        test_valueTypes.get(0).moveBy(15, -34);

        this._diagram.undoState();

        // Check result
        Assertions.assertTrue(this._diagram.canRedoState());
        Assertions.assertEquals(test_valueTypes.get(0).borderLeftTop(), new Point2D(20, 30));
        test_anchorPoints(test_valueTypes.get(0), new HashSet<>(List.of(new Point2D(20 + Test_globalTest._objectTypeSize.width/2, 30 + Test_globalTest._objectTypeSize.height/2))));
    }

    @Test
    void valueType_redoMoveToPos() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new ValueType()));
        test_valueTypes.get(0).moveTo(new Point2D(20, 30));

        this._diagram.undoState();
        this._diagram.redoState();

        // Check result
        Assertions.assertFalse(this._diagram.canRedoState());
        Assertions.assertEquals(test_valueTypes.get(0).borderLeftTop(), new Point2D(20, 30));
        test_anchorPoints(test_valueTypes.get(0), new HashSet<>(List.of(new Point2D(20 + Test_globalTest._objectTypeSize.width/2, 30 + Test_globalTest._objectTypeSize.height/2))));
    }

    @Test
    void valueType_redoMoveByShiftValue() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new ValueType()));
        test_valueTypes.get(0).moveTo(new Point2D(20, 30));
        test_valueTypes.get(0).moveBy(15, -34);

        this._diagram.undoState();
        this._diagram.redoState();

        // Check result
        Assertions.assertFalse(this._diagram.canRedoState());
        Assertions.assertEquals(test_valueTypes.get(0).borderLeftTop(), new Point2D(35, -4));
        test_anchorPoints(test_valueTypes.get(0), new HashSet<>(List.of(new Point2D(35 + Test_globalTest._objectTypeSize.width/2, -4 + Test_globalTest._objectTypeSize.height/2))));
    }

    @Test
    void valueType_moveToPosAfterUndo() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new ValueType()));
        test_valueTypes.get(0).moveTo(new Point2D(20, 30));
        test_valueTypes.get(0).moveBy(15, -34);

        test_valueTypes.get(0).moveTo(new Point2D(20, 30));
        test_valueTypes.get(0).moveTo(new Point2D(30, -30));
        test_valueTypes.get(0).moveTo(new Point2D(40, 30));
        test_valueTypes.get(0).moveTo(new Point2D(50, -30));

        this._diagram.undoState();
        this._diagram.undoState();

        test_valueTypes.get(0).moveBy(15, 15);

        // Check result
        Assertions.assertFalse(this._diagram.canRedoState());
        Assertions.assertEquals(test_valueTypes.get(0).borderLeftTop(), new Point2D(45, -15));
        test_anchorPoints(test_valueTypes.get(0), new HashSet<>(List.of(new Point2D(45 + Test_globalTest._objectTypeSize.width/2, -15 + Test_globalTest._objectTypeSize.height/2))));
    }

    @Test
    void valueType_complexMovement() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new ValueType()));

        test_valueTypes.get(0).moveTo(new Point2D(20, 30));
        test_valueTypes.get(0).moveBy(-100, 3);
        test_valueTypes.get(0).moveBy(-100, 20);
        test_valueTypes.get(0).moveBy(-149, -1001);
        test_valueTypes.get(0).moveBy(10, 3);
        test_valueTypes.get(0).moveTo(new Point2D(10, 3));

        this._diagram.undoState();
        this._diagram.undoState();
        this._diagram.redoState();
        this._diagram.redoState();
        this._diagram.undoState();

        // Check result
        Assertions.assertTrue(this._diagram.canRedoState());
        Assertions.assertEquals(test_valueTypes.get(0).borderLeftTop(), new Point2D(-319, -945));
        test_anchorPoints(test_valueTypes.get(0), new HashSet<>(List.of(new Point2D(-319 + Test_globalTest._objectTypeSize.width/2, -945 + Test_globalTest._objectTypeSize.height/2))));
    }

    // ------------------ PREDICATE -----------------
    @Test
    void predicate_checkSize() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));

        // Check result
        Assertions.assertEquals(test_predicates.get(0).getRole(0).borderWidth() * 3, test_predicates.get(0).borderWidth());
        Assertions.assertEquals(test_predicates.get(0).getRole(0).borderHeight(),    test_predicates.get(0).borderHeight());
        Assertions.assertEquals(Test_globalTest._roleSize.width * 3, test_predicates.get(0).borderWidth());
        Assertions.assertEquals(Test_globalTest._roleSize.height,    test_predicates.get(0).borderHeight());

        test_anchorPoints(test_predicates.get(0), new HashSet<>());
        test_anchorPoints(test_predicates.get(0).getRole(0), new HashSet<>(List.of(new Point2D(test_predicates.get(0).borderLeftTop().x() + 0, test_predicates.get(0).borderLeftTop().y() + Test_globalTest._roleSize.height/2))));
        test_anchorPoints(test_predicates.get(0).getRole(1), new HashSet<>(List.of(new Point2D(test_predicates.get(0).borderLeftTop().x() + Test_globalTest._roleSize.width * 3/2, test_predicates.get(0).borderLeftTop().y() + 0),
                new Point2D(test_predicates.get(0).borderLeftTop().x() + Test_globalTest._roleSize.width * 3/2, test_predicates.get(0).borderLeftTop().y() + Test_globalTest._roleSize.height))));
        test_anchorPoints(test_predicates.get(0).getRole(2), new HashSet<>(List.of(new Point2D(test_predicates.get(0).borderLeftTop().x() + Test_globalTest._roleSize.width * 3, test_predicates.get(0).borderLeftTop().y() + Test_globalTest._roleSize.height/2))));
    }

    @Test
    void predicate_changeOrientationToVertical() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));
        test_predicates.get(0).setOrientation(DiagramElement.Orientation.VERTICAL);

        // Check result
        Assertions.assertEquals(test_predicates.get(0).getRole(0).borderWidth(),      test_predicates.get(0).borderWidth());
        Assertions.assertEquals(test_predicates.get(0).getRole(0).borderHeight() * 3, test_predicates.get(0).borderHeight());
        Assertions.assertEquals(Test_globalTest._roleSize.height, test_predicates.get(0).borderWidth());
        Assertions.assertEquals(Test_globalTest._roleSize.width * 3, test_predicates.get(0).borderHeight());

        test_anchorPoints(test_predicates.get(0), new HashSet<>());
        test_anchorPoints(test_predicates.get(0).getRole(0), new HashSet<>(List.of(new Point2D(test_predicates.get(0).borderLeftTop().x() + Test_globalTest._roleSize.height/2, test_predicates.get(0).borderLeftTop().y() + 0))));
        test_anchorPoints(test_predicates.get(0).getRole(1), new HashSet<>(List.of(new Point2D(test_predicates.get(0).borderLeftTop().x() + 0, test_predicates.get(0).borderLeftTop().y() + Test_globalTest._roleSize.width * 3/2),
                new Point2D(test_predicates.get(0).borderLeftTop().x() + Test_globalTest._roleSize.height, test_predicates.get(0).borderLeftTop().y() + Test_globalTest._roleSize.width * 3/2))));
        test_anchorPoints(test_predicates.get(0).getRole(2), new HashSet<>(List.of(new Point2D(test_predicates.get(0).borderLeftTop().x() + Test_globalTest._roleSize.height/2, test_predicates.get(0).borderLeftTop().y() + Test_globalTest._roleSize.width * 3))));
    }

    @Test
    void predicate_changeOrientationBackToHorizontal() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));

        test_predicates.get(0).setOrientation(DiagramElement.Orientation.VERTICAL);
        test_predicates.get(0).setOrientation(DiagramElement.Orientation.HORIZONTAL);

        // Check result
        Assertions.assertEquals(test_predicates.get(0).getRole(0).borderWidth() * 3, test_predicates.get(0).borderWidth());
        Assertions.assertEquals(test_predicates.get(0).getRole(0).borderHeight(),    test_predicates.get(0).borderHeight());
        Assertions.assertEquals(Test_globalTest._roleSize.width * 3, test_predicates.get(0).borderWidth());
        Assertions.assertEquals(Test_globalTest._roleSize.height,    test_predicates.get(0).borderHeight());

        test_anchorPoints(test_predicates.get(0), new HashSet<>());
        test_anchorPoints(test_predicates.get(0).getRole(0), new HashSet<>(List.of(new Point2D(test_predicates.get(0).borderLeftTop().x() + 0, test_predicates.get(0).borderLeftTop().y() + Test_globalTest._roleSize.height/2))));
        test_anchorPoints(test_predicates.get(0).getRole(1), new HashSet<>(List.of(new Point2D(test_predicates.get(0).borderLeftTop().x() + Test_globalTest._roleSize.width * 3/2, test_predicates.get(0).borderLeftTop().y() + 0),
                new Point2D(test_predicates.get(0).borderLeftTop().x() + Test_globalTest._roleSize.width * 3/2, test_predicates.get(0).borderLeftTop().y() + Test_globalTest._roleSize.height))));
        test_anchorPoints(test_predicates.get(0).getRole(2), new HashSet<>(List.of(new Point2D(test_predicates.get(0).borderLeftTop().x() + Test_globalTest._roleSize.width * 3, test_predicates.get(0).borderLeftTop().y() + Test_globalTest._roleSize.height/2))));
    }

    @Test
    void predicate_changeOrientationToHorizontalTwice() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));

        test_predicates.get(0).setOrientation(DiagramElement.Orientation.HORIZONTAL);
        test_predicates.get(0).setOrientation(DiagramElement.Orientation.HORIZONTAL);

        // Check result
        Assertions.assertEquals(test_predicates.get(0).getRole(0).borderWidth() * 3, test_predicates.get(0).borderWidth());
        Assertions.assertEquals(test_predicates.get(0).getRole(0).borderHeight(),    test_predicates.get(0).borderHeight());
        Assertions.assertEquals(Test_globalTest._roleSize.width * 3, test_predicates.get(0).borderWidth());
        Assertions.assertEquals(Test_globalTest._roleSize.height,    test_predicates.get(0).borderHeight());

        test_anchorPoints(test_predicates.get(0), new HashSet<>());
        test_anchorPoints(test_predicates.get(0).getRole(0), new HashSet<>(List.of(new Point2D(test_predicates.get(0).borderLeftTop().x() + 0, test_predicates.get(0).borderLeftTop().y() + Test_globalTest._roleSize.height/2))));
        test_anchorPoints(test_predicates.get(0).getRole(1), new HashSet<>(List.of(new Point2D(test_predicates.get(0).borderLeftTop().x() + Test_globalTest._roleSize.width * 3/2, test_predicates.get(0).borderLeftTop().y() + 0),
                new Point2D(test_predicates.get(0).borderLeftTop().x() + Test_globalTest._roleSize.width * 3/2, test_predicates.get(0).borderLeftTop().y() + Test_globalTest._roleSize.height))));
        test_anchorPoints(test_predicates.get(0).getRole(2), new HashSet<>(List.of(new Point2D(test_predicates.get(0).borderLeftTop().x() + Test_globalTest._roleSize.width * 3, test_predicates.get(0).borderLeftTop().y() + Test_globalTest._roleSize.height/2))));
    }

    @Test
    void predicate_changeOrientationToVerticalTwice() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));

        test_predicates.get(0).setOrientation(DiagramElement.Orientation.VERTICAL);
        test_predicates.get(0).setOrientation(DiagramElement.Orientation.VERTICAL);

        // Check result
        Assertions.assertEquals(test_predicates.get(0).getRole(0).borderWidth(),      test_predicates.get(0).borderWidth());
        Assertions.assertEquals(test_predicates.get(0).getRole(0).borderHeight() * 3, test_predicates.get(0).borderHeight());
        Assertions.assertEquals(Test_globalTest._roleSize.height, test_predicates.get(0).borderWidth());
        Assertions.assertEquals(Test_globalTest._roleSize.width * 3, test_predicates.get(0).borderHeight());

        test_anchorPoints(test_predicates.get(0), new HashSet<>());
        test_anchorPoints(test_predicates.get(0).getRole(0), new HashSet<>(List.of(new Point2D(test_predicates.get(0).borderLeftTop().x() + Test_globalTest._roleSize.height/2, test_predicates.get(0).borderLeftTop().y() + 0))));
        test_anchorPoints(test_predicates.get(0).getRole(1), new HashSet<>(List.of(new Point2D(test_predicates.get(0).borderLeftTop().x() + 0, test_predicates.get(0).borderLeftTop().y() + Test_globalTest._roleSize.width * 3/2),
                new Point2D(test_predicates.get(0).borderLeftTop().x() + Test_globalTest._roleSize.height, test_predicates.get(0).borderLeftTop().y() + Test_globalTest._roleSize.width * 3/2))));
        test_anchorPoints(test_predicates.get(0).getRole(2), new HashSet<>(List.of(new Point2D(test_predicates.get(0).borderLeftTop().x() + Test_globalTest._roleSize.height/2, test_predicates.get(0).borderLeftTop().y() + Test_globalTest._roleSize.width * 3))));
    }

    @Test
    void predicate_complexChangingOrientation() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(3)));

        test_predicates.get(0).setOrientation(DiagramElement.Orientation.VERTICAL);
        test_predicates.get(0).setOrientation(DiagramElement.Orientation.HORIZONTAL);
        test_predicates.get(0).setOrientation(DiagramElement.Orientation.VERTICAL);
        test_predicates.get(0).setOrientation(DiagramElement.Orientation.HORIZONTAL);
        test_predicates.get(0).setOrientation(DiagramElement.Orientation.VERTICAL);
        test_predicates.get(0).setOrientation(DiagramElement.Orientation.HORIZONTAL);
        test_predicates.get(0).setOrientation(DiagramElement.Orientation.VERTICAL);
        test_predicates.get(0).setOrientation(DiagramElement.Orientation.HORIZONTAL);

        // Check result
        Assertions.assertEquals(test_predicates.get(0).getRole(0).borderWidth() * 3, test_predicates.get(0).borderWidth());
        Assertions.assertEquals(test_predicates.get(0).getRole(0).borderHeight(),    test_predicates.get(0).borderHeight());
        Assertions.assertEquals(Test_globalTest._roleSize.width * 3, test_predicates.get(0).borderWidth());
        Assertions.assertEquals(Test_globalTest._roleSize.height,    test_predicates.get(0).borderHeight());

        test_anchorPoints(test_predicates.get(0), new HashSet<>());
        test_anchorPoints(test_predicates.get(0).getRole(0), new HashSet<>(List.of(new Point2D(test_predicates.get(0).borderLeftTop().x() + 0, test_predicates.get(0).borderLeftTop().y() + Test_globalTest._roleSize.height/2))));
        test_anchorPoints(test_predicates.get(0).getRole(1), new HashSet<>(List.of(new Point2D(test_predicates.get(0).borderLeftTop().x() + Test_globalTest._roleSize.width * 3/2, test_predicates.get(0).borderLeftTop().y() + 0),
                new Point2D(test_predicates.get(0).borderLeftTop().x() + Test_globalTest._roleSize.width * 3/2, test_predicates.get(0).borderLeftTop().y() + Test_globalTest._roleSize.height))));
        test_anchorPoints(test_predicates.get(0).getRole(2), new HashSet<>(List.of(new Point2D(test_predicates.get(0).borderLeftTop().x() + Test_globalTest._roleSize.width * 3, test_predicates.get(0).borderLeftTop().y() + Test_globalTest._roleSize.height/2))));
    }

    // ----------- OBJECTIFIED PREDICATES -----------
    // ----------------- CONSTRAINTS ----------------
}
