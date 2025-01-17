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
package org.apache.pdfbox.pdmodel.graphics.state;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/**
 * An extended graphics state dictionary.
 *
 * @author Ben Litchfield
 */
public class PDExtendedGraphicsState implements COSObjectable
{
    private final COSDictionary dict;

    /**
     * Default constructor, creates blank graphics state.
     */
    public PDExtendedGraphicsState()
    {
        dict = new COSDictionary();
        dict.setItem(COSName.TYPE, COSName.EXT_G_STATE);
    }

    /**
     * Create a graphics state from an existing dictionary.
     *
     * @param dictionary The existing graphics state.
     */
    public PDExtendedGraphicsState(COSDictionary dictionary)
    {
        dict = dictionary;
    }

    /**
     * This will get the underlying dictionary that this class acts on.
     *
     * @return The underlying dictionary for this class.
     */
    @Override
    public COSDictionary getCOSObject()
    {
        return dict;
    }


}