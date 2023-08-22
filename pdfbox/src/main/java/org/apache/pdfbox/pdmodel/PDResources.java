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
package org.apache.pdfbox.pdmodel;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDPropertyList;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontFactory;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.optionalcontent.PDOptionalContentGroup;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A set of resources available at the page/pages/stream level.
 * 
 * @author Ben Litchfield
 * @author John Hewson
 */
public final class PDResources implements COSObjectable
{
    private final COSDictionary resources;
    private final ResourceCache cache;
    
    // PDFBOX-3442 cache fonts that are not indirect objects, as these aren't cached in ResourceCache
    // and this would result in huge memory footprint in text extraction
    private final Map<COSName, SoftReference<PDFont>> directFontCache;

    /**
     * Constructor for embedding.
     */
    public PDResources()
    {
        resources = new COSDictionary();
        cache = null;
        directFontCache = new HashMap<>();
    }

    /**
     * Constructor for reading.
     *
     * @param resourceDictionary The cos dictionary for this resource.
     */
    public PDResources(COSDictionary resourceDictionary)
    {
        if (resourceDictionary == null)
        {
            throw new IllegalArgumentException("resourceDictionary is null");
        }
        resources = resourceDictionary;
        cache = null;
        directFontCache = new HashMap<>();
    }
    
    /**
     * Constructor for reading.
     *
     * @param resourceDictionary The cos dictionary for this resource.
     * @param resourceCache The document's resource cache, may be null.
     */
    public PDResources(COSDictionary resourceDictionary, ResourceCache resourceCache)
    {
        if (resourceDictionary == null)
        {
            throw new IllegalArgumentException("resourceDictionary is null");
        }
        resources = resourceDictionary;
        cache = resourceCache;
        directFontCache = new HashMap<>();
    }

    /**
     * Constructor for reading.
     *
     * @param resourceDictionary The cos dictionary for this resource.
     * @param resourceCache The document's resource cache, may be null.
     * @param directFontCache The document's direct font cache. Must be mutable
     */
    public PDResources(COSDictionary resourceDictionary, ResourceCache resourceCache,
            Map<COSName, SoftReference<PDFont>> directFontCache)
    {
        if (resourceDictionary == null)
        {
            throw new IllegalArgumentException("resourceDictionary is null");
        }
        if (directFontCache == null)
        {
            throw new IllegalArgumentException("directFontCache is null");
        }
        resources = resourceDictionary;
        cache = resourceCache;
        this.directFontCache = directFontCache;
    }

    /**
     * Returns the underlying dictionary.
     */
    @Override
    public COSDictionary getCOSObject()
    {
        return resources;
    }

    /**
     * Returns the font resource with the given name, or null if none exists.
     *
     * @param name Name of the font resource.
     * @return the font with the given name or null
     * 
     * @throws IOException if something went wrong.
     */
    public PDFont getFont(COSName name) throws IOException
    {
        COSObject indirect = getIndirect(COSName.FONT, name);
        if (cache != null && indirect != null)
        {
            PDFont cached = cache.getFont(indirect);
            if (cached != null)
            {
                return cached;
            }
        }
        else if (indirect == null)
        {
            SoftReference<PDFont> ref = directFontCache.get(name);
            if (ref != null)
            {
                PDFont cached = ref.get();
                if (cached != null)
                {
                    return cached;
                }
            }
        }

        PDFont font = null;
        COSBase base = get(COSName.FONT, name);
        if (base instanceof COSDictionary)
        {
            font = PDFontFactory.createFont((COSDictionary) base, cache);
        }
        
        if (cache != null && indirect != null)
        {
            cache.put(indirect, font);
        }
        else if (indirect == null)
        {
            directFontCache.put(name, new SoftReference<>(font));
        }
        return font;
    }

    /**
     * Returns the color space resource with the given name, or null if none exists.
     * 
     * @param name Name of the color space resource.
     * @return a new color space.
     * @throws IOException if something went wrong.
     */
    public PDColorSpace getColorSpace(COSName name) throws IOException
    {
        return getColorSpace(name, false);
    }
    
