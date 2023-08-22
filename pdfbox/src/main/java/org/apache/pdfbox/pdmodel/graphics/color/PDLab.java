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
import org.apache.pdfbox.pdmodel.common.PDRange;

/**
 * A Lab colour space is a CIE-based ABC colour space with two transformation stages.
 *
 * @author Ben Litchfield
 * @author John Hewson
 */
public final class PDLab extends PDCIEDictionaryBasedColorSpace
{
    private PDColor initialColor;
    
    /**
     * Creates a new Lab color space.
     */
    public PDLab()
    {
        super(COSName.LAB);
    }

    /**
     * Creates a new Lab color space from a PDF array.
     * @param lab the color space array
     */
    public PDLab(COSArray lab)
    {
        super(lab);
    }
    
    @Override
    public String getName()
    {
        return COSName.LAB.getName();
    }

    @Override
    public int getNumberOfComponents()
    {
        return 3;
    }

    @Override
    public PDColor getInitialColor()
    {
        if (initialColor == null)
        {
            initialColor = new PDColor(new float[] {
                    0,
                    Math.max(0, getARange().getMin()),
                    Math.max(0, getBRange().getMin()) },
                    this);
        }
        return initialColor;
    }

    /**
     * creates a range array with default values (-100..100 -100..100).
     * @return the new range array.
     */
    private COSArray getDefaultRangeArray()
    {
        COSArray range = new COSArray();
        range.add(new COSFloat(-100));
        range.add(new COSFloat(100));
        range.add(new COSFloat(-100));
        range.add(new COSFloat(100));
        return range;
    }

    /**
     * This will get the valid range for the "a" component.
     * If none is found then the default will be returned, which is -100..100.
     * @return the "a" range.
     */
    public PDRange getARange()
    {
        COSArray rangeArray = dictionary.getCOSArray(COSName.RANGE);
        if (rangeArray == null)
        {
            rangeArray = getDefaultRangeArray();
        }
        return new PDRange(rangeArray, 0);
    }

    /**
     * This will get the valid range for the "b" component.
     * If none is found  then the default will be returned, which is -100..100.
     * @return the "b" range.
     */
    public PDRange getBRange()
    {
        COSArray rangeArray = dictionary.getCOSArray(COSName.RANGE);
        if (rangeArray == null)
        {
            rangeArray = getDefaultRangeArray();
        }
        return new PDRange(rangeArray, 1);
    }

    /**
     * This will set the a range for the "a" component.
     * @param range the new range for the "a" component, 
     * or null if defaults (-100..100) are to be set.
     */
    public void setARange(PDRange range)
    {
        setComponentRangeArray(range, 0);
    }

    /**
     * This will set the "b" range for this color space.
     * @param range the new range for the "b" component,
     * or null if defaults (-100..100) are to be set.
     */
    public void setBRange(PDRange range)
    {
        setComponentRangeArray(range, 2);
    }

    private void setComponentRangeArray(PDRange range, int index)
    {
        COSArray rangeArray = dictionary.getCOSArray(COSName.RANGE);
        if (rangeArray == null)
        {
            rangeArray = getDefaultRangeArray();
        }
        if (range == null)
        {
            // reset to defaults
            rangeArray.set(index, new COSFloat(-100));
            rangeArray.set(index + 1, new COSFloat(100));
        }
        else
        {
            rangeArray.set(index, new COSFloat(range.getMin()));
            rangeArray.set(index + 1, new COSFloat(range.getMax()));
        }
        dictionary.setItem(COSName.RANGE, rangeArray);
        initialColor = null;
    }

}
