package org.apache.awt;

public final class Point {
    public int x, y;

    public Point() {}

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setLocation(Point point) {
        this.x = point.x;
        this.y = point.y;
    }


    @Override
    public int hashCode()
    {
        return 89 * (623 + this.x) + this.y;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final Point other = (Point) obj;
        return this.x == other.x && this.y == other.y;
    }
}
