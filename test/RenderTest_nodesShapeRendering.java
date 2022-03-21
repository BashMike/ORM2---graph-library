import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.DiagramElement;
import com.orm2_graph_library.core.Node;
import com.orm2_graph_library.nodes.common.EntityType;
import com.orm2_graph_library.nodes.constraints.Constraint;
import com.orm2_graph_library.nodes.constraints.SubsetConstraint;
import com.orm2_graph_library.nodes.predicates.ObjectifiedPredicate;
import com.orm2_graph_library.nodes.predicates.Role;
import com.orm2_graph_library.nodes.predicates.StandalonePredicate;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class RenderTest_nodesShapeRendering extends JFrame {
    public RenderTest_nodesShapeRendering() {
        // Set JFrame settings
        this.setTitle("Nodes...");
        this.setSize(new Dimension(640, 480));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setBackground(Color.BLACK);
        this.getContentPane().setBackground(Color.BLACK);

        // Render nodes
        ArrayList<DiagramElement> diagramElementsToRender = new ArrayList<>();

        Diagram diagram = new Diagram();
        StandalonePredicate node0 = diagram.addNode(new StandalonePredicate(4));

        node0.moveTo(new Point(100, 100));
        node0.setOrientation(DiagramElement.Orientation.VERTICAL);

        StandalonePredicate node1 = diagram.addNode(new StandalonePredicate(4));

        node1.moveTo(new Point(250, 250));

        diagramElementsToRender.add(node0);
        diagramElementsToRender.addAll(node0.roles());
        diagramElementsToRender.add(node1);
        diagramElementsToRender.addAll(node1.roles());

        EntityType node2 = diagram.addNode(new EntityType());
        node2.moveTo(new Point(150, 150));
        diagramElementsToRender.add(node2);

        EntityType node21 = diagram.addNode(new EntityType());
        node21.moveTo(new Point(350, 150));
        diagramElementsToRender.add(node21);

        Constraint node3 = diagram.addNode(new SubsetConstraint());
        node3.moveTo(new Point(30, 30));
        diagramElementsToRender.add(node3);

        ObjectifiedPredicate node4 = diagram.addNode(new ObjectifiedPredicate(10));
        node4.moveTo(new Point(100, 30));
        node4.innerPredicate().setOrientation(DiagramElement.Orientation.VERTICAL);
        diagramElementsToRender.add(node4);

        diagramElementsToRender.add(diagram.connectBySubtypingRelation(node2.upAnchorPoint(), node21.downAnchorPoint()));

        node21.moveTo(new Point(450, 250));

        diagramElementsToRender.add(node4.innerPredicate());
        for (Role role : node4.innerPredicate().roles()) { diagramElementsToRender.add(role); }

        this.getContentPane().add(new DiagramElementsPanel(diagramElementsToRender, DiagramElementsPanel.RenderMode.WIREFRAME));
    }

    static public void main(String[] args) {
        SwingUtilities.invokeLater(RenderTest_nodesShapeRendering::new);
    }

    static class DiagramElementsPanel extends JPanel {
        public enum RenderMode { NORMAL, WIREFRAME }

        private final ArrayList<DiagramElement> _diagramElements;
        private final RenderMode _renderMode;

        public DiagramElementsPanel(ArrayList<DiagramElement> diagramElements, RenderMode renderMode) {
            this._diagramElements = diagramElements;
            this._renderMode      = renderMode;
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g;

            for (DiagramElement de : this._diagramElements) {
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
                        if (de instanceof Node) {
                            g2.setColor(Color.BLUE);
                            g2.fill(diagramElementShape);
                        }
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
