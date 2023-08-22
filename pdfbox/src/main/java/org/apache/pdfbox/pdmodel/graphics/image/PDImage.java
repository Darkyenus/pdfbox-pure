/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.filter.DecodeOptions;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * An image in a PDF document.
 *
 * @author John Hewson
 */
public interface PDImage extends COSObjectable
{

    /**
     * Returns an InputStream containing the image data, irrespective of whether this is an
     * inline image or an image XObject.
     * @return Decoded stream
     * @throws IOException if the data could not be read.
     */
    InputStream createInputStream() throws IOException;

    /**
     * Returns an InputStream containing the image data, irrespective of whether this is an
     * inline image or an image XObject. The given filters will not be decoded.
     * @param stopFilters A list of filters to stop decoding at.
     * @return Decoded stream
     * @throws IOException if the data could not be read.
     */
    InputStream createInputStream(List<String> stopFilters) throws IOException;

    /**
     * Returns an InputStream, passing additional options to each filter. As a side effect, the
     * filterSubsampled flag is set in {@link DecodeOptions}.
     *
     * @param options Additional decoding options passed to the filters used
     * @return Decoded stream
     * @throws IOException if the data could not be read
     */
    InputStream createInputStream(DecodeOptions options) throws IOException;

    /**
     * Returns true if the image has no data.
     * 
     * @return true if the image has no data
     */
    boolean isEmpty();

    /**
     * Returns true if the image is a stencil mask.
     * 
     * @return true if the image is a stencil mask
     */
    boolean isStencil();

    /**
     * Sets whether or not the image is a stencil.
     * This corresponds to the {@code ImageMask} entry in the image stream's dictionary.
     * @param isStencil True to make the image a stencil.
     */
    void setStencil(boolean isStencil);

    /**
     * Returns bits per component of this image, or -1 if one has not been set.
     * 
     * @return bits per component of this image or -1
     */
    int getBitsPerComponent();

    /**
     * Set the number of bits per component.
     * @param bitsPerComponent The number of bits per component.
     */
    void setBitsPerComponent(int bitsPerComponent);

    /**
     * Returns the image's color space.
     * 
     * @return the image's color space
     * @throws IOException If there is an error getting the color space.
     */
    PDColorSpace getColorSpace() throws IOException;

    /**
     * Sets the color space for this image.
     * @param colorSpace The color space for this image.
     */
    void setColorSpace(PDColorSpace colorSpace);

    /**
     * Returns height of this image, or -1 if one has not been set.
     * 
     * @return height of this image or -1
     */
    int getHeight();

    /**
     * Sets the height of the image.
     * @param height The height of the image.
     */
    void setHeight(int height);

    /**
     * Returns the width of this image, or -1 if one has not been set.
     * 
     * @return width of this image or -1
     */
    int getWidth();

    /**
     * Sets the width of the image.
     * @param width The width of the image.
     */
    void setWidth(int width);

    /**
     * Sets the decode array.
     * @param decode  the new decode array.
     */
    void setDecode(COSArray decode);

    /**
     * Returns the decode array.
     * 
     * @return the decode array
     */
    COSArray getDecode();

    /**
     * Returns true if the image should be interpolated when rendered.
     * 
     * @return true if the image should be interpolated when rendered
     */
    boolean getInterpolate();


    /**
     * Sets the Interpolate flag, true for high-quality image scaling.
     * 
     * @param value true for high-quality image scaling
     */
    void setInterpolate(boolean value);

    /**
     * Returns the suffix for this image type, e.g. "jpg"
     * 
     * @return the suffix for this image type
     */
    String getSuffix();
    
    /**
     * Convert this image to a COS object.
     *
     * @return The cos object that matches this image object.
     */
    @Override
    COSDictionary getCOSObject();
}
