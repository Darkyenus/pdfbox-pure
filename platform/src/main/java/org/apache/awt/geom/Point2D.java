package org.apache.awt.geom;

public final class Point2D {
    public float x, y;

    public Point2D() {}

    public Point2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setLocation(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setLocation(Point2D point) {
        this.x = point.x;
        this.y = point.y;
    }
}
