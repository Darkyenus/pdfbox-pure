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
package org.apache.pdfbox.pdmodel.interactive.form;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.font.PDFont;

/**
 * An interactive form, also known as an AcroForm.
 *
 * @author Ben Litchfield
 */
public final class PDAcroForm implements COSObjectable
{
    private static final Log LOG = LogFactory.getLog(PDAcroForm.class);

    private static final int FLAG_SIGNATURES_EXIST = 1;
    private static final int FLAG_APPEND_ONLY = 1 << 1;

    private final PDDocument document;
    private final COSDictionary dictionary;

    private Map<String, PDField> fieldCache;

    private ScriptingHandler scriptingHandler;

    private final Map<COSName, SoftReference<PDFont>> directFontCache = new HashMap<>();

    /**
     * Constructor.
     *
     * @param doc The document that this form is part of.
     */
    public PDAcroForm(PDDocument doc)
    {
        document = doc;
        dictionary = new COSDictionary();
        dictionary.setItem(COSName.FIELDS, new COSArray());
    }

    /**
     * Constructor.
     *
     * @param doc The document that this form is part of.
     * @param form The existing acroForm.
     */
    public PDAcroForm(PDDocument doc, COSDictionary form)
    {
        document = doc;
        dictionary = form;
    }

    /**
     * This will get the document associated with this form.
     *
     * @return The PDF document.
     */
    PDDocument getDocument()
    {
        return document;
    }

    @Override
    public COSDictionary getCOSObject()
    {
        return dictionary;
    }


    /**
     * This will return all of the documents root fields.
     * 
     * A field might have children that are fields (non-terminal field) or does not
     * have children which are fields (terminal fields).
     * 
     * The fields within an AcroForm are organized in a tree structure. The documents root fields 
     * might either be terminal fields, non-terminal fields or a mixture of both. Non-terminal fields
     * mark branches which contents can be retrieved using {@link PDNonTerminalField#getChildren()}.
     * 
     * @return A list of the documents root fields, never null. If there are no fields then this
     * method returns an empty list.
     */
    public List<PDField> getFields()
    {
        COSArray cosFields = dictionary.getCOSArray(COSName.FIELDS);
        if (cosFields == null)
        {
            return Collections.emptyList();
        }
        List<PDField> pdFields = new ArrayList<>();
        for (int i = 0; i < cosFields.size(); i++)
        {
            COSBase element = cosFields.getObject(i);
            if (element instanceof COSDictionary)
            {
                PDField field = PDField.fromDictionary(this, (COSDictionary) element, null);
                if (field != null)
                {
                    pdFields.add(field);
                }
            }
        }
        return new COSArrayList<>(pdFields, cosFields);
    }

    /**
     * Set the documents root fields.
     *
     * @param fields The fields that are part of the documents root fields.
     */
    public void setFields(List<PDField> fields)
    {
        dictionary.setItem(COSName.FIELDS, new COSArray(fields));
    }

    /**
     * Returns an iterator which walks all fields in the field tree, in order.
     * 
     * @return an iterator which walks all fields in the field tree
     */
    public Iterator<PDField> getFieldIterator()
    {
        return new PDFieldTree(this).iterator();
    }

    /**
     * Return the field tree representing all form fields
     * 
     * @return the field tree representing all form fields
     */
    public PDFieldTree getFieldTree()
    {
        return new PDFieldTree(this);
    }

    /**
     * This will tell this form to cache the fields into a Map structure
     * for fast access via the getField method.  The default is false.  You would
     * want this to be false if you were changing the COSDictionary behind the scenes,
     * otherwise setting this to true is acceptable.
     *
     * @param cache A boolean telling if we should cache the fields.
     */
    public void setCacheFields(boolean cache)
    {
        if (cache)
        {
            fieldCache = new HashMap<>();

            for (PDField field : getFieldTree())
            {
                fieldCache.put(field.getFullyQualifiedName(), field);
            }
        }
        else
        {
            fieldCache = null;
        }
    }

    /**
     * This will tell if this acro form is caching the fields.
     *
     * @return true if the fields are being cached.
     */
    public boolean isCachingFields()
    {
        return fieldCache != null;
    }

    /**
     * This will get a field by name, possibly using the cache if setCache is true.
     *
     * @param fullyQualifiedName The name of the field to get.
     * @return The field with that name of null if one was not found.
     */
    public PDField getField(String fullyQualifiedName)
    {
        // get the field from the cache if there is one.
        if (fieldCache != null)
        {
            return fieldCache.get(fullyQualifiedName);
        }

        // get the field from the field tree
        for (PDField field : getFieldTree())
        {
            if (field.getFullyQualifiedName().equals(fullyQualifiedName))
            {
                return field;
            }
        }

        return null;
    }

    /**
     * Get the default appearance.
     * 
     * @return the DA element of the dictionary object
     */
    public String getDefaultAppearance()
    {
        return dictionary.getString(COSName.DA,"");
    }

    /**
     * Set the default appearance.
     * 
     * @param daValue a string describing the default appearance
     */
    public void setDefaultAppearance(String daValue)
    {
        dictionary.setString(COSName.DA, daValue);
    }

    /**
     * True if the viewing application should construct the appearances of all field widgets.
     * The default value is false.
     * 
     * @return the value of NeedAppearances, false if the value isn't set
     */
    public boolean getNeedAppearances()
    {
        return dictionary.getBoolean(COSName.NEED_APPEARANCES, false);
    }

