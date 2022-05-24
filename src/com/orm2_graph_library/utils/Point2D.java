package com.orm2_graph_library.utils;

import org.jetbrains.annotations.NotNull;

public class Point2D {
    public int x;
    public int y;

    public Point2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point2D(@NotNull Point2D other) {
        this.x = other.x;
        this.y = other.y;
    }

    public int x() { return this.x; }
    public int y() { return this.y; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    public void move(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void translate(int shiftX, int shiftY) {
        this.x += shiftX;
        this.y += shiftY;
    }

    @Override
    public boolean equals(@NotNull Object other) {
        return (other instanceof Point2D otherConverted && (this.x == otherConverted.x && this.y == otherConverted.y));
    }

    @Override
    public int hashCode() {
        return this.x * this.y;
    }

    @Override
    public String toString() {
        return "Point2D(x: " + this.x + ", y: " + this.y + ")";
    }
}