    /**
     * Returns the color space resource with the given name, or null if none exists. This method is
     * for PDFBox internal use only, others should use {@link #getColorSpace(COSName)}.
     *
     * @param name Name of the color space resource.
     * @param wasDefault if current color space was used by a default color space. This parameter is
     * to
     * @return a new color space.
     * @throws IOException if something went wrong.
     */
    public PDColorSpace getColorSpace(COSName name, boolean wasDefault) throws IOException
    {
        COSObject indirect = getIndirect(COSName.COLORSPACE, name);
        if (cache != null && indirect != null)
        {
            PDColorSpace cached = cache.getColorSpace(indirect);
            if (cached != null)
            {
                return cached;
            }
        }

        // get the instance
        PDColorSpace colorSpace;
        COSBase object = get(COSName.COLORSPACE, name);
        if (object != null)
        {
            colorSpace = PDColorSpace.create(object, this, wasDefault);
        }
        else
        {
            colorSpace = PDColorSpace.create(name, this, wasDefault);
        }

        // we can't cache PDPattern, because it holds page resources, see PDFBOX-2370
        if (cache != null && indirect != null)
        {
            cache.put(indirect, colorSpace);
        }
        return colorSpace;
    }

    /**
     * Returns true if the given color space name exists in these resources.
     *
     * @param name Name of the color space resource.
     * @return true if the given color space name exists in these resources, otherwise false
     */
    public boolean hasColorSpace(COSName name)
    {
        return get(COSName.COLORSPACE, name) != null;
    }

    /**
     * Returns the extended graphics state resource with the given name, or null if none exists.
     *
     * @param name Name of the graphics state resource.
     * @return the extended graphics state with the given name or null
     */
    public PDExtendedGraphicsState getExtGState(COSName name)
    {
        COSObject indirect = getIndirect(COSName.EXT_G_STATE, name);
        if (cache != null && indirect != null)
        {
            PDExtendedGraphicsState cached = cache.getExtGState(indirect);
            if (cached != null)
            {
                return cached;
            }
        }

        // get the instance
        PDExtendedGraphicsState extGState = null;
        COSBase base = get(COSName.EXT_G_STATE, name);
        if (base instanceof COSDictionary)
        {
            extGState = new PDExtendedGraphicsState((COSDictionary) base);
        }

        if (cache != null && indirect != null)
        {
            cache.put(indirect, extGState);
        }
        return extGState;
    }

    /**
     * Returns the property list resource with the given name, or null if none exists.
     * 
     * @param name Name of the property list resource.
     * @return the property list with the given name or null
     */
    public PDPropertyList getProperties(COSName name)
    {
        COSObject indirect = getIndirect(COSName.PROPERTIES, name);
        if (cache != null && indirect != null)
        {
            PDPropertyList cached = cache.getProperties(indirect);
            if (cached != null)
            {
                return cached;
            }
        }

        // get the instance
        PDPropertyList propertyList = null;
        COSBase base = get(COSName.PROPERTIES, name);
        if (base instanceof COSDictionary)
        {
            propertyList = PDPropertyList.create((COSDictionary) base);
        }

        if (cache != null && indirect != null)
        {
            cache.put(indirect, propertyList);
        }
        return propertyList;
    }

    /**
     * Tells whether the XObject resource with the given name is an image.
     *
     * @param name Name of the XObject resource.
     * @return true if it is an image XObject, false if not.
     */
    public boolean isImageXObject(COSName name)
    {
        // get the instance
        COSBase value = get(COSName.XOBJECT, name);
        if (value == null)
        {
            return false;
        }
        else if (value instanceof COSObject)
        {
            value = ((COSObject) value).getObject();
        }
        if (!(value instanceof COSStream))
        {
            return false;
        }
        COSStream stream = (COSStream) value;
        return COSName.IMAGE.equals(stream.getCOSName(COSName.SUBTYPE));
    }

