import com.orm2_graph_library.anchor_points.AnchorPosition;
import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.DiagramElement;
import com.orm2_graph_library.core.Node;
import com.orm2_graph_library.nodes.common.EntityType;
import com.orm2_graph_library.nodes.common.ObjectType;
import com.orm2_graph_library.nodes.constraints.Constraint;
import com.orm2_graph_library.nodes.constraints.SubsetConstraint;
import com.orm2_graph_library.nodes.predicates.ObjectifiedPredicate;
import com.orm2_graph_library.nodes.predicates.Predicate;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class RenderTest_nodesShapeRendering extends JFrame {
    // ================== STATIC ==================
    static Dimension _objectTypeSize                           = new Dimension(100, 50);

    static int       _constraintSize                           = 35;

    static Dimension _objectifiedPredicateGapsDistances        = new Dimension(20, 20);
    static int       _objectifiedPredicateBorderRoundingDegree = 15;

    static Dimension _roleSize                                 = new Dimension(20, 10);

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    static public void main(String[] args) {
        SwingUtilities.invokeLater(RenderTest_nodesShapeRendering::new);
    }

    public RenderTest_nodesShapeRendering() {
        // Set JFrame settings
        this.setTitle("Nodes...");
        this.setSize(new Dimension(640, 480));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setBackground(Color.BLACK);
        this.getContentPane().setBackground(Color.BLACK);

        // Render nodes
        Diagram diagram = this._createDiagram();
        this._initDiagramElementsSizes(diagram);

        this.getContentPane().add(new DiagramElementsPanel(diagram, DiagramElementsPanel.RenderMode.WIREFRAME));
    }

    private Diagram _createDiagram() {
        Diagram diagram = new Diagram();

        // Create nodes
        Predicate sp0 = diagram.addNode(new Predicate(4));
        Predicate sp1 = diagram.addNode(new Predicate(3));
        ObjectifiedPredicate op0 = diagram.addNode(new ObjectifiedPredicate(sp1));

        EntityType e0 = diagram.addNode(new EntityType());
        EntityType e1 = diagram.addNode(new EntityType());

        Constraint c0 = diagram.addNode(new SubsetConstraint());

        // Modify nodes
        sp0.moveTo(new Point(200, 200));
        sp0.setOrientation(DiagramElement.Orientation.VERTICAL);
        sp1.moveTo(new Point(20, 20));

        e0.moveTo(new Point(350, 150));
        e1.moveTo(new Point(450, 250));

        c0.moveTo(new Point(30, 30));

        op0.moveTo(new Point(100, 30));
        op0.innerPredicate().setOrientation(DiagramElement.Orientation.VERTICAL);

        // Connect nodes
        diagram.connectBySubtypingRelation(e0.centerAnchorPoint(), e1.centerAnchorPoint());
        diagram.connectByRoleConstraintRelation(sp0.rolesSequence(0, 3), c0.centerAnchorPoint());
        diagram.connectByRoleRelation(sp0.getRole(2).anchorPoint(AnchorPosition.LEFT), op0.rightAnchorPoint());
        diagram.connectByRoleRelation(sp1.getRole(2).anchorPoint(AnchorPosition.RIGHT), op0.leftAnchorPoint());

        // Modify diagram elements
        c0.moveBy(300, 100);
        e1.moveTo(new Point(450, 250));

        return diagram;
    }

    private void _initDiagramElementsSizes(Diagram diagram) {
        for (DiagramElement de : diagram.getElements(DiagramElement.class).collect(Collectors.toCollection(ArrayList::new))) {
            if (de instanceof ObjectType) {
                ((ObjectType)de).setBorderSize(_objectTypeSize.width, _objectTypeSize.height);
            }
            else if (de instanceof Constraint) {
                ((Constraint)de).setBorderSize(_constraintSize);
            }
            else if (de instanceof Predicate) {
                ((Predicate)de).setRolesBorderSize(_roleSize.width, _roleSize.height);
            }
            else if (de instanceof ObjectifiedPredicate) {
                ((ObjectifiedPredicate)de).setGapsDistances(_objectifiedPredicateGapsDistances.width, _objectifiedPredicateGapsDistances.height);
                ((ObjectifiedPredicate)de).setBorderRoundingDegree(_objectifiedPredicateBorderRoundingDegree);

                ((ObjectifiedPredicate)de).innerPredicate().setRolesBorderSize(_roleSize.width, _roleSize.height);
            }
        }
    }

    static class DiagramElementsPanel extends JPanel {
        public enum RenderMode { NORMAL, WIREFRAME }

        private final Diagram _diagram;
        private final RenderMode _renderMode;

        public DiagramElementsPanel(Diagram diagram, RenderMode renderMode) {
            this._diagram    = diagram;
            this._renderMode = renderMode;
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g;

            ArrayList<DiagramElement> initialDiagramElements = this._diagram.getElements(DiagramElement.class).collect(Collectors.toCollection(ArrayList::new));
            ArrayList<DiagramElement> diagramElementsToRender = new ArrayList<>(initialDiagramElements);

            for (DiagramElement de : initialDiagramElements) {
                if (de instanceof Predicate) {
                    for (int i=0; i<((Predicate)de).arity(); i++) {
                        diagramElementsToRender.add(((Predicate)de).getRole(i));
                    }
                }
                else if (de instanceof ObjectifiedPredicate) {
                    for (int i=0; i<((ObjectifiedPredicate)de).innerPredicate().arity(); i++) {
                        diagramElementsToRender.add(((ObjectifiedPredicate)de).innerPredicate().getRole(i));
                    }
                }
            }

            for (DiagramElement de : diagramElementsToRender) {
                ArrayList<Point> diagramElementPoints = de.geometryApproximation().points();

                if (de instanceof Node) {
                    int[] pointsX = new int[diagramElementPoints.size()];
                    int[] pointsY = new int[diagramElementPoints.size()];

                    for (int i = 0; i < pointsX.length; i++) { pointsX[i] = diagramElementPoints.get(i).x; }
                    for (int i = 0; i < pointsY.length; i++) { pointsY[i] = diagramElementPoints.get(i).y; }

                    Shape diagramElementShape = new Polygon(pointsX, pointsY, pointsX.length);

                    if (this._renderMode == RenderMode.NORMAL) {
                        g2.setColor(Color.WHITE);
                        g2.draw(diagramElementShape);
                    } else if (this._renderMode == RenderMode.WIREFRAME) {
                        g2.setColor(Color.BLUE);
                        g2.fill(diagramElementShape);

                        g2.setColor(Color.MAGENTA);
                        g2.draw(diagramElementShape);

                        for (Point p : diagramElementPoints) {
                            g2.setColor(Color.GREEN);
                            g2.drawRect(p.x, p.y, 0, 0);
                        }
                    }
                }
                else if (diagramElementPoints.size() > 1) {
                    if (this._renderMode == RenderMode.NORMAL)         { g2.setColor(Color.WHITE); }
                    else if (this._renderMode == RenderMode.WIREFRAME) { g2.setColor(Color.GREEN); }

                    for (int i=0; i<diagramElementPoints.size()-1; i++) {
                        g2.drawLine(diagramElementPoints.get(i).x, diagramElementPoints.get(i).y, diagramElementPoints.get(i+1).x, diagramElementPoints.get(i+1).y);
                    }
                }
            }
        }
    }
}
