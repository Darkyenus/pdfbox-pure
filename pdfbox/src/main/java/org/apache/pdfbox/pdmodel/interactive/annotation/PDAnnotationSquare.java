/*
 * Copyright 2018 The Apache Software Foundation.
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

package org.apache.pdfbox.pdmodel.interactive.annotation;

import org.apache.pdfbox.cos.COSDictionary;

/**
 *
 * @author Paul King
 */
public class PDAnnotationSquare extends PDAnnotationSquareCircle
{
    /**
     * The type of annotation.
     */
    public static final String SUB_TYPE = "Square";

    public PDAnnotationSquare()
    {
        super(SUB_TYPE);
    }

    /**
     * Creates a square annotation from a COSDictionary, expected to be a correct object definition.
     *
     * @param field the PDF object to represent as a field.
     */
    public PDAnnotationSquare(COSDictionary field)
    {
        super(field);
    }

}
