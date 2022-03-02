package com.orm2_graph_library.core;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public abstract class Geometry {
    // ================= SUBTYPES =================
    public class RectangleArea {
        // ---------------- ATTRIBUTES ----------------
        private Point _leftTop;
        private int   _width   = -1;
        private int   _height  = -1;

        // ---------------- OPERATIONS ----------------
        // ~~~~ creating ~~~~
        public RectangleArea(@NotNull Point leftTop, int width, int height) {
            this._leftTop = (Point)leftTop.clone();
            this._width   = width;
            this._height  = height;
        }

        // ~~~~ contract ~~~~
        public int width()  { return this._width; }
        public int height() { return this._height; }

        public Point leftTop()     { return (Point)this._leftTop.clone(); }
        public Point leftBottom()  { return new Point(this._leftTop.x, this._leftTop.y + this._height); }
        public Point rightTop()    { return new Point(this._leftTop.x + this._width, this._leftTop.y + this._height); }
        public Point rightBottom() { return new Point(this._leftTop.x + this._width, this._leftTop.y + this._height); }
    }

    // ================ ATTRIBUTES ================
    protected RectangleArea _area = new RectangleArea(new Point(0, 0), -1, -1);

    // ================ OPERATIONS ================
    // ---------------- attributes ----------------
    public RectangleArea borderArea() { return this._area; }

    // ----------------- contract -----------------
    void moveTo(int x, int y) { this._area._leftTop.move(x, y); }
    void moveBy(int shiftX, int shiftY) { this._area._leftTop.translate(shiftX, shiftY); }
}
