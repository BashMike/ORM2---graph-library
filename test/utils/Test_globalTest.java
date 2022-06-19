package utils;

import com.orm2_graph_library.core.*;
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
import com.orm2_graph_library.nodes.predicates.RolesSequence;
import com.orm2_graph_library.utils.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.awt.*;
import java.util.*;
import java.util.stream.Collectors;

public class Test_globalTest {
    // =========== ATTRIBUTES & OPERATIONS ==========
    // ------------------- GLOBAL -------------------
    static protected Dimension _objectTypeSize                           = new Dimension(100, 50);
    static protected int       _constraintSize                           = 35;
    static protected Dimension _objectifiedPredicateGapsDistances        = new Dimension(20, 20);
    static protected int       _objectifiedPredicateBorderRoundingDegree = 15;
    static protected Dimension _roleSize                                 = new Dimension(20, 10);

    protected Diagram _diagram;

    protected ArrayList<EntityType>           test_entityTypes           = new ArrayList<>();
    protected ArrayList<ValueType>            test_valueTypes            = new ArrayList<>();
    protected ArrayList<Predicate>            test_predicates            = new ArrayList<>();
    protected ArrayList<Role>                 test_roles                 = new ArrayList<>();
    protected ArrayList<ObjectifiedPredicate> test_objectifiedPredicates = new ArrayList<>();
    protected ArrayList<Constraint>           test_constraints           = new ArrayList<>();
    protected ArrayList<RolesSequence>        test_rolesSequences        = new ArrayList<>();

    protected ArrayList<SubtypingRelationEdge>           test_subtypingRelationEdges           = new ArrayList<>();
    protected ArrayList<RoleRelationEdge>                test_roleRelationEdges                = new ArrayList<>();
    protected ArrayList<RoleConstraintRelationEdge>      test_roleConstraintRelationEdges      = new ArrayList<>();
    protected ArrayList<SubtypingConstraintRelationEdge> test_subtypingConstraintRelationEdges = new ArrayList<>();

    final private Map<Pair<DiagramElement, DiagramElement>, Class<? extends Edge>> test_connectedDiagramElements = new HashMap<>();

