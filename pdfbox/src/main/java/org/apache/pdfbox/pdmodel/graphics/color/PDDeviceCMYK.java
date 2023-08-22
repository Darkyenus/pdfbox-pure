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

import org.apache.pdfbox.cos.COSName;

/**
 * Allows colors to be specified according to the subtractive CMYK (cyan, magenta, yellow, black)
 * model typical of printers and other paper-based output devices.
 *
 * @author John Hewson
 * @author Ben Litchfield
 */
public class PDDeviceCMYK extends PDDeviceColorSpace
{
    /**  The single instance of this class. */
    public static PDDeviceCMYK INSTANCE;
    static
    {
        INSTANCE = new PDDeviceCMYK();
    }

    private final PDColor initialColor = new PDColor(new float[] { 0, 0, 0, 1 }, this);

    protected PDDeviceCMYK()
    {
    }

    @Override
    public String getName()
    {
        return COSName.DEVICECMYK.getName();
    }

    @Override
    public int getNumberOfComponents()
    {
        return 4;
    }

    @Override
    public PDColor getInitialColor()
    {
        return initialColor;
    }

}
