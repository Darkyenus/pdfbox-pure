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
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNull;
import org.apache.pdfbox.pdmodel.common.function.PDFunction;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * DeviceN colour spaces may contain an arbitrary number of colour components.
 * DeviceN represents a colour space containing multiple components that correspond to colorants
 * of some target device. As with Separation colour spaces, readers are able to approximate the
 * colorants if they are not available on the current output device, such as a display
 *
 * @author John Hewson
 * @author Ben Litchfield
 */
public class PDDeviceN extends PDSpecialColorSpace
{
    // array indexes
    private static final int COLORANT_NAMES = 1;
    private static final int ALTERNATE_CS = 2;
    private static final int TINT_TRANSFORM = 3;
    private static final int DEVICEN_ATTRIBUTES = 4;

    // fields
    private PDColorSpace alternateColorSpace = null;
    private PDFunction tintTransform = null;
    private PDDeviceNAttributes attributes;
    private PDColor initialColor;

    /**
     * Creates a new DeviceN color space.
     */
    public PDDeviceN()
    {
        array = new COSArray();
        array.add(COSName.DEVICEN);

        // empty placeholder
        array.add(COSNull.NULL);
        array.add(COSNull.NULL);
        array.add(COSNull.NULL);
    }

    /**
     * Creates a new DeviceN color space from the given COS array.
     * 
     * @param deviceN an array containing the color space information
     * 
     * @throws IOException if the colorspace could not be created
     */
    public PDDeviceN(COSArray deviceN) throws IOException
    {
        array = deviceN;
        alternateColorSpace = PDColorSpace.create(array.getObject(ALTERNATE_CS));
        tintTransform = PDFunction.create(array.getObject(TINT_TRANSFORM));

        if (array.size() > DEVICEN_ATTRIBUTES)
        {
            attributes = new PDDeviceNAttributes((COSDictionary)array.getObject(DEVICEN_ATTRIBUTES));
        }

        // set initial color space
        int n = getNumberOfComponents();
        float[] initial = new float[n];
        Arrays.fill(initial, 1);
        initialColor = new PDColor(initial, this);
    }

    @Override
    public String getName()
    {
        return COSName.DEVICEN.getName();
    }

    @Override
    public final int getNumberOfComponents()
    {
        return getColorantNames().size();
    }

    @Override
    public PDColor getInitialColor()
    {
        return initialColor;
    }

    /**
     * Returns the list of colorants.
     * @return the list of colorants
     */
    public List<String> getColorantNames()
    {
        return ((COSArray) array.getObject(COLORANT_NAMES)).toCOSNameStringList();
    }

    /**
     * Returns the attributes associated with the DeviceN color space.
     * @return the DeviceN attributes
     */
    public PDDeviceNAttributes getAttributes()
    {
        return attributes;
    }

    /**
     * Sets the color space attributes.
     * If null is passed in then all attribute will be removed.
     * @param attributes the color space attributes, or null
     */
    public void setAttributes(PDDeviceNAttributes attributes)
    {
        this.attributes = attributes;
        if (attributes == null)
        {
            array.remove(DEVICEN_ATTRIBUTES);
        }
        else
        {
            // make sure array is large enough
            while (array.size() <= DEVICEN_ATTRIBUTES)
            {
                array.add(COSNull.NULL);
            }
            array.set(DEVICEN_ATTRIBUTES, attributes.getCOSDictionary());
        }
    }


    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(getName());
        sb.append('{');
        for (String col : getColorantNames())
        {
            sb.append('\"');
            sb.append(col);
            sb.append("\" ");
        }
        sb.append(alternateColorSpace.getName());
        sb.append(' ');
        sb.append(tintTransform);
        sb.append(' ');
        if (attributes != null)
        {
            sb.append(attributes);
        }
        sb.append('}');
        return sb.toString();
    }
}
