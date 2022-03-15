package com.orm2_graph_library.nodes.predicates;

import com.orm2_graph_library.core.Diagram;
import com.orm2_graph_library.core.DiagramElement;
import com.orm2_graph_library.core.Node;
import com.orm2_graph_library.nodes_shapes.RectangleShape;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

// TODO - @modify   :: Should ROLE calculate its geometry position with geometry of its parent?
//      # @question :: How INNER PREDICATE can be inherited from PREDICATE if its geometry must calculate its position by parent and
//                     STANDALONE PREDICATE don't have to?
//      # @idea     :: We can: 1) re-calculate position with parent (current decision); 2) calculate position by parent.

public class Role extends Node {
    // ================== STATIC ==================
    // TODO - @add :: Set what orientation is using for given default width and height?
    static private int _width  = 20;
    static private int _height = 10;

    static public int width()  { return Role._width; }
    static public int height() { return Role._height; }

    static public void setWidth(int width)   { Role._width  = width; }
    static public void setHeight(int height) { Role._height = height; }

    // ================ ATTRIBUTES ================
    private DiagramElement.Orientation _orientation;
    private String    _text; // TODO - @modify :: Change name of role's text.
    private Predicate _ownerPredicate;
    private final int _indexInPredicate;

    // ================ OPERATIONS ================
    // ----------------- creating -----------------
    Role(@NotNull Predicate predicate, int indexInPredicate) {
        this._ownerPredicate   = predicate;
        this._orientation      = predicate.orientation();
        this._indexInPredicate = indexInPredicate;

        this._shape = new RectangleShape();
    }

    @Override
    protected void _initSelf(Diagram owner) {}

    // ----------------- attributes -----------------
    public String text() { return this._text; }
    public void setText(String text) { this._text = text; }

    // TODO - @add :: Calculate left top position depending on self placement in the owner predicate.
    @Override
    public Point borderLeftTop() {
        Point result = this._ownerPredicate.borderLeftTop();
        int shiftX = this.borderWidth()  * (this._ownerPredicate.orientation() == DiagramElement.Orientation.HORIZONTAL ? this._indexInPredicate : 0);
        int shiftY = this.borderHeight() * (this._ownerPredicate.orientation() == DiagramElement.Orientation.VERTICAL   ? this._indexInPredicate : 0);
        result.translate(shiftX, shiftY);

        return new Point(result.x, result.y);
    }

    @Override
    public int borderWidth() { return (this._orientation == DiagramElement.Orientation.HORIZONTAL ? Role.width() : Role.height()); }
    @Override
    public int borderHeight() { return (this._orientation == DiagramElement.Orientation.HORIZONTAL ? Role.height() : Role.width()); }

    // TODO - @modify :: Width and height depending on orientation.
    void _setOrientation(DiagramElement.Orientation orientation) { this._orientation = orientation; }

    void _moveTo(Point leftTop)          { this._leftTop.move(leftTop.x, leftTop.y); }
    void _moveBy(int shiftX, int shiftY) { this._leftTop.translate(shiftX, shiftY); }
}
