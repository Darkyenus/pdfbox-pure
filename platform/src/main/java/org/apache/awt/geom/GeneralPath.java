package org.apache.awt.geom;

import java.util.Arrays;

/**
 * Simplified copy of java.awt.geom.GeneralPath
 */
public final class GeneralPath {

    public static final int WIND_EVEN_ODD = 0;
    public static final int WIND_NON_ZERO = 1;

    private static final byte SEG_MOVETO  = (byte) 0;
    private static final byte SEG_LINETO  = (byte) 1;
    private static final byte SEG_QUADTO  = (byte) 2;
    private static final byte SEG_CUBICTO = (byte) 3;
    private static final byte SEG_CLOSE   = (byte) 4;

    private byte[] pointTypes;
    private float[] floatCoords;
    private int numTypes = 0;
    private int numCoords = 0;
    private int windingRule;

    public GeneralPath() {
        this(WIND_NON_ZERO);
    }

    public GeneralPath(int windingRule) {
        this(windingRule, 20);
    }

    public GeneralPath(int windingRule, int initialCapacity) {
        pointTypes = new byte[initialCapacity];
        floatCoords = new float[initialCapacity * 2];
        this.windingRule = windingRule;
    }

    public GeneralPath(GeneralPath clone) {
        this.pointTypes = Arrays.copyOf(clone.pointTypes, Math.max(clone.numTypes, 20));
        this.floatCoords = Arrays.copyOf(clone.floatCoords, Math.max(clone.numCoords, 20));
        this.numTypes = clone.numTypes;
        this.numCoords = clone.numCoords;
        this.windingRule = clone.windingRule;
    }

    public int getWindingRule() {
        return windingRule;
    }

    public void setWindingRule(int rule) {
        if (rule != WIND_EVEN_ODD && rule != WIND_NON_ZERO) {
            throw new IllegalArgumentException("winding rule must be "+
                    "WIND_EVEN_ODD or "+
                    "WIND_NON_ZERO");
        }
        windingRule = rule;
    }

    private void ensureCapacity(int moreTypes, int moreCoords) {
        if (numTypes + moreTypes > pointTypes.length) {
            pointTypes = Arrays.copyOf(pointTypes, Math.max(pointTypes.length * 2, numTypes + moreTypes));
        }
        if (numCoords + moreCoords > floatCoords.length) {
            floatCoords = Arrays.copyOf(floatCoords, Math.max(floatCoords.length * 2, numTypes + moreCoords));
        }
    }

    public void moveTo(float x, float y) {
        if (numTypes > 0 && pointTypes[numTypes - 1] == SEG_MOVETO) {
            floatCoords[numCoords-2] = x;
            floatCoords[numCoords-1] = y;
        } else {
            ensureCapacity(1, 2);
            pointTypes[numTypes++] = SEG_MOVETO;
            floatCoords[numCoords++] = x;
            floatCoords[numCoords++] = y;
        }
    }

    public void lineTo(float x, float y) {
        ensureCapacity(1, 2);
        pointTypes[numTypes++] = SEG_LINETO;
        floatCoords[numCoords++] = x;
        floatCoords[numCoords++] = y;
    }

    public void quadTo(float controlPointX, float controlPointY, float x, float y) {
        ensureCapacity(1, 4);
        pointTypes[numTypes++] = SEG_QUADTO;
        floatCoords[numCoords++] = controlPointX;
        floatCoords[numCoords++] = controlPointY;
        floatCoords[numCoords++] = x;
        floatCoords[numCoords++] = y;
    }

    /** Bézier curve */
    public void curveTo(float controlPoint1X, float controlPoint1Y,
                        float controlPoint2X, float controlPoint2Y,
                        float x, float y) {
        ensureCapacity(1, 6);
        pointTypes[numTypes++] = SEG_CUBICTO;
        floatCoords[numCoords++] = controlPoint1X;
        floatCoords[numCoords++] = controlPoint1Y;
        floatCoords[numCoords++] = controlPoint2X;
        floatCoords[numCoords++] = controlPoint2Y;
        floatCoords[numCoords++] = x;
        floatCoords[numCoords++] = y;
    }

