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
package org.apache.pdfbox.pdmodel.graphics.color;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSName;

import org.apache.pdfbox.util.Matrix;

/**
 * A CalRGB colour space is a CIE-based colour space with one transformation stage instead of two.
 * In this type of space, A, B, and C represent calibrated red, green, and blue colour values.
 *
 * @author Ben Litchfield
 * @author John Hewson
 */
public class PDCalRGB extends PDCIEDictionaryBasedColorSpace
{
    private final PDColor initialColor = new PDColor(new float[] { 0, 0, 0 }, this);

    /**
     * Creates a new CalRGB color space.
     */
    public PDCalRGB()
    {
        super(COSName.CALRGB);
    }

    /**
     * Creates a new CalRGB color space using the given COS array.
     * @param rgb the cos array which represents this color space
     */
    public PDCalRGB(COSArray rgb)
    {
        super(rgb);
    }

    @Override
    public String getName()
    {
        return COSName.CALRGB.getName();
    }

    @Override
    public int getNumberOfComponents()
    {
        return 3;
    }

    @Override
    public PDColor getInitialColor()
    {
        return initialColor;
    }

}