    protected<T extends DiagramElement> T test_addDiagramElement(T diagramElement) {
        if (diagramElement instanceof EntityType)     {
            test_entityTypes.add((EntityType)diagramElement);
            ((EntityType)diagramElement).setBorderSize(Test_globalTest._objectTypeSize.width, Test_globalTest._objectTypeSize.height);
        }
        else if (diagramElement instanceof ValueType) {
            test_valueTypes.add((ValueType)diagramElement);
            ((ValueType)diagramElement).setBorderSize(Test_globalTest._objectTypeSize.width, Test_globalTest._objectTypeSize.height);
        }
        else if (diagramElement instanceof Predicate) {
            test_predicates.add((Predicate)diagramElement);
            for (int i=0; i<((Predicate)diagramElement).arity(); i++) {
                test_roles.add(((Predicate)diagramElement).getRole(i));
            }
            ((Predicate)diagramElement).setRolesBorderSize(Test_globalTest._roleSize.width, Test_globalTest._roleSize.height);
        }
        else if (diagramElement instanceof ObjectifiedPredicate) {
            test_objectifiedPredicates.add((ObjectifiedPredicate)diagramElement);

            if (!test_predicates.contains(((ObjectifiedPredicate)diagramElement).innerPredicate())) {
                test_predicates.add(((ObjectifiedPredicate)diagramElement).innerPredicate());
                for (int i=0; i<((ObjectifiedPredicate)diagramElement).innerPredicate().arity(); i++) {
                    test_roles.add(((ObjectifiedPredicate)diagramElement).innerPredicate().getRole(i));
                }
            }

            ((ObjectifiedPredicate)diagramElement).setGapsDistances(Test_globalTest._objectifiedPredicateGapsDistances.width, Test_globalTest._objectifiedPredicateGapsDistances.height);
            ((ObjectifiedPredicate)diagramElement).setBorderRoundingDegree(Test_globalTest._objectifiedPredicateBorderRoundingDegree);
        }
        else if (diagramElement instanceof Constraint) {
            test_constraints.add((Constraint)diagramElement);
            ((Constraint)diagramElement).setBorderSize(Test_globalTest._constraintSize);
        }
        else if (diagramElement instanceof RolesSequence) { test_rolesSequences.add((RolesSequence)diagramElement);}

        else if (diagramElement instanceof SubtypingRelationEdge) {
            test_subtypingRelationEdges.add((SubtypingRelationEdge)diagramElement);
            test_connectedDiagramElements.put(new Pair<>(((SubtypingRelationEdge)diagramElement).begin(), ((SubtypingRelationEdge)diagramElement).end()), SubtypingRelationEdge.class);
        }
        else if (diagramElement instanceof RoleRelationEdge) {
            test_roleRelationEdges.add((RoleRelationEdge)diagramElement);
            test_connectedDiagramElements.put(new Pair<>(((RoleRelationEdge)diagramElement).begin(), ((RoleRelationEdge)diagramElement).end()), RoleRelationEdge.class);
        }
        else if (diagramElement instanceof SubtypingConstraintRelationEdge) {
            test_subtypingConstraintRelationEdges.add((SubtypingConstraintRelationEdge)diagramElement);
            test_connectedDiagramElements.put(new Pair<>(((SubtypingConstraintRelationEdge)diagramElement).begin(), ((SubtypingConstraintRelationEdge)diagramElement).end()), SubtypingConstraintRelationEdge.class);
        }
        else if (diagramElement instanceof RoleConstraintRelationEdge) {
            test_roleConstraintRelationEdges.add((RoleConstraintRelationEdge)diagramElement);
            test_connectedDiagramElements.put(new Pair<>(((RoleConstraintRelationEdge)diagramElement).begin(), ((RoleConstraintRelationEdge)diagramElement).end()), RoleConstraintRelationEdge.class);
        }

        if (!this._diagram.isInnerElement(diagramElement)) {
            throw new RuntimeException("TEST_ERROR :: Tried to add element in test set without adding to the diagram.");
        }

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

    // ---------------- LOGIC ERRORS ----------------
    private Set<LogicError> test_expLogicErrors = new HashSet<>();

    private Map<EntityType, Set<LogicError>>           test_entityTypesLogicErrors           = new HashMap<>();
    private Map<ValueType, Set<LogicError>>            test_valueTypesLogicErrors            = new HashMap<>();
    private Map<Predicate, Set<LogicError>>            test_predicatesLogicErrors            = new HashMap<>();
    private Map<Role, Set<LogicError>>                 test_rolesLogicErrors                 = new HashMap<>();
    private Map<ObjectifiedPredicate, Set<LogicError>> test_objectifiedPredicatesLogicErrors = new HashMap<>();
    private Map<Constraint, Set<LogicError>>           test_constraintsLogicErrors           = new HashMap<>();
    private Map<RolesSequence, Set<LogicError>>        test_rolesSequencesLogicErrors        = new HashMap<>();

    private Map<SubtypingRelationEdge, Set<LogicError>>           test_subtypingRelationEdgesLogicErrors           = new HashMap<>();
    private Map<RoleRelationEdge, Set<LogicError>>                test_roleRelationEdgesLogicErrors                = new HashMap<>();
    private Map<SubtypingConstraintRelationEdge, Set<LogicError>> test_subtypingConstraintRelationEdgesLogicErrors = new HashMap<>();
    private Map<RoleConstraintRelationEdge, Set<LogicError>>      test_roleConstraintRelationEdgesLogicErrors      = new HashMap<>();

    protected void test_addLogicErrorTo(DiagramElement diagramElement, LogicError logicError) {
        if      (diagramElement instanceof EntityType)                      { test_entityTypesLogicErrors.computeIfAbsent((EntityType)diagramElement, k -> new HashSet<>()).add(logicError); }
        else if (diagramElement instanceof ValueType)                       { test_valueTypesLogicErrors.computeIfAbsent((ValueType)diagramElement, k -> new HashSet<>()).add(logicError); }
        else if (diagramElement instanceof Predicate)                       { test_predicatesLogicErrors.computeIfAbsent((Predicate)diagramElement, k -> new HashSet<>()).add(logicError); }
        else if (diagramElement instanceof Role)                            { test_rolesLogicErrors.computeIfAbsent((Role)diagramElement, k -> new HashSet<>()).add(logicError); }
        else if (diagramElement instanceof ObjectifiedPredicate)            { test_objectifiedPredicatesLogicErrors.computeIfAbsent((ObjectifiedPredicate)diagramElement, k -> new HashSet<>()).add(logicError); }
        else if (diagramElement instanceof Constraint)                      { test_constraintsLogicErrors.computeIfAbsent((Constraint)diagramElement, k -> new HashSet<>()).add(logicError); }
        else if (diagramElement instanceof RolesSequence)                   { test_rolesSequencesLogicErrors.computeIfAbsent((RolesSequence)diagramElement, k -> new HashSet<>()).add(logicError); }

        else if (diagramElement instanceof SubtypingRelationEdge)           { test_subtypingRelationEdgesLogicErrors.computeIfAbsent((SubtypingRelationEdge)diagramElement, k -> new HashSet<>()).add(logicError); }
        else if (diagramElement instanceof RoleRelationEdge)                { test_roleRelationEdgesLogicErrors.computeIfAbsent((RoleRelationEdge)diagramElement, k -> new HashSet<>()).add(logicError); }
        else if (diagramElement instanceof SubtypingConstraintRelationEdge) { test_subtypingConstraintRelationEdgesLogicErrors.computeIfAbsent((SubtypingConstraintRelationEdge)diagramElement, k -> new HashSet<>()).add(logicError); }
        else if (diagramElement instanceof RoleConstraintRelationEdge)      { test_roleConstraintRelationEdgesLogicErrors.computeIfAbsent((RoleConstraintRelationEdge)diagramElement, k -> new HashSet<>()).add(logicError); }

        if (!this._diagram.isInnerElement(diagramElement)) {
            throw new RuntimeException("TEST_ERROR :: An attempt to add logic error to element which isn't added to the diagram.");
        }
    }

    protected void test_addLogicErrorsTo(DiagramElement diagramElement, Collection<LogicError> logicErrors) {
        if      (diagramElement instanceof EntityType)                      { test_entityTypesLogicErrors.computeIfAbsent((EntityType)diagramElement, k -> new HashSet<>()).addAll(logicErrors); }
        else if (diagramElement instanceof ValueType)                       { test_valueTypesLogicErrors.computeIfAbsent((ValueType)diagramElement, k -> new HashSet<>()).addAll(logicErrors); }
        else if (diagramElement instanceof Predicate)                       { test_predicatesLogicErrors.computeIfAbsent((Predicate)diagramElement, k -> new HashSet<>()).addAll(logicErrors); }
        else if (diagramElement instanceof Role)                            { test_rolesLogicErrors.computeIfAbsent((Role)diagramElement, k -> new HashSet<>()).addAll(logicErrors); }
        else if (diagramElement instanceof ObjectifiedPredicate)            { test_objectifiedPredicatesLogicErrors.computeIfAbsent((ObjectifiedPredicate)diagramElement, k -> new HashSet<>()).addAll(logicErrors); }
        else if (diagramElement instanceof Constraint)                      { test_constraintsLogicErrors.computeIfAbsent((Constraint)diagramElement, k -> new HashSet<>()).addAll(logicErrors); }
        else if (diagramElement instanceof RolesSequence)                   { test_rolesSequencesLogicErrors.computeIfAbsent((RolesSequence)diagramElement, k -> new HashSet<>()).addAll(logicErrors); }

        else if (diagramElement instanceof SubtypingRelationEdge)           { test_subtypingRelationEdgesLogicErrors.computeIfAbsent((SubtypingRelationEdge)diagramElement, k -> new HashSet<>()).addAll(logicErrors); }
        else if (diagramElement instanceof RoleRelationEdge)                { test_roleRelationEdgesLogicErrors.computeIfAbsent((RoleRelationEdge)diagramElement, k -> new HashSet<>()).addAll(logicErrors); }
        else if (diagramElement instanceof SubtypingConstraintRelationEdge) { test_subtypingConstraintRelationEdgesLogicErrors.computeIfAbsent((SubtypingConstraintRelationEdge)diagramElement, k -> new HashSet<>()).addAll(logicErrors); }
        else if (diagramElement instanceof RoleConstraintRelationEdge)      { test_roleConstraintRelationEdgesLogicErrors.computeIfAbsent((RoleConstraintRelationEdge)diagramElement, k -> new HashSet<>()).addAll(logicErrors); }

        if (!this._diagram.isInnerElement(diagramElement)) {
            throw new RuntimeException("TEST_ERROR :: An attempt to add logic errors to element which isn't added to the diagram.");
        }
    }

    // ---------------- ACTION ERRORS ---------------
    class TestActionErrorListener implements ActionErrorListener {
        private Set<ActionError> _actionErrors = new HashSet<>();
        public Set<ActionError> actionErrors() { return this._actionErrors; }

        @Override
        public void handle(ActionError actionError) { this._actionErrors.add(actionError); }
    }

    private TestActionErrorListener _actionErrorListener = null;
    final protected Set<ActionError> test_actionErrors = new HashSet<>();

    // ==================== TEST ====================
    @BeforeEach
    protected void beginTest_global() {
        this._diagram = new Diagram();
        if (this._actionErrorListener == null) {
             this._actionErrorListener = new TestActionErrorListener();
             this._diagram.addActionErrorListener(this._actionErrorListener);
        }

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

    @BeforeEach
    protected void beginTest_logicErrors() {
        this.test_expLogicErrors.clear();
    }

    @BeforeEach
    protected void beginTest_actionErrors() {
        this._actionErrorListener._actionErrors.clear();
        test_actionErrors.clear();
    }

    @AfterEach
    protected void endTest_global() {
        // this._diagram.saveToXmlFile("hello.xml", true);
        // this._diagram = Diagram.loadFromXmlFile("hello.xml");

        for (var o : test_entityTypes)           { Assertions.assertTrue(o.isOwnerDiagram(this._diagram), "" + o + " :: " + test_entityTypes.indexOf(o)); }
        for (var o : test_valueTypes)            { Assertions.assertTrue(o.isOwnerDiagram(this._diagram), "" + o + " :: " + test_valueTypes.indexOf(o)); }
        for (var o : test_predicates)            { Assertions.assertTrue(o.isOwnerDiagram(this._diagram), "" + o + " :: " + test_predicates.indexOf(o)); }
        for (var o : test_roles)                 { Assertions.assertTrue(o.isOwnerDiagram(this._diagram), "" + o + " :: " + test_roles.indexOf(o)); }
        for (var o : test_objectifiedPredicates) { Assertions.assertTrue(o.isOwnerDiagram(this._diagram), "" + o + " :: " + test_objectifiedPredicates.indexOf(o)); }
        for (var o : test_constraints)           { Assertions.assertTrue(o.isOwnerDiagram(this._diagram), "" + o + " :: " + test_constraints.indexOf(o)); }

        for (var o : test_subtypingRelationEdges)           { Assertions.assertTrue(o.isOwnerDiagram(this._diagram), "" + o + " :: " + test_subtypingRelationEdges.indexOf(o)); }
        for (var o : test_roleRelationEdges)                { Assertions.assertTrue(o.isOwnerDiagram(this._diagram), "" + o + " :: " + test_roleRelationEdges.indexOf(o)); }
        for (var o : test_roleConstraintRelationEdges)      { Assertions.assertTrue(o.isOwnerDiagram(this._diagram), "" + o + " :: " + test_roleConstraintRelationEdges.indexOf(o)); }
        for (var o : test_subtypingConstraintRelationEdges) { Assertions.assertTrue(o.isOwnerDiagram(this._diagram), "" + o + " :: " + test_subtypingConstraintRelationEdges.indexOf(o)); }

        Set<DiagramElement> allDiagramElements = new HashSet<>();
        allDiagramElements.addAll(test_entityTypes);
        allDiagramElements.addAll(test_valueTypes);
        allDiagramElements.addAll(test_predicates);
        allDiagramElements.addAll(test_roles);
        allDiagramElements.addAll(test_objectifiedPredicates);
        allDiagramElements.addAll(test_constraints);
        allDiagramElements.addAll(test_rolesSequences);

        allDiagramElements.addAll(test_subtypingRelationEdges);
        allDiagramElements.addAll(test_roleRelationEdges);
        allDiagramElements.addAll(test_roleConstraintRelationEdges);
        allDiagramElements.addAll(test_subtypingConstraintRelationEdges);

        Assertions.assertEquals(allDiagramElements, this._diagram.getElements(DiagramElement.class).collect(Collectors.toCollection(HashSet::new)));

        // Check that all needed diagram elements aren't connected with each other (including self connections)
        boolean areAllNecessaryDiagramElementsWithoutConnection = this._diagram.getElements(DiagramElement.class)
                .allMatch(e -> this._diagram.getElements(DiagramElement.class)
                        .filter(e1 -> !this.test_connectedDiagramElements.containsKey(new Pair<>(e, e1)))
                        .noneMatch(e2 -> this._diagram.hasConnectByAnyRelation(e, e2)));

        Assertions.assertTrue(areAllNecessaryDiagramElementsWithoutConnection);

        // Check that all needed diagram elements are connected with corresponding type of edge
        for (var connectPair : this.test_connectedDiagramElements.entrySet()) {
            Assertions.assertTrue(this._diagram.hasConnectByRelation(connectPair.getKey().first(), connectPair.getKey().second(), connectPair.getValue()));

            Edge edge = this._diagram.getConnectByRelation(connectPair.getKey().first(), connectPair.getKey().second(), connectPair.getValue());
            Assertions.assertNotNull(edge);
            Assertions.assertEquals(connectPair.getKey().first(), edge.begin());
            Assertions.assertEquals(connectPair.getKey().first(), edge.begin());
        }
    }

    @AfterEach
    protected void endTest_logicErrors() {
        ArrayList<EntityType>           allEntityTypes           = this._diagram.getElements(EntityType.class).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<ValueType>            allValueTypes            = this._diagram.getElements(ValueType.class).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Predicate>            allPredicates            = this._diagram.getElements(Predicate.class).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Role>                 allRoles                 = this._diagram.getElements(Role.class).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<ObjectifiedPredicate> allObjectifiedPredicates = this._diagram.getElements(ObjectifiedPredicate.class).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Constraint>           allConstraints           = this._diagram.getElements(Constraint.class).collect(Collectors.toCollection(ArrayList::new));

        ArrayList<SubtypingRelationEdge>           allSubtypingRelationEdges           = this._diagram.getElements(SubtypingRelationEdge.class).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<RoleRelationEdge>                allRoleRelationEdges                = this._diagram.getElements(RoleRelationEdge.class).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<SubtypingConstraintRelationEdge> allSubtypingConstraintRelationEdges = this._diagram.getElements(SubtypingConstraintRelationEdge.class).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<RoleConstraintRelationEdge>      allRoleConstraintRelationEdges      = this._diagram.getElements(RoleConstraintRelationEdge.class).collect(Collectors.toCollection(ArrayList::new));

        // Set empty set of logic errors for diagram elements with no logic errors
        for (EntityType de : allEntityTypes)                     { test_entityTypesLogicErrors.computeIfAbsent(de, k -> new HashSet<>()); }
        for (ValueType de : allValueTypes)                       { test_valueTypesLogicErrors.computeIfAbsent(de, k -> new HashSet<>()); }
        for (Predicate de : allPredicates)                       { test_predicatesLogicErrors.computeIfAbsent(de, k -> new HashSet<>()); }
        for (Role de : allRoles)                                 { test_rolesLogicErrors.computeIfAbsent(de, k -> new HashSet<>()); }
        for (ObjectifiedPredicate de : allObjectifiedPredicates) { test_objectifiedPredicatesLogicErrors.computeIfAbsent(de, k -> new HashSet<>()); }
        for (Constraint de : allConstraints)                     { test_constraintsLogicErrors.computeIfAbsent(de, k -> new HashSet<>()); }

        for (SubtypingRelationEdge de : allSubtypingRelationEdges)                     { test_subtypingRelationEdgesLogicErrors.computeIfAbsent(de, k -> new HashSet<>()); }
        for (RoleRelationEdge de : allRoleRelationEdges)                               { test_roleRelationEdgesLogicErrors.computeIfAbsent(de, k -> new HashSet<>()); }
        for (SubtypingConstraintRelationEdge de : allSubtypingConstraintRelationEdges) { test_subtypingConstraintRelationEdgesLogicErrors.computeIfAbsent(de, k -> new HashSet<>()); }
        for (RoleConstraintRelationEdge de : allRoleConstraintRelationEdges)           { test_roleConstraintRelationEdgesLogicErrors.computeIfAbsent(de, k -> new HashSet<>()); }

        // Check logic errors for each diagram element of the diagram
        for (EntityType de : allEntityTypes)                     { Assertions.assertEquals(test_entityTypesLogicErrors.get(de),           this._diagram.getLogicErrorsFor(de).collect(Collectors.toCollection(HashSet::new)), "" + de + " :: " + allEntityTypes.indexOf(de)); }
        for (ValueType de : allValueTypes)                       { Assertions.assertEquals(test_valueTypesLogicErrors.get(de),            this._diagram.getLogicErrorsFor(de).collect(Collectors.toCollection(HashSet::new)), "" + de + " :: " + allValueTypes.indexOf(de)); }
        for (Predicate de : allPredicates)                       { Assertions.assertEquals(test_predicatesLogicErrors.get(de),            this._diagram.getLogicErrorsFor(de).collect(Collectors.toCollection(HashSet::new)), "" + de + " :: " + allPredicates.indexOf(de)); }
        for (Role de : allRoles)                                 { Assertions.assertEquals(test_rolesLogicErrors.get(de),                 this._diagram.getLogicErrorsFor(de).collect(Collectors.toCollection(HashSet::new)), "" + de + " :: " + allRoles.indexOf(de)); }
        for (ObjectifiedPredicate de : allObjectifiedPredicates) { Assertions.assertEquals(test_objectifiedPredicatesLogicErrors.get(de), this._diagram.getLogicErrorsFor(de).collect(Collectors.toCollection(HashSet::new)), "" + de + " :: " + allObjectifiedPredicates.indexOf(de)); }

        for (Constraint de : allConstraints)                     { Assertions.assertEquals(test_constraintsLogicErrors.get(de),           this._diagram.getLogicErrorsFor(de).collect(Collectors.toCollection(HashSet::new)), "" + de + " :: " + allConstraints.indexOf(de)); }

        for (SubtypingRelationEdge de : allSubtypingRelationEdges)                     { Assertions.assertEquals(test_subtypingRelationEdgesLogicErrors.get(de),           this._diagram.getLogicErrorsFor(de).collect(Collectors.toCollection(HashSet::new)), "" + de + " :: " + allSubtypingRelationEdges.indexOf(de)); }
        for (RoleRelationEdge de : allRoleRelationEdges)                               { Assertions.assertEquals(test_roleRelationEdgesLogicErrors.get(de),                this._diagram.getLogicErrorsFor(de).collect(Collectors.toCollection(HashSet::new)), "" + de + " :: " + allRoleRelationEdges.indexOf(de)); }
        for (SubtypingConstraintRelationEdge de : allSubtypingConstraintRelationEdges) { Assertions.assertEquals(test_subtypingConstraintRelationEdgesLogicErrors.get(de), this._diagram.getLogicErrorsFor(de).collect(Collectors.toCollection(HashSet::new)), "" + de + " :: " + allSubtypingConstraintRelationEdges.indexOf(de)); }
        for (RoleConstraintRelationEdge de : allRoleConstraintRelationEdges)           { Assertions.assertEquals(test_roleConstraintRelationEdgesLogicErrors.get(de),      this._diagram.getLogicErrorsFor(de).collect(Collectors.toCollection(HashSet::new)), "" + de + " :: " + allRoleConstraintRelationEdges.indexOf(de)); }

        // Check logic errors for the whole diagram
        for (var c : test_entityTypesLogicErrors.values())           { test_expLogicErrors.addAll(c); }
        for (var c : test_valueTypesLogicErrors.values())            { test_expLogicErrors.addAll(c); }
        for (var c : test_predicatesLogicErrors.values())            { test_expLogicErrors.addAll(c); }
        for (var c : test_rolesLogicErrors.values())                 { test_expLogicErrors.addAll(c); }
        for (var c : test_objectifiedPredicatesLogicErrors.values()) { test_expLogicErrors.addAll(c); }
        for (var c : test_constraintsLogicErrors.values())           { test_expLogicErrors.addAll(c); }

        for (var c : test_subtypingRelationEdgesLogicErrors.values())           { test_expLogicErrors.addAll(c); }
        for (var c : test_roleRelationEdgesLogicErrors.values())                { test_expLogicErrors.addAll(c); }
        for (var c : test_subtypingConstraintRelationEdgesLogicErrors.values()) { test_expLogicErrors.addAll(c); }
        for (var c : test_roleConstraintRelationEdgesLogicErrors.values())      { test_expLogicErrors.addAll(c); }

        Assertions.assertEquals(test_expLogicErrors, this._diagram.logicErrors(LogicError.class).collect(Collectors.toCollection(HashSet::new)));
    }

    @AfterEach
    protected void endTest_actionErrors() {
        // Check test action error listener
        Assertions.assertEquals(test_actionErrors, this._actionErrorListener.actionErrors());
    }
}
