import com.orm2_graph_library.core.LogicError;
import com.orm2_graph_library.logic_errors.EntityTypeWithNoneRefModeLogicError;
import com.orm2_graph_library.logic_errors.RoleHasNoTextSetLogicError;
import com.orm2_graph_library.nodes.common.EntityType;
import com.orm2_graph_library.nodes.common.ref_modes.NoneRefMode;
import com.orm2_graph_library.nodes.predicates.ObjectifiedPredicate;
import com.orm2_graph_library.nodes.predicates.Predicate;
import com.orm2_graph_library.nodes.predicates.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import utils.Test_globalTest;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Test_nodesModification extends Test_globalTest {
    // ==================== TEST ====================
    // ---------------- ENTITY TYPES ----------------
    // * Name
    @Test
    void entityType_setName() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_entityTypes.get(0).setName("Hello");

        // Check result
        Assertions.assertEquals("Hello", test_entityTypes.get(0).name());
        test_addLogicErrorTo(test_entityTypes.get(0), new EntityTypeWithNoneRefModeLogicError(test_entityTypes.get(0)));
    }

    @Test
    void entityType_undoSetName() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_entityTypes.get(0).setName("Hello");

        this._diagram.undoState();

        // Check result
        Assertions.assertEquals(test_entityTypes.get(0).basicName() + " " + 1, test_entityTypes.get(0).name());
        test_addLogicErrorTo(test_entityTypes.get(0), new EntityTypeWithNoneRefModeLogicError(test_entityTypes.get(0)));
    }

    @Test
    void entityType_setTheSameName() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        for (int i=0; i<5; i++) { test_entityTypes.get(0).setName("Hello"); }

        // Check result
        Assertions.assertEquals("Hello", test_entityTypes.get(0).name());
        test_addLogicErrorTo(test_entityTypes.get(0), new EntityTypeWithNoneRefModeLogicError(test_entityTypes.get(0)));
    }

    @Test
    void entityType_undoSetTheSameName() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        for (int i=0; i<5; i++) { test_entityTypes.get(0).setName("Hello"); }

        this._diagram.undoState();

        // Check result
        Assertions.assertEquals(test_entityTypes.get(0).basicName() + " " + 1, test_entityTypes.get(0).name());
        test_addLogicErrorTo(test_entityTypes.get(0), new EntityTypeWithNoneRefModeLogicError(test_entityTypes.get(0)));
    }

    // * IsPersonal flag
    @Test
    void entityType_setIsPersonal() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_entityTypes.get(0).setIsPersonal(true);

        // Check result
        Assertions.assertTrue(test_entityTypes.get(0).isPersonal());
        test_addLogicErrorTo(test_entityTypes.get(0), new EntityTypeWithNoneRefModeLogicError(test_entityTypes.get(0)));
    }

    @Test
    void entityType_undoSetIsPersonal() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_entityTypes.get(0).setIsPersonal(true);

        this._diagram.undoState();

        // Check result
        Assertions.assertFalse(test_entityTypes.get(0).isPersonal());
        test_addLogicErrorTo(test_entityTypes.get(0), new EntityTypeWithNoneRefModeLogicError(test_entityTypes.get(0)));
    }

    @Test
    void entityType_setTheSameIsPersonal() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        for (int i=0; i<5; i++) { test_entityTypes.get(0).setIsPersonal(true); }

        // Check result
        Assertions.assertTrue(test_entityTypes.get(0).isPersonal());
        test_addLogicErrorTo(test_entityTypes.get(0), new EntityTypeWithNoneRefModeLogicError(test_entityTypes.get(0)));
    }

    @Test
    void entityType_undoSetTheSameIsPersonal() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        for (int i=0; i<5; i++) { test_entityTypes.get(0).setIsPersonal(true); }

        // Check result
        Assertions.assertTrue(test_entityTypes.get(0).isPersonal());
        test_addLogicErrorTo(test_entityTypes.get(0), new EntityTypeWithNoneRefModeLogicError(test_entityTypes.get(0)));
    }

    // * IsIndependent flag
    @Test
    void entityType_setIsIndependent() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_entityTypes.get(0).setIsIndependent(true);

        // Check result
        Assertions.assertTrue(test_entityTypes.get(0).isIndependent());
        test_addLogicErrorTo(test_entityTypes.get(0), new EntityTypeWithNoneRefModeLogicError(test_entityTypes.get(0)));
    }

    @Test
    void entityType_undoSetIsIndependent() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        test_entityTypes.get(0).setIsIndependent(true);

        this._diagram.undoState();

        // Check result
        Assertions.assertFalse(test_entityTypes.get(0).isIndependent());
        test_addLogicErrorTo(test_entityTypes.get(0), new EntityTypeWithNoneRefModeLogicError(test_entityTypes.get(0)));
    }

    @Test
    void entityType_setTheSameIsIndependent() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        for (int i=0; i<5; i++) { test_entityTypes.get(0).setIsIndependent(true); }

        // Check result
        Assertions.assertTrue(test_entityTypes.get(0).isIndependent());
        test_addLogicErrorTo(test_entityTypes.get(0), new EntityTypeWithNoneRefModeLogicError(test_entityTypes.get(0)));
    }

    @Test
    void entityType_undoSetTheSameIsIndependent() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new EntityType()));
        for (int i=0; i<5; i++) { test_entityTypes.get(0).setIsIndependent(true); }

        this._diagram.undoState();

        // Check result
        Assertions.assertFalse(test_entityTypes.get(0).isIndependent());
        test_addLogicErrorTo(test_entityTypes.get(0), new EntityTypeWithNoneRefModeLogicError(test_entityTypes.get(0)));
    }

    // TODO - @test :: Setting reference mode and data type for entity type.

    @Test
    void entityType_setAttributesWhenNotInDiagram() {
        // Prepare data and start testing
        EntityType entityType = new EntityType();

        // Check result
        Assertions.assertThrows(NullPointerException.class, () -> entityType.setName("Hello"));
        Assertions.assertThrows(NullPointerException.class, () -> entityType.setIsPersonal(false));
        Assertions.assertThrows(NullPointerException.class, () -> entityType.setIsIndependent(false));
        Assertions.assertThrows(NullPointerException.class, () -> entityType.setRefMode(new NoneRefMode()));
    }

    // * Complex modification

    // ----------------- PREDICATES -----------------
    // * Roles sequence uniqueness
    @Test
    void predicate_makeRolesSequencesUnique() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));

        ArrayList<int[]> uniqueRolesSequencesIndexes = new ArrayList<>();
        uniqueRolesSequencesIndexes.add(new int[]{0, 2, 3});
        uniqueRolesSequencesIndexes.add(new int[]{1, 2, 3});
        uniqueRolesSequencesIndexes.add(new int[]{0, 2});
        uniqueRolesSequencesIndexes.add(new int[]{1});
        uniqueRolesSequencesIndexes.add(new int[]{0, 1, 2});
        uniqueRolesSequencesIndexes.add(new int[]{0, 1, 2, 3});

        for (int[] indexes : uniqueRolesSequencesIndexes) {
            test_addDiagramElement(test_predicates.get(0).rolesSequence(indexes));
            test_predicates.get(0).makeRolesSequenceUnique(indexes);
        }

        // Check result
        for (int[] indexes : uniqueRolesSequencesIndexes) {
            Assertions.assertTrue(test_predicates.get(0).isRolesSequenceUnique(indexes));
            Assertions.assertTrue(test_predicates.get(0).isRolesSequenceUnique(test_predicates.get(0).rolesSequence(indexes)));
        }

        for (Predicate predicate : test_predicates) {
            for (Role role : predicate.roles().collect(Collectors.toCollection(ArrayList::new))) {
                LogicError logicError = new RoleHasNoTextSetLogicError(role);

                test_addLogicErrorTo(role.ownerPredicate(), logicError);
                test_addLogicErrorTo(role, logicError);
            }
        }
    }

    @Test
    void predicate_undoMakingSomeRolesSequencesUnique() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));

        ArrayList<int[]> uniqueRolesSequencesIndexes = new ArrayList<>();
        uniqueRolesSequencesIndexes.add(new int[]{0, 2, 3});
        uniqueRolesSequencesIndexes.add(new int[]{1, 2, 3});
        uniqueRolesSequencesIndexes.add(new int[]{0, 2});
        uniqueRolesSequencesIndexes.add(new int[]{1});
        uniqueRolesSequencesIndexes.add(new int[]{0, 1, 2});
        uniqueRolesSequencesIndexes.add(new int[]{0, 1, 2, 3});

        for (int[] indexes : uniqueRolesSequencesIndexes) {
            test_addDiagramElement(test_predicates.get(0).rolesSequence(indexes));
            test_predicates.get(0).makeRolesSequenceUnique(indexes);
        }

        for (int i=0; i<3; i++) { this._diagram.undoState(); }

        // Check result
        for (int[] indexes : uniqueRolesSequencesIndexes.subList(0, 2)) {
            Assertions.assertTrue(test_predicates.get(0).isRolesSequenceUnique(indexes));
            Assertions.assertTrue(test_predicates.get(0).isRolesSequenceUnique(test_predicates.get(0).rolesSequence(indexes)));
        }

        for (int[] indexes : uniqueRolesSequencesIndexes.subList(3, 5)) {
            Assertions.assertFalse(test_predicates.get(0).isRolesSequenceUnique(indexes));
            Assertions.assertFalse(test_predicates.get(0).isRolesSequenceUnique(test_predicates.get(0).rolesSequence(indexes)));
        }

        for (Predicate predicate : test_predicates) {
            for (Role role : predicate.roles().collect(Collectors.toCollection(ArrayList::new))) {
                LogicError logicError = new RoleHasNoTextSetLogicError(role);

                test_addLogicErrorTo(role.ownerPredicate(), logicError);
                test_addLogicErrorTo(role, logicError);
            }
        }
    }

    @Test
    void predicate_undoMakingRolesSequencesUnique() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));

        ArrayList<int[]> uniqueRolesSequencesIndexes = new ArrayList<>();
        uniqueRolesSequencesIndexes.add(new int[]{0, 2, 3});
        uniqueRolesSequencesIndexes.add(new int[]{1, 2, 3});
        uniqueRolesSequencesIndexes.add(new int[]{0, 2});
        uniqueRolesSequencesIndexes.add(new int[]{1});
        uniqueRolesSequencesIndexes.add(new int[]{0, 1, 2});
        uniqueRolesSequencesIndexes.add(new int[]{0, 1, 2, 3});

        for (int[] indexes : uniqueRolesSequencesIndexes) {
            test_addDiagramElement(test_predicates.get(0).rolesSequence(indexes));
            test_predicates.get(0).makeRolesSequenceUnique(indexes);
        }

        for (int i=0; i<6; i++) { this._diagram.undoState(); }

        // Check result
        for (int[] indexes : uniqueRolesSequencesIndexes) {
            Assertions.assertFalse(test_predicates.get(0).isRolesSequenceUnique(indexes));
            Assertions.assertFalse(test_predicates.get(0).isRolesSequenceUnique(test_predicates.get(0).rolesSequence(indexes)));
        }

        for (Predicate predicate : test_predicates) {
            for (Role role : predicate.roles().collect(Collectors.toCollection(ArrayList::new))) {
                LogicError logicError = new RoleHasNoTextSetLogicError(role);

                test_addLogicErrorTo(role.ownerPredicate(), logicError);
                test_addLogicErrorTo(role, logicError);
            }
        }
    }

    @Test
    void predicate_redoMakingSomeRolesSequencesUnique() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));

        ArrayList<int[]> uniqueRolesSequencesIndexes = new ArrayList<>();
        uniqueRolesSequencesIndexes.add(new int[]{0, 2, 3});
        uniqueRolesSequencesIndexes.add(new int[]{1, 2, 3});
        uniqueRolesSequencesIndexes.add(new int[]{0, 2});
        uniqueRolesSequencesIndexes.add(new int[]{1});
        uniqueRolesSequencesIndexes.add(new int[]{0, 1, 2});
        uniqueRolesSequencesIndexes.add(new int[]{0, 1, 2, 3});

        for (int[] indexes : uniqueRolesSequencesIndexes) {
            test_addDiagramElement(test_predicates.get(0).rolesSequence(indexes));
            test_predicates.get(0).makeRolesSequenceUnique(indexes);
        }

        for (int i=0; i<3; i++) { this._diagram.undoState(); }
        for (int i=0; i<3; i++) { this._diagram.redoState(); }

        // Check result
        for (int[] indexes : uniqueRolesSequencesIndexes) {
            Assertions.assertTrue(test_predicates.get(0).isRolesSequenceUnique(indexes));
            Assertions.assertTrue(test_predicates.get(0).isRolesSequenceUnique(test_predicates.get(0).rolesSequence(indexes)));
        }

        for (Predicate predicate : test_predicates) {
            for (Role role : predicate.roles().collect(Collectors.toCollection(ArrayList::new))) {
                LogicError logicError = new RoleHasNoTextSetLogicError(role);

                test_addLogicErrorTo(role.ownerPredicate(), logicError);
                test_addLogicErrorTo(role, logicError);
            }
        }
    }

    @Test
    void predicate_redoMakingRolesSequencesUnique() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));

        ArrayList<int[]> uniqueRolesSequencesIndexes = new ArrayList<>();
        uniqueRolesSequencesIndexes.add(new int[]{0, 2, 3});
        uniqueRolesSequencesIndexes.add(new int[]{1, 2, 3});
        uniqueRolesSequencesIndexes.add(new int[]{0, 2});
        uniqueRolesSequencesIndexes.add(new int[]{1});
        uniqueRolesSequencesIndexes.add(new int[]{0, 1, 2});
        uniqueRolesSequencesIndexes.add(new int[]{0, 1, 2, 3});

        for (int[] indexes : uniqueRolesSequencesIndexes) {
            test_addDiagramElement(test_predicates.get(0).rolesSequence(indexes));
            test_predicates.get(0).makeRolesSequenceUnique(indexes);
        }

        for (int i=0; i<6; i++) { this._diagram.undoState(); }
        for (int i=0; i<6; i++) { this._diagram.redoState(); }

        // Check result
        for (int[] indexes : uniqueRolesSequencesIndexes) {
            Assertions.assertTrue(test_predicates.get(0).isRolesSequenceUnique(indexes));
            Assertions.assertTrue(test_predicates.get(0).isRolesSequenceUnique(test_predicates.get(0).rolesSequence(indexes)));
        }

        for (Predicate predicate : test_predicates) {
            for (Role role : predicate.roles().collect(Collectors.toCollection(ArrayList::new))) {
                LogicError logicError = new RoleHasNoTextSetLogicError(role);

                test_addLogicErrorTo(role.ownerPredicate(), logicError);
                test_addLogicErrorTo(role, logicError);
            }
        }
    }

    @Test
    void predicate_setAllRolesText() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));
        test_predicates.get(0).getRole(0).setText("has");
        test_predicates.get(0).getRole(1).setText("do");
        test_predicates.get(0).getRole(2).setText("is");
        test_predicates.get(0).getRole(3).setText("made of");
    }

    @Test
    void predicate_undoSetAllRolesText() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));
        test_predicates.get(0).getRole(0).setText("has");
        test_predicates.get(0).getRole(1).setText("do");
        test_predicates.get(0).getRole(2).setText("is");
        test_predicates.get(0).getRole(3).setText("made of");

        for (int i=0; i<4; i++) { this._diagram.undoState(); }

        // Check result
        for (Predicate predicate : test_predicates) {
            for (Role role : predicate.roles().collect(Collectors.toCollection(ArrayList::new))) {
                LogicError logicError = new RoleHasNoTextSetLogicError(role);

                test_addLogicErrorTo(role.ownerPredicate(), logicError);
                test_addLogicErrorTo(role, logicError);
            }
        }
    }

    @Test
    void predicate_redoSetAllRolesText() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));
        test_predicates.get(0).getRole(0).setText("has");
        test_predicates.get(0).getRole(1).setText("do");
        test_predicates.get(0).getRole(2).setText("is");
        test_predicates.get(0).getRole(3).setText("made of");

        for (int i=0; i<4; i++) { this._diagram.undoState(); }
        for (int i=0; i<4; i++) { this._diagram.redoState(); }
    }

    @Test
    void predicate_setRolesTextAfterUndoSettingAllRolesText() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));
        test_predicates.get(0).getRole(0).setText("has");
        test_predicates.get(0).getRole(1).setText("do");
        test_predicates.get(0).getRole(2).setText("is");
        test_predicates.get(0).getRole(3).setText("made of");

        this._diagram.undoState();
        this._diagram.undoState();

        test_predicates.get(0).getRole(2).setText("builds");
        test_predicates.get(0).getRole(3).setText("makes");
    }

    @Test
    void predicate_setSomeRolesText() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));
        test_predicates.get(0).getRole(0).setText("has");
        test_predicates.get(0).getRole(2).setText("do");

        // Check result
        LogicError logicError = new RoleHasNoTextSetLogicError(test_predicates.get(0).getRole(1));
        test_addLogicErrorTo(test_predicates.get(0).getRole(1).ownerPredicate(), logicError);
        test_addLogicErrorTo(test_predicates.get(0).getRole(1), logicError);

        logicError = new RoleHasNoTextSetLogicError(test_predicates.get(0).getRole(3));
        test_addLogicErrorTo(test_predicates.get(0).getRole(3).ownerPredicate(), logicError);
        test_addLogicErrorTo(test_predicates.get(0).getRole(3), logicError);
    }

    @Test
    void predicate_undoSetSomeRolesText() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));
        test_predicates.get(0).getRole(0).setText("has");
        test_predicates.get(0).getRole(2).setText("is");

        this._diagram.undoState();
        this._diagram.undoState();

        // Check result
        for (Predicate predicate : test_predicates) {
            for (Role role : predicate.roles().collect(Collectors.toCollection(ArrayList::new))) {
                LogicError logicError = new RoleHasNoTextSetLogicError(role);

                test_addLogicErrorTo(role.ownerPredicate(), logicError);
                test_addLogicErrorTo(role, logicError);
            }
        }
    }

    @Test
    void predicate_redoSetSomeRolesText() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));
        test_predicates.get(0).getRole(0).setText("has");
        test_predicates.get(0).getRole(2).setText("do");

        this._diagram.undoState();
        this._diagram.undoState();
        this._diagram.redoState();
        this._diagram.redoState();

        // Check result
        LogicError logicError = new RoleHasNoTextSetLogicError(test_predicates.get(0).getRole(1));
        test_addLogicErrorTo(test_predicates.get(0).getRole(1).ownerPredicate(), logicError);
        test_addLogicErrorTo(test_predicates.get(0).getRole(1), logicError);

        logicError = new RoleHasNoTextSetLogicError(test_predicates.get(0).getRole(3));
        test_addLogicErrorTo(test_predicates.get(0).getRole(3).ownerPredicate(), logicError);
        test_addLogicErrorTo(test_predicates.get(0).getRole(3), logicError);
    }

    @Test
    void predicate_setRolesTextAfterUndoSettingSomeRolesText() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));
        test_predicates.get(0).getRole(0).setText("has");
        test_predicates.get(0).getRole(2).setText("is");

        this._diagram.undoState();
        this._diagram.undoState();

        test_predicates.get(0).getRole(1).setText("has");
        test_predicates.get(0).getRole(3).setText("is");

        // Check result
        LogicError logicError = new RoleHasNoTextSetLogicError(test_predicates.get(0).getRole(0));
        test_addLogicErrorTo(test_predicates.get(0).getRole(0).ownerPredicate(), logicError);
        test_addLogicErrorTo(test_predicates.get(0).getRole(0), logicError);

        logicError = new RoleHasNoTextSetLogicError(test_predicates.get(0).getRole(2));
        test_addLogicErrorTo(test_predicates.get(0).getRole(2).ownerPredicate(), logicError);
        test_addLogicErrorTo(test_predicates.get(0).getRole(2), logicError);
    }

    /*
    @Test
    void predicate_makeRolesSequencesUnique() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));

        ArrayList<int[]> uniqueRolesSequencesIndexes = new ArrayList<>();
        uniqueRolesSequencesIndexes.add(new int[]{0, 2, 3});
        uniqueRolesSequencesIndexes.add(new int[]{1, 2, 3});
        uniqueRolesSequencesIndexes.add(new int[]{0, 2});
        uniqueRolesSequencesIndexes.add(new int[]{1});
        uniqueRolesSequencesIndexes.add(new int[]{0, 1, 2});
        uniqueRolesSequencesIndexes.add(new int[]{0, 1, 2, 3});

        for (int[] indexes : uniqueRolesSequencesIndexes) {
            test_addDiagramElement(test_predicates.get(0).rolesSequence(indexes));
            test_predicates.get(0).makeRolesSequenceUnique(indexes);
        }

        // Check result
        for (int[] indexes : uniqueRolesSequencesIndexes) {
            Assertions.assertTrue(test_predicates.get(0).isRolesSequenceUnique(indexes));
            Assertions.assertTrue(test_predicates.get(0).isRolesSequenceUnique(test_predicates.get(0).rolesSequence(indexes)));
        }
    }

    @Test
    void predicate_undoMakingSomeRolesSequencesUnique() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));

        ArrayList<int[]> uniqueRolesSequencesIndexes = new ArrayList<>();
        uniqueRolesSequencesIndexes.add(new int[]{0, 2, 3});
        uniqueRolesSequencesIndexes.add(new int[]{1, 2, 3});
        uniqueRolesSequencesIndexes.add(new int[]{0, 2});
        uniqueRolesSequencesIndexes.add(new int[]{1});
        uniqueRolesSequencesIndexes.add(new int[]{0, 1, 2});
        uniqueRolesSequencesIndexes.add(new int[]{0, 1, 2, 3});

        for (int[] indexes : uniqueRolesSequencesIndexes) {
            test_addDiagramElement(test_predicates.get(0).rolesSequence(indexes));
            test_predicates.get(0).makeRolesSequenceUnique(indexes);
        }

        for (int i=0; i<3; i++) { this._diagram.undoState(); }

        // Check result
        for (int[] indexes : uniqueRolesSequencesIndexes.subList(0, 2)) {
            Assertions.assertTrue(test_predicates.get(0).isRolesSequenceUnique(indexes));
            Assertions.assertTrue(test_predicates.get(0).isRolesSequenceUnique(test_predicates.get(0).rolesSequence(indexes)));
        }

        for (int[] indexes : uniqueRolesSequencesIndexes.subList(3, 5)) {
            Assertions.assertFalse(test_predicates.get(0).isRolesSequenceUnique(indexes));
            Assertions.assertFalse(test_predicates.get(0).isRolesSequenceUnique(test_predicates.get(0).rolesSequence(indexes)));
        }
    }

    @Test
    void predicate_undoMakingRolesSequencesUnique() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));

        ArrayList<int[]> uniqueRolesSequencesIndexes = new ArrayList<>();
        uniqueRolesSequencesIndexes.add(new int[]{0, 2, 3});
        uniqueRolesSequencesIndexes.add(new int[]{1, 2, 3});
        uniqueRolesSequencesIndexes.add(new int[]{0, 2});
        uniqueRolesSequencesIndexes.add(new int[]{1});
        uniqueRolesSequencesIndexes.add(new int[]{0, 1, 2});
        uniqueRolesSequencesIndexes.add(new int[]{0, 1, 2, 3});

        for (int[] indexes : uniqueRolesSequencesIndexes) {
            test_addDiagramElement(test_predicates.get(0).rolesSequence(indexes));
            test_predicates.get(0).makeRolesSequenceUnique(indexes);
        }

        for (int i=0; i<6; i++) { this._diagram.undoState(); }

        // Check result
        for (int[] indexes : uniqueRolesSequencesIndexes) {
            Assertions.assertFalse(test_predicates.get(0).isRolesSequenceUnique(indexes));
            Assertions.assertFalse(test_predicates.get(0).isRolesSequenceUnique(test_predicates.get(0).rolesSequence(indexes)));
        }
    }

    @Test
    void predicate_redoMakingSomeRolesSequencesUnique() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));

        ArrayList<int[]> uniqueRolesSequencesIndexes = new ArrayList<>();
        uniqueRolesSequencesIndexes.add(new int[]{0, 2, 3});
        uniqueRolesSequencesIndexes.add(new int[]{1, 2, 3});
        uniqueRolesSequencesIndexes.add(new int[]{0, 2});
        uniqueRolesSequencesIndexes.add(new int[]{1});
        uniqueRolesSequencesIndexes.add(new int[]{0, 1, 2});
        uniqueRolesSequencesIndexes.add(new int[]{0, 1, 2, 3});

        for (int[] indexes : uniqueRolesSequencesIndexes) {
            test_addDiagramElement(test_predicates.get(0).rolesSequence(indexes));
            test_predicates.get(0).makeRolesSequenceUnique(indexes);
        }

        for (int i=0; i<3; i++) { this._diagram.undoState(); }
        for (int i=0; i<3; i++) { this._diagram.redoState(); }

        // Check result
        for (int[] indexes : uniqueRolesSequencesIndexes) {
            Assertions.assertTrue(test_predicates.get(0).isRolesSequenceUnique(indexes));
            Assertions.assertTrue(test_predicates.get(0).isRolesSequenceUnique(test_predicates.get(0).rolesSequence(indexes)));
        }
    }

    @Test
    void predicate_redoMakingRolesSequencesUnique() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));

        ArrayList<int[]> uniqueRolesSequencesIndexes = new ArrayList<>();
        uniqueRolesSequencesIndexes.add(new int[]{0, 2, 3});
        uniqueRolesSequencesIndexes.add(new int[]{1, 2, 3});
        uniqueRolesSequencesIndexes.add(new int[]{0, 2});
        uniqueRolesSequencesIndexes.add(new int[]{1});
        uniqueRolesSequencesIndexes.add(new int[]{0, 1, 2});
        uniqueRolesSequencesIndexes.add(new int[]{0, 1, 2, 3});

        for (int[] indexes : uniqueRolesSequencesIndexes) {
            test_addDiagramElement(test_predicates.get(0).rolesSequence(indexes));
            test_predicates.get(0).makeRolesSequenceUnique(indexes);
        }

        for (int i=0; i<6; i++) { this._diagram.undoState(); }
        for (int i=0; i<6; i++) { this._diagram.redoState(); }

        // Check result
        for (int[] indexes : uniqueRolesSequencesIndexes) {
            Assertions.assertTrue(test_predicates.get(0).isRolesSequenceUnique(indexes));
            Assertions.assertTrue(test_predicates.get(0).isRolesSequenceUnique(test_predicates.get(0).rolesSequence(indexes)));
        }
    }
    */

    // * Predicate objectifying
    @Test
    void predicate_objectify() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(test_predicates.get(0))));

        // Check result
        Assertions.assertTrue(test_predicates.get(0).hasOwnerObjectifiedPredicate());
        Assertions.assertEquals(test_objectifiedPredicates.get(0), test_predicates.get(0).ownerObjectifiedPredicate());

        for (Predicate predicate : test_predicates) {
            for (Role role : predicate.roles().collect(Collectors.toCollection(ArrayList::new))) {
                LogicError logicError = new RoleHasNoTextSetLogicError(role);

                test_addLogicErrorTo(role.ownerPredicate(), logicError);
                test_addLogicErrorTo(role, logicError);
            }
        }
    }

    @Test
    void predicate_undoObjectifying() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));
        this._diagram.addNode(new ObjectifiedPredicate(test_predicates.get(0)));

        this._diagram.undoState();

        // Check result
        Assertions.assertFalse(test_predicates.get(0).hasOwnerObjectifiedPredicate());

        for (Predicate predicate : test_predicates) {
            for (Role role : predicate.roles().collect(Collectors.toCollection(ArrayList::new))) {
                LogicError logicError = new RoleHasNoTextSetLogicError(role);

                test_addLogicErrorTo(role.ownerPredicate(), logicError);
                test_addLogicErrorTo(role, logicError);
            }
        }
    }

    @Test
    void predicate_redoObjectifying() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(test_predicates.get(0))));

        this._diagram.undoState();
        this._diagram.redoState();

        // Check result
        Assertions.assertTrue(test_predicates.get(0).hasOwnerObjectifiedPredicate());
        Assertions.assertEquals(test_objectifiedPredicates.get(0), test_predicates.get(0).ownerObjectifiedPredicate());

        for (Predicate predicate : test_predicates) {
            for (Role role : predicate.roles().collect(Collectors.toCollection(ArrayList::new))) {
                LogicError logicError = new RoleHasNoTextSetLogicError(role);

                test_addLogicErrorTo(role.ownerPredicate(), logicError);
                test_addLogicErrorTo(role, logicError);
            }
        }
    }

    @Test
    void predicate_objectifyTwice() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(test_predicates.get(0))));

        // Check result
        Assertions.assertThrows(RuntimeException.class, () -> this._diagram.addNode(new ObjectifiedPredicate(test_predicates.get(0))));

        for (Predicate predicate : test_predicates) {
            for (Role role : predicate.roles().collect(Collectors.toCollection(ArrayList::new))) {
                LogicError logicError = new RoleHasNoTextSetLogicError(role);

                test_addLogicErrorTo(role.ownerPredicate(), logicError);
                test_addLogicErrorTo(role, logicError);
            }
        }
    }

    @Test
    void predicate_unobjectifyByRemovingObjectifiedPredicate() {
        // Prepare data and start testing
        Predicate predicate = this._diagram.addNode(new Predicate(4));
        ObjectifiedPredicate objectifiedPredicate = this._diagram.addNode(new ObjectifiedPredicate(predicate));

        this._diagram.removeNode(objectifiedPredicate);

        // Check result
        Assertions.assertFalse(predicate.hasOwnerObjectifiedPredicate());
    }

    @Test
    void predicate_undoUnobjectifyingByRemovingObjectifiedPredicate() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(test_predicates.get(0))));

        this._diagram.removeNode(test_objectifiedPredicates.get(0));

        this._diagram.undoState();

        // Check result
        Assertions.assertTrue(test_predicates.get(0).hasOwnerObjectifiedPredicate());
        Assertions.assertEquals(test_objectifiedPredicates.get(0), test_predicates.get(0).ownerObjectifiedPredicate());
        Assertions.assertEquals(test_predicates.get(0), test_objectifiedPredicates.get(0).innerPredicate());

        for (Predicate predicate : test_predicates) {
            for (Role role : predicate.roles().collect(Collectors.toCollection(ArrayList::new))) {
                LogicError logicError = new RoleHasNoTextSetLogicError(role);

                test_addLogicErrorTo(role.ownerPredicate(), logicError);
                test_addLogicErrorTo(role, logicError);
            }
        }
    }

    @Test
    void predicate_redoUnobjectifyingByRemovingObjectifiedPredicate() {
        // Prepare data and start testing
        Predicate predicate = this._diagram.addNode(new Predicate(4));
        ObjectifiedPredicate objectifiedPredicate = this._diagram.addNode(new ObjectifiedPredicate(predicate));

        this._diagram.removeNode(objectifiedPredicate);

        this._diagram.undoState();
        this._diagram.redoState();

        // Check result
        Assertions.assertFalse(predicate.hasOwnerObjectifiedPredicate());
    }

    @Test
    void predicate_unobjectifyByObjectifiedPredicateInterface() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));
        ObjectifiedPredicate objectifiedPredicate = this._diagram.addNode(new ObjectifiedPredicate(test_predicates.get(0)));

        objectifiedPredicate.becomeUnobjectified();

        // Check result
        Assertions.assertFalse(test_predicates.get(0).hasOwnerObjectifiedPredicate());
        Assertions.assertTrue(this._diagram.hasOnlyElements(Predicate.class));

        for (Predicate predicate : test_predicates) {
            for (Role role : predicate.roles().collect(Collectors.toCollection(ArrayList::new))) {
                LogicError logicError = new RoleHasNoTextSetLogicError(role);

                test_addLogicErrorTo(role.ownerPredicate(), logicError);
                test_addLogicErrorTo(role, logicError);
            }
        }
    }

    @Test
    void predicate_undoUnobjectifyingByObjectifiedPredicateInterface() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(test_predicates.get(0))));

        test_objectifiedPredicates.get(0).becomeUnobjectified();

        this._diagram.undoState();

        // Check result
        Assertions.assertTrue(test_predicates.get(0).hasOwnerObjectifiedPredicate());
        Assertions.assertEquals(test_objectifiedPredicates.get(0), test_predicates.get(0).ownerObjectifiedPredicate());
        Assertions.assertEquals(test_predicates.get(0), test_objectifiedPredicates.get(0).innerPredicate());

        for (Predicate predicate : test_predicates) {
            for (Role role : predicate.roles().collect(Collectors.toCollection(ArrayList::new))) {
                LogicError logicError = new RoleHasNoTextSetLogicError(role);

                test_addLogicErrorTo(role.ownerPredicate(), logicError);
                test_addLogicErrorTo(role, logicError);
            }
        }
    }

    @Test
    void predicate_redoUnobjectifyingByObjectifiedPredicateInterface() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new Predicate(4)));
        ObjectifiedPredicate objectifiedPredicate = this._diagram.addNode(new ObjectifiedPredicate(test_predicates.get(0)));

        objectifiedPredicate.becomeUnobjectified();

        this._diagram.undoState();
        this._diagram.redoState();

        // Check result
        Assertions.assertFalse(test_predicates.get(0).hasOwnerObjectifiedPredicate());
        Assertions.assertTrue(this._diagram.hasOnlyElements(Predicate.class));

        for (Predicate predicate : test_predicates) {
            for (Role role : predicate.roles().collect(Collectors.toCollection(ArrayList::new))) {
                LogicError logicError = new RoleHasNoTextSetLogicError(role);

                test_addLogicErrorTo(role.ownerPredicate(), logicError);
                test_addLogicErrorTo(role, logicError);
            }
        }
    }

    @Test
    void predicate_setAttributesWhenNotInDiagram() {
        // Prepare data and start testing
        Predicate predicate = new Predicate(4);

        // Check result
        Assertions.assertThrows(NullPointerException.class, () -> predicate.makeRolesSequenceUnique(0, 2, 3));
        Assertions.assertThrows(NullPointerException.class, () -> predicate.makeRolesSequenceUnique(1, 2, 3));
        Assertions.assertThrows(NullPointerException.class, () -> predicate.makeRolesSequenceUnique(0, 2));
        Assertions.assertThrows(NullPointerException.class, () -> predicate.makeRolesSequenceUnique(1));
        Assertions.assertThrows(NullPointerException.class, () -> predicate.makeRolesSequenceUnique(0, 1, 2));
        Assertions.assertThrows(NullPointerException.class, () -> predicate.makeRolesSequenceUnique(0, 1, 2, 3));
    }


    // ----------- OBJECTIFIED PREDICATES -----------
    // * Name
    @Test
    void objectifiedPredicate_setName() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(1))));
        test_objectifiedPredicates.get(0).setName("Hello");

        // Check result
        Assertions.assertEquals("Hello", test_objectifiedPredicates.get(0).name());

        for (Predicate predicate : test_predicates) {
            for (Role role : predicate.roles().collect(Collectors.toCollection(ArrayList::new))) {
                LogicError logicError = new RoleHasNoTextSetLogicError(role);

                test_addLogicErrorTo(role.ownerPredicate(), logicError);
                test_addLogicErrorTo(role, logicError);
            }
        }
    }

    @Test
    void objectifiedPredicate_undoSetName() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(1))));
        test_objectifiedPredicates.get(0).setName("Hello");

        this._diagram.undoState();

        // Check result
        Assertions.assertEquals(test_objectifiedPredicates.get(0).basicName() + " " + 1, test_objectifiedPredicates.get(0).name());

        for (Predicate predicate : test_predicates) {
            for (Role role : predicate.roles().collect(Collectors.toCollection(ArrayList::new))) {
                LogicError logicError = new RoleHasNoTextSetLogicError(role);

                test_addLogicErrorTo(role.ownerPredicate(), logicError);
                test_addLogicErrorTo(role, logicError);
            }
        }
    }

    @Test
    void objectifiedPredicate_setTheSameName() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(1))));
        for (int i=0; i<5; i++) { test_objectifiedPredicates.get(0).setName("Hello"); }

        // Check result
        Assertions.assertEquals("Hello", test_objectifiedPredicates.get(0).name());

        for (Predicate predicate : test_predicates) {
            for (Role role : predicate.roles().collect(Collectors.toCollection(ArrayList::new))) {
                LogicError logicError = new RoleHasNoTextSetLogicError(role);

                test_addLogicErrorTo(role.ownerPredicate(), logicError);
                test_addLogicErrorTo(role, logicError);
            }
        }
    }

    @Test
    void objectifiedPredicate_undoSetTheSameName() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(1))));
        for (int i=0; i<5; i++) { test_objectifiedPredicates.get(0).setName("Hello"); }

        this._diagram.undoState();

        // Check result
        Assertions.assertEquals(test_objectifiedPredicates.get(0).basicName() + " " + 1, test_objectifiedPredicates.get(0).name());

        for (Predicate predicate : test_predicates) {
            for (Role role : predicate.roles().collect(Collectors.toCollection(ArrayList::new))) {
                LogicError logicError = new RoleHasNoTextSetLogicError(role);

                test_addLogicErrorTo(role.ownerPredicate(), logicError);
                test_addLogicErrorTo(role, logicError);
            }
        }
    }

    // * IsIndependent flag
    @Test
    void ObjectifiedPredicate_setIsIndependent() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(1))));
        test_objectifiedPredicates.get(0).setIsIndependent(true);

        // Check result
        Assertions.assertTrue(test_objectifiedPredicates.get(0).isIndependent());

        for (Predicate predicate : test_predicates) {
            for (Role role : predicate.roles().collect(Collectors.toCollection(ArrayList::new))) {
                LogicError logicError = new RoleHasNoTextSetLogicError(role);

                test_addLogicErrorTo(role.ownerPredicate(), logicError);
                test_addLogicErrorTo(role, logicError);
            }
        }
    }

    @Test
    void ObjectifiedPredicate_undoSetIsIndependent() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(1))));
        test_objectifiedPredicates.get(0).setIsIndependent(true);

        this._diagram.undoState();

        // Check result
        Assertions.assertFalse(test_objectifiedPredicates.get(0).isIndependent());

        for (Predicate predicate : test_predicates) {
            for (Role role : predicate.roles().collect(Collectors.toCollection(ArrayList::new))) {
                LogicError logicError = new RoleHasNoTextSetLogicError(role);

                test_addLogicErrorTo(role.ownerPredicate(), logicError);
                test_addLogicErrorTo(role, logicError);
            }
        }
    }

    @Test
    void ObjectifiedPredicate_setTheSameIsIndependent() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(1))));
        for (int i=0; i<5; i++) { test_objectifiedPredicates.get(0).setIsIndependent(true); }

        // Check result
        Assertions.assertTrue(test_objectifiedPredicates.get(0).isIndependent());

        for (Predicate predicate : test_predicates) {
            for (Role role : predicate.roles().collect(Collectors.toCollection(ArrayList::new))) {
                LogicError logicError = new RoleHasNoTextSetLogicError(role);

                test_addLogicErrorTo(role.ownerPredicate(), logicError);
                test_addLogicErrorTo(role, logicError);
            }
        }
    }

    @Test
    void ObjectifiedPredicate_undoSetTheSameIsIndependent() {
        // Prepare data and start testing
        test_addDiagramElement(this._diagram.addNode(new ObjectifiedPredicate(new Predicate(1))));
        for (int i=0; i<5; i++) { test_objectifiedPredicates.get(0).setIsIndependent(true); }

        this._diagram.undoState();

        // Check result
        Assertions.assertFalse(test_objectifiedPredicates.get(0).isIndependent());

        for (Predicate predicate : test_predicates) {
            for (Role role : predicate.roles().collect(Collectors.toCollection(ArrayList::new))) {
                LogicError logicError = new RoleHasNoTextSetLogicError(role);

                test_addLogicErrorTo(role.ownerPredicate(), logicError);
                test_addLogicErrorTo(role, logicError);
            }
        }
    }
}