    /**
     * Returns the XObject resource with the given name, or null if none exists.
     * 
     * @param name Name of the XObject resource.
     * @return the XObject with the given name or null
     * 
     * @throws IOException if something went wrong.
     */
    public PDXObject getXObject(COSName name) throws IOException
    {
        COSObject indirect = getIndirect(COSName.XOBJECT, name);
        if (cache != null && indirect != null)
        {
            PDXObject cached = cache.getXObject(indirect);
            if (cached != null)
            {
                return cached;
            }
        }

        // get the instance
        PDXObject xobject;
        COSBase value = get(COSName.XOBJECT, name);
        if (value == null)
        {
            xobject = null;
        }
        else if (value instanceof COSObject)
        {
            xobject = PDXObject.createXObject(((COSObject) value).getObject(), this);
        }
        else
        {
            xobject = PDXObject.createXObject(value, this);
        }
        return xobject;
    }

    /**
     * Returns the resource with the given name and kind as an indirect object, or null.
     */
    private COSObject getIndirect(COSName kind, COSName name)
    {
        COSDictionary dict = resources.getCOSDictionary(kind);
        if (dict == null)
        {
            return null;
        }
        COSBase base = dict.getItem(name);
        if (base instanceof COSObject)
        {
            return (COSObject)base;
        }
        // not an indirect object. Resource may have been added at runtime.
        return null;
    }
    
    /**
     * Returns the resource with the given name and kind, or null.
     */
    private COSBase get(COSName kind, COSName name)
    {
        COSDictionary dict = resources.getCOSDictionary(kind);
        return dict != null ? dict.getDictionaryObject(name) : null;
    }

    /**
     * Returns the names of the XObject resources, if any.
     * 
     * @return an iterable containing all names of available xobjects
     */
    public Iterable<COSName> getXObjectNames()
    {
        return getNames(COSName.XOBJECT);
    }

    /**
     * Returns the names of the font resources, if any.
     * 
     * @return an iterable containing all names of available fonts
     */
    public Iterable<COSName> getFontNames()
    {
        return getNames(COSName.FONT);
    }

    /**
     * Returns the names of the property list resources, if any.
     * 
     * @return an iterable containing all names of available property lists
     */
    public Iterable<COSName> getPropertiesNames()
    {
        return getNames(COSName.PROPERTIES);
    }

    /**
     * Returns the names of the shading resources, if any.
     * 
     * @return an iterable containing all names of available shadings
     */
    public Iterable<COSName> getShadingNames()
    {
        return getNames(COSName.SHADING);
    }

    /**
     * Returns the resource names of the given kind.
     */
    private Iterable<COSName> getNames(COSName kind)
    {
        COSDictionary dict = resources.getCOSDictionary(kind);
        return dict != null ? dict.keySet() : Collections.emptySet();
    }

    /**
     * Adds the given font to the resources of the current page and returns the name for the
     * new resources. Returns the existing resource name if the given item already exists.
     *
     * @param font the font to add
     * @return the name of the resource in the resources dictionary
     */
    public COSName add(PDFont font)
    {
        return add(COSName.FONT, "F", font);
    }

    /**
     * Adds the given color space to the resources of the current page and returns the name for the
     * new resources. Returns the existing resource name if the given item already exists.
     *
     * @param colorSpace the color space to add
     * @return the name of the resource in the resources dictionary
     */
    public COSName add(PDColorSpace colorSpace)
    {
        return add(COSName.COLORSPACE, "cs", colorSpace);
    }

    /**
     * Adds the given extended graphics state to the resources of the current page and returns the
     * name for the new resources. Returns the existing resource name if the given item already exists.
     *
     * @param extGState the extended graphics state to add
     * @return the name of the resource in the resources dictionary
     */
    public COSName add(PDExtendedGraphicsState extGState)
    {
        return add(COSName.EXT_G_STATE, "gs", extGState);
    }

    /**
     * Adds the given property list to the resources of the current page and returns the name for
     * the new resources. Returns the existing resource name if the given item already exists.
     *
     * @param properties the property list to add
     * @return the name of the resource in the resources dictionary
     */
    public COSName add(PDPropertyList properties)
    {
        if (properties instanceof PDOptionalContentGroup)
        {
            return add(COSName.PROPERTIES, "oc", properties);
        }
        else
        {
            return add(COSName.PROPERTIES, "Prop", properties);
        }
    }

    /**
     * Adds the given image to the resources of the current page and returns the name for the
     * new resources. Returns the existing resource name if the given item already exists.
     *
     * @param image the image to add
     * @return the name of the resource in the resources dictionary
     */
    public COSName add(PDImageXObject image)
    {
        return add(COSName.XOBJECT, "Im", image);
    }

