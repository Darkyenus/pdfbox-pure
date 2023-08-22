/*
 * Copyright 2014 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.pdfbox.pdmodel.graphics.image;

import org.apache.awt.Transparency;
import org.apache.awt.image.BufferedImage;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.filter.Filter;
import org.apache.pdfbox.filter.FilterFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceGray;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Factory for creating a PDImageXObject containing a lossless compressed image.
 *
 * @author Tilman Hausherr
 */
public final class LosslessFactory {

    private LosslessFactory()
    {
    }

    /**
     * Creates a new lossless encoded image XObject from a BufferedImage.
     * <p>
     * <u>New for advanced users from 2.0.12 on:</u><br>
     * If you created your image with a non standard ICC colorspace, it will be
     * preserved. (If you load images in java using ImageIO then no need to read
     * this segment) However a new colorspace will be created for each image. So
     * if you create a PDF with several such images, consider replacing the
     * colorspace with a common object to save space. This is done with
     * {@link PDImageXObject#getColorSpace()} and
     * {@link PDImageXObject#setColorSpace(org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace) PDImageXObject.setColorSpace()}
     *
     * @param document the document where the image will be created
     * @param image the BufferedImage to embed
     * @return a new image XObject
     * @throws IOException if something goes wrong
     */
    public static PDImageXObject createFromImage(PDDocument document, BufferedImage image)
            throws IOException {

        return createFromRGBImage(image, document);
    }

    private static PDImageXObject createFromRGBImage(BufferedImage image, PDDocument document) throws IOException
    {
        int height = image.getHeight();
        int width = image.getWidth();
        int[] rgbLineBuffer = new int[width];
        int bpc = 8;
        PDDeviceColorSpace deviceColorSpace = PDDeviceRGB.INSTANCE;
        byte[] imageData = new byte[width * height * 3];
        int byteIdx = 0;
        int alphaByteIdx = 0;
        int alphaBitPos = 7;
        int transparency = image.getTransparency();
        int apbc = transparency == Transparency.BITMASK ? 1 : 8;
        byte[] alphaImageData;
        if (transparency != Transparency.OPAQUE)
        {
            alphaImageData = new byte[((width * apbc / 8) + (width * apbc % 8 != 0 ? 1 : 0)) * height];
        }
        else
        {
            alphaImageData = new byte[0];
        }
        for (int y = 0; y < height; ++y)
        {
            for (int pixel : image.getRGB(0, y, width, 1, rgbLineBuffer, 0, width))
            {
                imageData[byteIdx++] = (byte) ((pixel >> 16) & 0xFF);
                imageData[byteIdx++] = (byte) ((pixel >> 8) & 0xFF);
                imageData[byteIdx++] = (byte) (pixel & 0xFF);

                // we have the alpha right here, so no need to do it separately
                // as done prior April 2018
                if (transparency == Transparency.BITMASK)
                {
                    // write a bit
                    alphaImageData[alphaByteIdx] |= ((pixel >> 24) & 1) << alphaBitPos;
                    if (--alphaBitPos < 0)
                    {
                        alphaBitPos = 7;
                        ++alphaByteIdx;
                    }
                }
                else if (transparency != Transparency.OPAQUE)
                {
                    // write a byte
                    alphaImageData[alphaByteIdx++] = (byte) ((pixel >> 24) & 0xFF);
                }
            }

            // skip boundary if needed
            if (transparency == Transparency.BITMASK && alphaBitPos != 7)
            {
                alphaBitPos = 7;
                ++alphaByteIdx;
            }
        }
        PDImageXObject pdImage = prepareImageXObject(document, imageData,
                image.getWidth(), image.getHeight(), bpc, deviceColorSpace);      
        if (transparency != Transparency.OPAQUE)
        {
            PDImageXObject pdMask = prepareImageXObject(document, alphaImageData,
                    image.getWidth(), image.getHeight(), apbc, PDDeviceGray.INSTANCE);
            pdImage.getCOSObject().setItem(COSName.SMASK, pdMask);
        }
        return pdImage;
    }

    /**
     * Create a PDImageXObject using the Flate filter.
     * 
     * @param document The document.
     * @param byteArray array with data.
     * @param width the image width
     * @param height the image height
     * @param bitsPerComponent the bits per component
     * @param initColorSpace the color space
     * @return the newly created PDImageXObject with the data compressed.
     * @throws IOException 
     */
    static PDImageXObject prepareImageXObject(PDDocument document,
            byte [] byteArray, int width, int height, int bitsPerComponent, 
            PDColorSpace initColorSpace) throws IOException
    {
        //pre-size the output stream to half of the input
        ByteArrayOutputStream baos = new ByteArrayOutputStream(byteArray.length/2);

        Filter filter = FilterFactory.INSTANCE.getFilter(COSName.FLATE_DECODE);
        filter.encode(new ByteArrayInputStream(byteArray), baos, new COSDictionary(), 0);

        ByteArrayInputStream encodedByteStream = new ByteArrayInputStream(baos.toByteArray());
        return new PDImageXObject(document, encodedByteStream, COSName.FLATE_DECODE, 
                width, height, bitsPerComponent, initColorSpace);
    }

}