    /**
     * Set the NeedAppearances value. If this is false, PDFBox will create appearances for all field
     * widget.
     * 
     * @param value the value for NeedAppearances
     */
    public void setNeedAppearances(Boolean value)
    {
        dictionary.setBoolean(COSName.NEED_APPEARANCES, value);
    }

    /**
     * This will get the default resources for the AcroForm.
     *
     * @return The default resources or null if there is none.
     */
    public PDResources getDefaultResources()
    {
        COSDictionary dr = dictionary.getCOSDictionary(COSName.DR);
        return dr != null ? new PDResources(dr, document.getResourceCache(), directFontCache)
                : null;
    }

    /**
     * This will set the default resources for the acroform.
     *
     * @param dr The new default resources.
     */
    public void setDefaultResources(PDResources dr)
    {
        dictionary.setItem(COSName.DR, dr);
    }

    /**
     * This will tell if the AcroForm has XFA content.
     *
     * @return true if the AcroForm is an XFA form
     */
    public boolean hasXFA()
    {
        return dictionary.containsKey(COSName.XFA);
    }

    /**
     * This will tell if the AcroForm is a dynamic XFA form.
     *
     * @return true if the AcroForm is a dynamic XFA form
     */
    public boolean xfaIsDynamic()
    {
        return hasXFA() && getFields().isEmpty();
    }

    /**
     * Get the XFA resource, the XFA resource is only used for PDF 1.5+ forms.
     *
     * @return The xfa resource or null if it does not exist.
     */
    public PDXFAResource getXFA()
    {
        COSBase base = dictionary.getDictionaryObject(COSName.XFA);
        return base != null ? new PDXFAResource(base) : null;
    }

    /**
     * Set the XFA resource, this is only used for PDF 1.5+ forms.
     *
     * @param xfa The xfa resource.
     */
    public void setXFA(PDXFAResource xfa)
    {
        dictionary.setItem(COSName.XFA, xfa);
    }

    /**
     * This will get the document-wide default value for the quadding/justification of variable text
     * fields. 
     * <p>
     * 0 - Left(default)<br>
     * 1 - Centered<br>
     * 2 - Right<br>
     * See the QUADDING constants of {@link PDVariableText}.
     *
     * @return The justification of the variable text fields.
     */
    public int getQ()
    {
        return dictionary.getInt(COSName.Q, 0);
    }

    /**
     * This will set the document-wide default value for the quadding/justification of variable text
     * fields. See the QUADDING constants of {@link PDVariableText}.
     *
     * @param q The justification of the variable text fields.
     */
    public void setQ(int q)
    {
        dictionary.setInt(COSName.Q, q);
    }

    /**
     * Determines if SignaturesExist is set.
     * 
     * @return true if the document contains at least one signature.
     */
    public boolean isSignaturesExist()
    {
        return dictionary.getFlag(COSName.SIG_FLAGS, FLAG_SIGNATURES_EXIST);
    }

    /**
     * Set the SignaturesExist bit.
     *
     * @param signaturesExist The value for SignaturesExist.
     */
    public void setSignaturesExist(boolean signaturesExist)
    {
        dictionary.setFlag(COSName.SIG_FLAGS, FLAG_SIGNATURES_EXIST, signaturesExist);
    }

    /**
     * Determines if AppendOnly is set.
     * 
     * @return true if the document contains signatures that may be invalidated if the file is saved.
     */
    public boolean isAppendOnly()
    {
        return dictionary.getFlag(COSName.SIG_FLAGS, FLAG_APPEND_ONLY);
    }

    /**
     * Set a handler to support JavaScript actions in the form.
     * 
     * @return scriptingHandler the handler to support JavaScript actions in the form
     */
    public ScriptingHandler getScriptingHandler()
    {
        return scriptingHandler;
    }

    /**
     * Set a handler to support JavaScript actions in the form.
     * 
     * @param scriptingHandler a handler to support JavaScript actions in the form
     */
    public void setScriptingHandler(ScriptingHandler scriptingHandler)
    {
        this.scriptingHandler = scriptingHandler;
    }

    /**
     * Set the AppendOnly bit.
     *
     * @param appendOnly The value for AppendOnly.
     */
    public void setAppendOnly(boolean appendOnly)
    {
        dictionary.setFlag(COSName.SIG_FLAGS, FLAG_APPEND_ONLY, appendOnly);
    }

    /**
     * Return the calculation order in which field values should be recalculated when the value of
     * any field changes. (Read about "Trigger Events" in the PDF specification)
     *
     * @return field list. Note these objects may not be identical to PDField objects retrieved from
     * other methods (depending on cache setting). The best strategy is to call
     * {@link #getCOSObject()} to check for identity. The list is not backed by the /CO COSArray in
     * the document.
     */
    public List<PDField> getCalcOrder()
    {
        COSArray co = dictionary.getCOSArray(COSName.CO);
        if (co == null)
        {
            return Collections.emptyList();
        }

        Iterable<PDField> fields = isCachingFields() ? fieldCache.values() : getFieldTree();

        List<PDField> actuals = new ArrayList<>();
        for (int i = 0; i < co.size(); i++)
        {
            COSBase item = co.getObject(i);
            for (PDField field : fields)
            {
                if (field.getCOSObject() == item)
                {
                    actuals.add(field);
                    break;
                }
            }
        }
        return actuals;
    }

    /**
     * Set the calculation order in which field values should be recalculated when the value of any
     * field changes. (Read about "Trigger Events" in the PDF specification)
     *
     * @param fields The field list.
     */
    public void setCalcOrder(List<PDField> fields)
    {
        dictionary.setItem(COSName.CO, new COSArray(fields));
    }

}
