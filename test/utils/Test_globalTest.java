package utils;

import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.DiagramElement;
import com.orm2_graph_library.core.Node;
import com.orm2_graph_library.edges.RoleConstraintRelationEdge;
import com.orm2_graph_library.edges.RoleRelationEdge;
import com.orm2_graph_library.edges.SubtypingConstraintRelationEdge;
import com.orm2_graph_library.edges.SubtypingRelationEdge;
import com.orm2_graph_library.nodes.common.EntityType;
import com.orm2_graph_library.nodes.common.ValueType;
import com.orm2_graph_library.nodes.constraints.Constraint;
import com.orm2_graph_library.nodes.predicates.ObjectifiedPredicate;
import com.orm2_graph_library.nodes.predicates.Predicate;
import com.orm2_graph_library.nodes.predicates.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Test_globalTest {
    protected Diagram _diagram;

    protected ArrayList<EntityType>           test_entityTypes           = new ArrayList<>();
    protected ArrayList<ValueType>            test_valueTypes            = new ArrayList<>();
    protected ArrayList<Predicate>            test_predicates            = new ArrayList<>();
    protected ArrayList<Role>                 test_roles                 = new ArrayList<>();
    protected ArrayList<ObjectifiedPredicate> test_objectifiedPredicates = new ArrayList<>();
    protected ArrayList<Constraint>           test_constraints           = new ArrayList<>();

    protected ArrayList<SubtypingRelationEdge>           test_subtypingRelationEdges           = new ArrayList<>();
    protected ArrayList<RoleRelationEdge>                test_roleRelationEdges                = new ArrayList<>();
    protected ArrayList<RoleConstraintRelationEdge>      test_roleConstraintRelationEdges      = new ArrayList<>();
    protected ArrayList<SubtypingConstraintRelationEdge> test_subtypingConstraintRelationEdges = new ArrayList<>();

    protected<T extends DiagramElement> T test_addDiagramElement(T diagramElement) {
        if (diagramElement instanceof EntityType)     { test_entityTypes.add((EntityType)diagramElement); }
        else if (diagramElement instanceof ValueType) { test_valueTypes.add((ValueType)diagramElement); }
        else if (diagramElement instanceof Predicate) {
            test_predicates.add((Predicate)diagramElement);
            for (int i=0; i<((Predicate)diagramElement).arity(); i++) {
                test_roles.add(((Predicate)diagramElement).getRole(i));
            }
        }
        else if (diagramElement instanceof ObjectifiedPredicate) {
            test_objectifiedPredicates.add((ObjectifiedPredicate)diagramElement);
            test_predicates.add(((ObjectifiedPredicate)diagramElement).innerPredicate());
            for (int i=0; i<((ObjectifiedPredicate)diagramElement).innerPredicate().arity(); i++) {
                test_roles.add(((ObjectifiedPredicate)diagramElement).innerPredicate().getRole(i));
            }
        }
        else if (diagramElement instanceof Constraint) { test_constraints.add((Constraint)diagramElement); }

        else if (diagramElement instanceof SubtypingRelationEdge)           { test_subtypingRelationEdges.add((SubtypingRelationEdge)diagramElement); }
        else if (diagramElement instanceof RoleRelationEdge)                { test_roleRelationEdges.add((RoleRelationEdge)diagramElement); }
        else if (diagramElement instanceof SubtypingConstraintRelationEdge) { test_subtypingConstraintRelationEdges.add((SubtypingConstraintRelationEdge)diagramElement); }
        else if (diagramElement instanceof RoleConstraintRelationEdge)      { test_roleConstraintRelationEdges.add((RoleConstraintRelationEdge)diagramElement); }

        return diagramElement;
    }

    protected<T extends DiagramElement> void test_removeDiagramElement(T diagramElement) {
        if (diagramElement instanceof EntityType)     { test_entityTypes.remove((EntityType)diagramElement); }
        else if (diagramElement instanceof ValueType) { test_valueTypes.remove((ValueType)diagramElement); }
        else if (diagramElement instanceof Predicate) {
            test_predicates.remove((Predicate)diagramElement);
            for (int i=0; i<((Predicate)diagramElement).arity(); i++) {
                test_roles.remove(((Predicate)diagramElement).getRole(i));
            }
        }
        else if (diagramElement instanceof ObjectifiedPredicate) {
            test_objectifiedPredicates.remove((ObjectifiedPredicate)diagramElement);
            test_predicates.remove(((ObjectifiedPredicate)diagramElement).innerPredicate());
            for (int i=0; i<((ObjectifiedPredicate)diagramElement).innerPredicate().arity(); i++) {
                test_roles.remove(((ObjectifiedPredicate)diagramElement).innerPredicate().getRole(i));
            }
        }
        else if (diagramElement instanceof Constraint) { test_constraints.remove((Constraint)diagramElement); }

        else if (diagramElement instanceof SubtypingRelationEdge)           { test_subtypingRelationEdges.remove((SubtypingRelationEdge)diagramElement); }
        else if (diagramElement instanceof RoleRelationEdge)                { test_roleRelationEdges.remove((RoleRelationEdge)diagramElement); }
        else if (diagramElement instanceof SubtypingConstraintRelationEdge) { test_subtypingConstraintRelationEdges.remove((SubtypingConstraintRelationEdge)diagramElement); }
        else if (diagramElement instanceof RoleConstraintRelationEdge)      { test_roleConstraintRelationEdges.remove((RoleConstraintRelationEdge)diagramElement); }
    }

    protected void test_removeAllDiagramElements() {
        test_entityTypes.clear();
        test_valueTypes.clear();
        test_predicates.clear();
        test_roles.clear();
        test_objectifiedPredicates.clear();
        test_constraints.clear();

        test_subtypingRelationEdges.clear();
        test_roleRelationEdges.clear();
        test_subtypingConstraintRelationEdges.clear();
        test_roleConstraintRelationEdges.clear();
    }

    @BeforeEach
    protected void beginTest_global() {
        this._diagram = new Diagram();

        test_entityTypes.clear();
        test_valueTypes.clear();
        test_predicates.clear();
        test_objectifiedPredicates.clear();
        test_constraints.clear();

        test_subtypingRelationEdges.clear();
        test_roleRelationEdges.clear();
        test_roleConstraintRelationEdges.clear();
        test_subtypingConstraintRelationEdges.clear();
    }

    @AfterEach
    protected void endTest_global() {
        for (var o : test_entityTypes)           { Assertions.assertTrue(o.isOwnerDiagram(this._diagram)); }
        for (var o : test_valueTypes)            { Assertions.assertTrue(o.isOwnerDiagram(this._diagram)); }
        for (var o : test_predicates)            { Assertions.assertTrue(o.isOwnerDiagram(this._diagram)); }
        for (var o : test_roles)                 { Assertions.assertTrue(o.isOwnerDiagram(this._diagram)); }
        for (var o : test_objectifiedPredicates) { Assertions.assertTrue(o.isOwnerDiagram(this._diagram)); }
        for (var o : test_constraints)           { Assertions.assertTrue(o.isOwnerDiagram(this._diagram)); }

        for (var o : test_subtypingRelationEdges)           { Assertions.assertTrue(o.isOwnerDiagram(this._diagram)); }
        for (var o : test_roleRelationEdges)                { Assertions.assertTrue(o.isOwnerDiagram(this._diagram)); }
        for (var o : test_roleConstraintRelationEdges)      { Assertions.assertTrue(o.isOwnerDiagram(this._diagram)); }
        for (var o : test_subtypingConstraintRelationEdges) { Assertions.assertTrue(o.isOwnerDiagram(this._diagram)); }

        Set<DiagramElement> allDiagramElements = new HashSet<>();
        allDiagramElements.addAll(test_entityTypes);
        allDiagramElements.addAll(test_valueTypes);
        allDiagramElements.addAll(test_predicates);
        allDiagramElements.addAll(test_roles);
        allDiagramElements.addAll(test_objectifiedPredicates);
        allDiagramElements.addAll(test_constraints);

        allDiagramElements.addAll(test_subtypingRelationEdges);
        allDiagramElements.addAll(test_roleRelationEdges);
        allDiagramElements.addAll(test_roleConstraintRelationEdges);
        allDiagramElements.addAll(test_subtypingConstraintRelationEdges);

        Assertions.assertEquals(allDiagramElements, this._diagram.getElements(DiagramElement.class).collect(Collectors.toCollection(HashSet::new)));
    }
}