    /**
     * Adds the given form to the resources of the current page and returns the name for the
     * new resources. Returns the existing resource name if the given item already exists.
     *
     * @param form the form to add
     * @return the name of the resource in the resources dictionary
     */
    public COSName add(PDFormXObject form)
    {
        return add(COSName.XOBJECT, "Form", form);
    }

    /**
     * Adds the given XObject to the resources of the current page and returns the name for the
     * new resources. Returns the existing resource name if the given item already exists.
     *
     * @param xobject the XObject to add
     * @param prefix the prefix to be used when creating the resource name
     * @return the name of the resource in the resources dictionary
     */
    public COSName add(PDXObject xobject, String prefix)
    {
        return add(COSName.XOBJECT, prefix, xobject);
    }

    /**
     * Adds the given resource if it does not already exist.
     */
    private COSName add(COSName kind, String prefix, COSObjectable object)
    {
        // return the existing key if the item exists already
        COSDictionary dict = resources.getCOSDictionary(kind);
        if (dict != null && dict.containsValue(object.getCOSObject()))
        {
            return dict.getKeyForValue(object.getCOSObject());
        }

        // PDFBOX-4509: It could exist as an indirect object, happens when a font is taken from the 
        // AcroForm default resources of a loaded PDF.
        if (dict != null && COSName.FONT.equals(kind))
        {
            for (Map.Entry<COSName, COSBase> entry : dict.entrySet())
            {
                if (entry.getValue() instanceof COSObject &&
                    object.getCOSObject() == ((COSObject) entry.getValue()).getObject())
                {
                    return entry.getKey();
                }
            }
        }

        // add the item with a new key
        COSName name = createKey(kind, prefix);
        put(kind, name, object);
        return name;
    }

    /**
     * Returns a unique key for a new resource.
     */
    private COSName createKey(COSName kind, String prefix)
    {
        COSDictionary dict = resources.getCOSDictionary(kind);
        if (dict == null)
        {
            return COSName.getPDFName(prefix + 1);
        }

        // find a unique key
        String key;
        int n = dict.keySet().size();
        do
        {
            ++n;
            key = prefix + n;
        }
        while (dict.containsKey(key));
        return COSName.getPDFName(key);
    }

    /**
     * Sets the value of a given named resource.
     */
    private void put(COSName kind, COSName name, COSObjectable object)
    {
        COSDictionary dict = resources.getCOSDictionary(kind);
        if (dict == null)
        {
            dict = new COSDictionary();
            resources.setItem(kind, dict);
        }
        dict.setItem(name, object);
    }

    /**
     * Sets the font resource with the given name.
     *
     * @param name the name of the resource
     * @param font the font to be added
     */
    public void put(COSName name, PDFont font)
    {
        put(COSName.FONT, name, font);
    }

    /**
     * Sets the color space resource with the given name.
     *
     * @param name the name of the resource
     * @param colorSpace the color space to be added
     */
    public void put(COSName name, PDColorSpace colorSpace)
    {
        put(COSName.COLORSPACE, name, colorSpace);
    }

    /**
     * Sets the extended graphics state resource with the given name.
     *
     * @param name the name of the resource
     * @param extGState the extended graphics state to be added
     */
    public void put(COSName name, PDExtendedGraphicsState extGState)
    {
        put(COSName.EXT_G_STATE, name, extGState);
    }

    /**
     * Sets the property list resource with the given name.
     *
     * @param name the name of the resource
     * @param properties the property list to be added
     */
    public void put(COSName name, PDPropertyList properties)
    {
        put(COSName.PROPERTIES, name, properties);
    }

    /**
     * Sets the XObject resource with the given name.
     *
     * @param name the name of the resource
     * @param xobject the XObject to be added
     */
    public void put(COSName name, PDXObject xobject)
    {
        put(COSName.XOBJECT, name, xobject);
    }

    /**
     * Returns the resource cache associated with the Resources, or null if there is none.
     * 
     * @return the resource cache associated with the resources, or null
     */
    public ResourceCache getResourceCache()
    {
        return cache;
    }
}
