package org.apache.awt.geom;

import org.apache.awt.Rectangle;

public class Rectangle2D {
    public float x;
    public float y;
    public float width;
    public float height;

    public Rectangle2D(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public boolean isEmpty() {
        return (width <= 0.0f) || (height <= 0.0f);
    }

    public void setRect(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }

    public void setRect(Rectangle2D r) {
        this.x = r.getX();
        this.y = r.getY();
        this.width = r.getWidth();
        this.height = r.getHeight();
    }

    public Rectangle getBounds() {
        int x = (int) Math.floor(getMinX());
        int x2 = (int) Math.ceil(getMaxX());
        int y = (int) Math.floor(getMinY());
        int y2 = (int) Math.ceil(getMaxY());
        return new Rectangle(x, y, x2 - x, y2 - y);
    }


    public float getMaxX() {
        return x + Math.max(width, 0);
    }

    public float getMaxY() {
        return y + Math.max(height, 0);
    }

    public float getMinX() {
        return x + Math.min(width, 0);
    }

    public float getMinY() {
        return y + Math.min(height, 0);
    }

    public void add(Point2D p) {
        float minX = Math.min(getMinX(), p.x);
        float maxX = Math.max(getMaxX(), p.x);
        float minY = Math.min(getMinY(), p.y);
        float maxY = Math.max(getMaxY(), p.y);
        this.x = minX;
        this.y = minY;
        this.width = maxX - minX;
        this.height = maxY - minY;
    }
}
