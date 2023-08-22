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
import org.apache.pdfbox.cos.COSName;

/**
 * A Pattern color space is either a Tiling pattern or a Shading pattern.
 * @author John Hewson
 * @author Ben Litchfield
 */
public final class PDPattern extends PDSpecialColorSpace
{
    /** A pattern which leaves no marks on the page. */
    private static final PDColor EMPTY_PATTERN = new PDColor(new float[] { }, null);

    /**
     * Creates a new pattern color space.
     *
     */
    public PDPattern()
    {
        array = new COSArray();
        array.add(COSName.PATTERN);
    }

    /**
     * Creates a new uncolored tiling pattern color space.
     * 
     * @param colorSpace The underlying color space.
     */
    public PDPattern(PDColorSpace colorSpace)
    {
        array = new COSArray();
        array.add(COSName.PATTERN);
        array.add(colorSpace);
    }

    @Override
    public String getName()
    {
        return COSName.PATTERN.getName();
    }

    @Override
    public int getNumberOfComponents()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public PDColor getInitialColor()
    {
        return EMPTY_PATTERN;
    }

    @Override
    public String toString()
    {
        return "Pattern";
    }
}
