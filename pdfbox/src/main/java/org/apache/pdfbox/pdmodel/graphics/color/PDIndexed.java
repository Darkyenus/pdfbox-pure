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
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDStream;

import java.io.IOException;

/**
 * An Indexed colour space specifies that an area is to be painted using a colour table
 * of arbitrary colours from another color space.
 * 
 * @author John Hewson
 * @author Ben Litchfield
 */
public final class PDIndexed extends PDSpecialColorSpace
{
    private final PDColor initialColor = new PDColor(new float[] { 0 }, this);

    /**
     * Creates a new Indexed color space.
     * Default DeviceRGB, hival 255.
     */
    public PDIndexed()
    {
        array = new COSArray();
        array.add(COSName.INDEXED);
        array.add(COSName.DEVICERGB);
        array.add(COSInteger.get(255));
        array.add(org.apache.pdfbox.cos.COSNull.NULL);
    }

    /**
     * Creates a new indexed color space from the given PDF array.
     * 
     * @param indexedArray the array containing the indexed parameters
     * @throws IOException if the colorspace could not be created
     */
    public PDIndexed(COSArray indexedArray) throws IOException
    {
        this(indexedArray, null);
    }

    /**
     * Creates a new indexed color space from the given PDF array.
     * 
     * @param indexedArray the array containing the indexed parameters
     * @param resources the resources, can be null. Allows to use its cache for the colorspace.
     * @throws IOException if the colorspace could not be created
     */
    public PDIndexed(COSArray indexedArray, PDResources resources) throws IOException
    {
        array = indexedArray;
        // don't call getObject(1), we want to pass a reference if possible
        // to profit from caching (PDFBOX-4149)
        COSBase lookupTable = array.getObject(3);
        // cached lookup data
        byte[] lookupData;
        if (lookupTable instanceof COSString)
        {
            lookupData = ((COSString) lookupTable).getBytes();
        }
        else if (lookupTable instanceof COSStream)
        {
            lookupData = new PDStream((COSStream)lookupTable).toByteArray();
        }
        else if (lookupTable == null)
        {
            lookupData = new byte[0];
        }
        else
        {
            throw new IOException("Error: Unknown type for lookup table " + lookupTable);
        }
    }

    @Override
    public String getName()
    {
        return COSName.INDEXED.getName();
    }

    @Override
    public int getNumberOfComponents()
    {
        return 1;
    }

    @Override
    public PDColor getInitialColor()
    {
        return initialColor;
    }

    // reads the lookup table data from the array

    //
    // WARNING: this method is performance sensitive, modify with care!
    //

    @Override
    public String toString()
    {
        return "Indexed";
    }
}
