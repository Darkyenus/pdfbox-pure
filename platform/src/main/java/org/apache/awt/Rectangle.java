package org.apache.awt;

public class Rectangle {
    public int x;
    public int y;
    public int width;
    public int height;

    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isEmpty() {
        return (width <= 0.0f) || (height <= 0.0f);
    }

    public void setRect(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }


    public void setRect(Rectangle r) {
        this.x = r.getX();
        this.y = r.getY();
        this.width = r.getWidth();
        this.height = r.getHeight();
    }

    public int getMaxX() {
        return x + Math.max(width, 0);
    }

    public int getMaxY() {
        return y + Math.max(height, 0);
    }

    public int getMinX() {
        return x + Math.min(width, 0);
    }

    public int getMinY() {
        return y + Math.min(height, 0);
    }
}