    public void append(PathIterator pi, boolean connect) {
        float[] coords = new float[6];
        while (!pi.isDone()) {
            switch (pi.currentSegment(coords)) {
                case SEG_MOVETO:
                    if (!connect || numTypes < 1 || numCoords < 1) {
                        moveTo(coords[0], coords[1]);
                        break;
                    }
                    if (pointTypes[numTypes - 1] != SEG_CLOSE &&
                            floatCoords[numCoords-2] == coords[0] &&
                            floatCoords[numCoords-1] == coords[1])
                    {
                        // Collapse out initial moveto/lineto
                        break;
                    }
                    lineTo(coords[0], coords[1]);
                    break;
                case SEG_LINETO:
                    lineTo(coords[0], coords[1]);
                    break;
                case SEG_QUADTO:
                    quadTo(coords[0], coords[1],
                            coords[2], coords[3]);
                    break;
                case SEG_CUBICTO:
                    curveTo(coords[0], coords[1],
                            coords[2], coords[3],
                            coords[4], coords[5]);
                    break;
                case SEG_CLOSE:
                    closePath();
                    break;
            }
            pi.next();
            connect = false;
        }
    }

    public void closePath() {
        if (numTypes == 0 || pointTypes[numTypes - 1] != SEG_CLOSE) {
            ensureCapacity(1, 0);
            pointTypes[numTypes++] = SEG_CLOSE;
        }
    }

    public void transform(AffineTransform at) {
        at.transform(floatCoords, 0, floatCoords, 0, numCoords / 2);
    }

    public Rectangle2D getBounds2D() {
        float x1, y1, x2, y2;
        int i = numCoords;
        if (i > 0) {
            y1 = y2 = floatCoords[--i];
            x1 = x2 = floatCoords[--i];
            while (i > 0) {
                float y = floatCoords[--i];
                float x = floatCoords[--i];
                if (x < x1) x1 = x;
                if (y < y1) y1 = y;
                if (x > x2) x2 = x;
                if (y > y2) y2 = y;
            }
        } else {
            x1 = y1 = x2 = y2 = 0.0f;
        }
        return new Rectangle2D(x1, y1, x2 - x1, y2 - y1);
    }

    public PathIterator getPathIterator(AffineTransform transform) {
        return new GeneralPathIterator(this, transform);
    }

    public Rectangle2D getBounds() {
        return getBounds2D();
    }

    private static final class GeneralPathIterator implements PathIterator {

        private final GeneralPath path;
        private final AffineTransform transform;

        private int typeIdx;
        private int pointIdx;

        private static final int[] curvecoords = {2, 2, 4, 6, 0};

        GeneralPathIterator(GeneralPath path, AffineTransform transform) {
            this.path = path;
            this.transform = transform;
        }

        public int getWindingRule() {
            return path.getWindingRule();
        }

        public boolean isDone() {
            return (typeIdx >= path.numTypes);
        }

        public void next() {
            int type = path.pointTypes[typeIdx++];
            pointIdx += curvecoords[type];
        }

        public int currentSegment(float[] coords) {
            int type = path.pointTypes[typeIdx];
            int numCoords = curvecoords[type];
            if (numCoords > 0) {
                if (transform != null) {
                    transform.transform(path.floatCoords, pointIdx,
                            coords, 0, numCoords / 2);
                } else {
                    for (int i = 0; i < numCoords; i++) {
                        coords[i] = path.floatCoords[pointIdx + i];
                    }
                }
            }
            return type;
        }
    }

    private Point2D getPoint(int coordindex) {
        return new Point2D(floatCoords[coordindex],
                floatCoords[coordindex+1]);
    }

    public Point2D getCurrentPoint() {
        int index = numCoords;
        if (numTypes < 1 || index < 1) {
            return null;
        }
        if (pointTypes[numTypes - 1] == SEG_CLOSE) {
            loop:
            for (int i = numTypes - 2; i > 0; i--) {
                switch (pointTypes[i]) {
                    case SEG_MOVETO:
                        break loop;
                    case SEG_LINETO:
                        index -= 2;
                        break;
                    case SEG_QUADTO:
                        index -= 4;
                        break;
                    case SEG_CUBICTO:
                        index -= 6;
                        break;
                    case SEG_CLOSE:
                        break;
                }
            }
        }
        return getPoint(index - 2);
    }

    public void reset() {
        numTypes = numCoords = 0;
    }

    public GeneralPath clone() {
        return new GeneralPath(this);
    }
}
