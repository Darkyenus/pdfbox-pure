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
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.ResourceCache;
import org.apache.pdfbox.pdmodel.common.PDStream;

import java.io.IOException;

/**
 * ICCBased color spaces are based on a cross-platform color profile as defined by the
 * International Color Consortium (ICC).
 *
 * @author Ben Litchfield
 * @author John Hewson
 */
public final class PDICCBased extends PDCIEBasedColorSpace {

    private final PDStream stream;
    private int numberOfComponents = -1;
    private PDColor initialColor;


    /**
     * Creates a new ICC color space with an empty stream.
     * @param doc the document to store the ICC data
     */
    public PDICCBased(PDDocument doc)
    {
        array = new COSArray();
        array.add(COSName.ICCBASED);
        stream = new PDStream(doc);
        array.add(stream);
    }

    /**
     * Creates a new ICC color space using the PDF array.
     *
     * @param iccArray the ICC stream object.
     * @throws IOException if there is an error reading the ICC profile or if the parameter is
     * invalid.
     */
    private PDICCBased(COSArray iccArray) throws IOException
    {
        array = iccArray;
        stream = new PDStream((COSStream) iccArray.getObject(1));
        loadICCProfile();
    }

    /**
     * Creates a new ICC color space using the PDF array, optionally using a resource cache.
     *
     * @param iccArray the ICC stream object.
     * @param resources resources to use as cache, or null for no caching.
     * @return an ICC color space.
     * @throws IOException if there is an error reading the ICC profile or if the parameter is
     * invalid.
     */
    public static PDICCBased create(COSArray iccArray, PDResources resources) throws IOException
    {
        checkArray(iccArray);
        COSBase base = iccArray.get(1);
        if (base instanceof COSObject && resources != null)
        {
            ResourceCache resourceCache = resources.getResourceCache();
            if (resourceCache != null)
            {
                COSObject indirect = (COSObject) base;
                PDColorSpace space = resourceCache.getColorSpace(indirect);
                if (space instanceof PDICCBased)
                {
                    return (PDICCBased) space;
                }
                else
                {
                    PDICCBased newSpace = new PDICCBased(iccArray);
                    resourceCache.put(indirect, newSpace);
                    return newSpace;
                }
            }
        }
        return new PDICCBased(iccArray);
    }

    private static void checkArray(COSArray iccArray) throws IOException
    {
        if (iccArray.size() < 2)
        {
            throw new IOException("ICCBased colorspace array must have two elements");
        }
        if (!(iccArray.getObject(1) instanceof COSStream))
        {
            throw new IOException("ICCBased colorspace array must have a stream as second element");
        }
    }

    @Override
    public String getName()
    {
        return COSName.ICCBASED.getName();
    }

    /**
     * Get the underlying ICC profile stream.
     * @return the underlying ICC profile stream
     */
    public PDStream getPDStream()
    {
        return stream;
    }

    /**
     * Load the ICC profile, or init alternateColorSpace color space.
     */
    private void loadICCProfile() throws IOException
    {
        PDColorSpace alternateColorSpace = getAlternateColorSpace();
        initialColor = alternateColorSpace.getInitialColor();
    }

    @Override
    public int getNumberOfComponents()
    {
        if (numberOfComponents < 0)
        {
            numberOfComponents = stream.getCOSObject().getInt(COSName.N);
        }
        return numberOfComponents;
    }

    @Override
    public PDColor getInitialColor()
    {
        return initialColor;
    }

    /**
     * Returns a list of alternate color spaces for non-conforming readers.
     * WARNING: Do not use the information in a conforming reader.
     * @return A list of alternateColorSpace color spaces.
     * @throws IOException If there is an error getting the alternateColorSpace color spaces.
     */
    public PDColorSpace getAlternateColorSpace() throws IOException
    {
        COSBase alternate = stream.getCOSObject().getDictionaryObject(COSName.ALTERNATE);
        COSArray alternateArray;
        if(alternate == null)
        {
            alternateArray = new COSArray();
            int numComponents = getNumberOfComponents();
            COSName csName;
            switch (numComponents)
            {
                case 1:
                    csName = COSName.DEVICEGRAY;
                    break;
                case 3:
                    csName = COSName.DEVICERGB;
                    break;
                case 4:
                    csName = COSName.DEVICECMYK;
                    break;
                default:
                    throw new IOException("Unknown color space number of components:" + numComponents);
            }
            alternateArray.add(csName);
        }
        else
        {
            if(alternate instanceof COSArray)
            {
                alternateArray = (COSArray)alternate;
            }
            else if(alternate instanceof COSName)
            {
                alternateArray = new COSArray();
                alternateArray.add(alternate);
            }
            else
            {
                throw new IOException("Error: expected COSArray or COSName and not " +
                    alternate.getClass().getName());
            }
        }
        return PDColorSpace.create(alternateArray);
    }

    /**
     * Returns the metadata stream for this object, or null if there is no metadata stream.
     * @return the metadata stream, or null if there is none
     */
    public COSStream getMetadata()
    {
        return stream.getCOSObject().getCOSStream(COSName.METADATA);
    }

    /**
     * Sets the metadata stream that is associated with this color space.
     * @param metadata the new metadata stream
     */
    public void setMetadata(COSStream metadata)
    {
        stream.getCOSObject().setItem(COSName.METADATA, metadata);
    }

    @Override
    public String toString()
    {
        return getName() + "{numberOfComponents: " + getNumberOfComponents() + "}";
    }
}
