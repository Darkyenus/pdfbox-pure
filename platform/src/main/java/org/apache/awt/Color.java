package org.apache.awt;

public class Color implements Transparency {

    private float r, g, b, a;

    public Color(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }
    public Color(int r, int g, int b, int a) {
        this.r = r / 255f;
        this.g = g / 255f;
        this.b = b / 255f;
        this.a = a / 255f;
    }

    public float getRed() {
        return r;
    }

    public float getGreen() {
        return g;
    }

    public float getBlue() {
        return b;
    }

    @Override
    public int getTransparency() {
        if (a == 1f) {
            return Transparency.OPAQUE;
        } else {
            return Transparency.TRANSLUCENT;
        }
    }
}
