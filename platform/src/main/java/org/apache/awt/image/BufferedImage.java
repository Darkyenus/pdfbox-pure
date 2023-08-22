package org.apache.awt.image;

import org.apache.awt.Transparency;

public interface BufferedImage extends Transparency {

    int getWidth();

    int getHeight();

    int[] getRGB(int startX, int startY, int w, int h,
                 int[] rgbArray, int offset, int scansize);
}
