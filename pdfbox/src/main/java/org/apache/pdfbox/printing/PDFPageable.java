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

package org.apache.pdfbox.printing;

import org.apache.pdfbox.pdmodel.PDDocument;

/**
 * Prints a PDF document using its original paper size.
 *
 * @author John Hewson
 */
public final class PDFPageable {
    private final PDDocument document;
    private final int numberOfPages;
    private final boolean showPageBorder;
    private final float dpi;
    private final Orientation orientation;
    private boolean subsamplingAllowed = false;

    /**
     * Creates a new PDFPageable.
     *
     * @param document the document to print
     */
    public PDFPageable(PDDocument document)
    {
        this(document, Orientation.AUTO, false, 0);
    }
    
    /**
     * Creates a new PDFPageable with the given page orientation.
     *
     * @param document the document to print
     * @param orientation page orientation policy
     */
    public PDFPageable(PDDocument document, Orientation orientation)
    {
        this(document, orientation, false, 0);
    }
    
    /**
     * Creates a new PDFPageable with the given page orientation and with optional page borders
     * shown. The image will be rasterized at the given DPI before being sent to the printer.
     *
     * @param document the document to print
     * @param orientation page orientation policy
     * @param showPageBorder true if page borders are to be printed
     */
    public PDFPageable(PDDocument document, Orientation orientation, boolean showPageBorder)
    {
        this(document, orientation, showPageBorder, 0);
    }

    /**
     * Creates a new PDFPageable with the given page orientation and with optional page borders
     * shown. The image will be rasterized at the given DPI before being sent to the printer.
     *
     * @param document the document to print
     * @param orientation page orientation policy
     * @param showPageBorder true if page borders are to be printed
     * @param dpi if non-zero then the image will be rasterized at the given DPI
     */
    public PDFPageable(PDDocument document, Orientation orientation, boolean showPageBorder,
                       float dpi)
    {
        this.document = document;
        this.orientation = orientation;
        this.showPageBorder = showPageBorder;
        this.dpi = dpi;
        numberOfPages = document.getNumberOfPages();
    }

    /**
     * Value indicating if the renderer is allowed to subsample images before drawing, according to
     * image dimensions and requested scale.
     *
     * Subsampling may be faster and less memory-intensive in some cases, but it may also lead to
     * loss of quality, especially in images with high spatial frequency.
     *
     * @return true if subsampling of images is allowed, false otherwise.
     */
    public boolean isSubsamplingAllowed()
    {
        return subsamplingAllowed;
    }

    /**
     * Sets a value instructing the renderer whether it is allowed to subsample images before
     * drawing. The subsampling frequency is determined according to image size and requested scale.
     *
     * Subsampling may be faster and less memory-intensive in some cases, but it may also lead to
     * loss of quality, especially in images with high spatial frequency.
     *
     * @param subsamplingAllowed The new value indicating if subsampling is allowed.
     */
    public void setSubsamplingAllowed(boolean subsamplingAllowed)
    {
        this.subsamplingAllowed = subsamplingAllowed;
    }

    public int getNumberOfPages()
    {
        return numberOfPages;
    }

}
