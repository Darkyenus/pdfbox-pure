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
package org.apache.pdfbox.pdmodel.graphics.color;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSName;

/**
 * CIE-based colour spaces that use a dictionary.
 *
 * @author Ben Litchfield
 * @author John Hewson
 */
public abstract class PDCIEDictionaryBasedColorSpace extends PDCIEBasedColorSpace
{
    protected final COSDictionary dictionary;

    // we need to cache whitepoint values, because using getWhitePoint()
    // would create a new default object for each pixel conversion if the original
    // PDF didn't have a whitepoint array
    protected float wpX = 1;
    protected float wpY = 1;
    protected float wpZ = 1;

    protected PDCIEDictionaryBasedColorSpace(COSName cosName)
    {
        array = new COSArray();
        dictionary = new COSDictionary();
        array.add(cosName);
        array.add(dictionary);

        fillWhitepointCache(getWhitepoint());
    }

    /**
     * Creates a new CalRGB color space using the given COS array.
     *
     * @param rgb the cos array which represents this color space
     */
    protected PDCIEDictionaryBasedColorSpace(COSArray rgb)
    {
        array = rgb;
        dictionary = (COSDictionary) array.getObject(1);

        fillWhitepointCache(getWhitepoint());
    }

    private void fillWhitepointCache(PDTristimulus whitepoint)
    {
        wpX = whitepoint.getX();
        wpY = whitepoint.getY();
        wpZ = whitepoint.getZ();
    }

    /**
     * This will return the whitepoint tristimulus. As this is a required field
     * this will never return null. A default of 1,1,1 will be returned if the
     * pdf does not have any values yet.
     *
     * @return the whitepoint tristimulus
     */
    public final PDTristimulus getWhitepoint()
    {
        COSArray wp = dictionary.getCOSArray(COSName.WHITE_POINT);
        if (wp == null)
        {
            wp = new COSArray();
            wp.add(new COSFloat(1.0f));
            wp.add(new COSFloat(1.0f));
            wp.add(new COSFloat(1.0f));
        }
        return new PDTristimulus(wp);
    }

}
