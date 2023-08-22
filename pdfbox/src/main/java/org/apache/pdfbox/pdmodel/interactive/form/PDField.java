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
import java.util.List;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.interactive.action.PDFormFieldAdditionalActions;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;

/**
 * A field in an interactive form.
 */
public abstract class PDField implements COSObjectable
{
    private static final int FLAG_READ_ONLY = 1;
    private static final int FLAG_REQUIRED = 1 << 1;
    private static final int FLAG_NO_EXPORT = 1 << 2;

    private final PDAcroForm acroForm;
    private final PDNonTerminalField parent;
    private final COSDictionary dictionary;
   
    /**
     * Constructor.
     * 
     * @param acroForm The form that this field is part of.
     */
    PDField(PDAcroForm acroForm)
    {
        this(acroForm, new COSDictionary(), null);
    }

    /**
     * Constructor.
     *  @param acroForm The form that this field is part of.
     * @param field the PDF object to represent as a field.
     * @param parent the parent node of the node
     */
    PDField(PDAcroForm acroForm, COSDictionary field, PDNonTerminalField parent)
    {
        this.acroForm = acroForm;
        this.dictionary = field;
        this.parent = parent;
    }
    
    /**
     * Creates a COSField subclass from the given COS field. This is for reading fields from PDFs.
     *
     * @param form the form that the field is part of
     * @param field the dictionary representing a field element
     * @param parent the parent node of the node to be created, or null if root.
     * @return a new PDField instance
     */
    static PDField fromDictionary(PDAcroForm form, COSDictionary field, PDNonTerminalField parent)
    {
        return PDFieldFactory.createField(form, field, parent);
    }

    /**
     * Returns the given attribute, inheriting from parent nodes if necessary.
     *
     * @param key the key to look up
     * @return COS value for the given key
     */
    protected COSBase getInheritableAttribute(COSName key)
    {
        if (dictionary.containsKey(key))
        {
            return dictionary.getDictionaryObject(key);
        }
        else if (parent != null)
        {
            return parent.getInheritableAttribute(key);
        }
        else
        {
            return acroForm.getCOSObject().getDictionaryObject(key);
        }
    }
    
    /**
     * Get the FT entry of the field. This is a read only field and is set depending on the actual type. The field type
     * is an inheritable attribute.
     * 
     * @return The Field type.
     * 
     */
    public abstract String getFieldType();

    /**
     * Returns a string representation of the "V" entry, or an empty string.
     * 
     * @return The list of widget annotations.
     */
    public abstract String getValueAsString();


    /**
     * Returns the widget annotations associated with this field.
     * 
     * For {@link PDNonTerminalField} the list will be empty as non terminal fields
     * have no visual representation in the form.
     * 
     * @return a List of {@link PDAnnotationWidget} annotations. Be aware that this list is
     * <i>not</i> backed by the actual widget collection of the field, so adding or deleting has no
     * effect on the PDF document. For {@link PDTerminalField} you'd have to call
     * {@link PDTerminalField#setWidgets(java.util.List) setWidgets()} with the modified list.
     */
    public abstract List<PDAnnotationWidget> getWidgets();
    
    /**
     * sets the field to be read-only.
     * 
     * @param readonly The new flag for readonly.
     */
    public void setReadOnly(boolean readonly)
    {
        dictionary.setFlag(COSName.FF, FLAG_READ_ONLY, readonly);
    }

    /**
     * 
     * @return true if the field is readonly
     */
    public boolean isReadOnly()
    {
        return dictionary.getFlag(COSName.FF, FLAG_READ_ONLY);
    }

    /**
     * sets the flag whether the field is to be required to have a value at the time it is exported
     * by a submit-form action.
     *
     * @param required The new flag for required.
     */
    public void setRequired(boolean required)
    {
        dictionary.setFlag(COSName.FF, FLAG_REQUIRED, required);
    }

    /**
     * @return true if the field is required to have a value at the time it is exported by a
     * submit-form action.
     */
    public boolean isRequired()
    {
        return dictionary.getFlag(COSName.FF, FLAG_REQUIRED);
    }

    /**
     * sets the field to be not exported.
     * 
     * @param noExport The new flag for noExport.
     */
    public void setNoExport(boolean noExport)
    {
        dictionary.setFlag(COSName.FF, FLAG_NO_EXPORT, noExport);
    }

    /**
     * 
     * @return true if the field is not to be exported.
     */
    public boolean isNoExport()
    {
        return dictionary.getFlag(COSName.FF, FLAG_NO_EXPORT);
    }

    /**
     * Get the additional actions for this field. This will return null if there
     * are no additional actions for this field.
     *
     * @return The actions of the field.
     */
    public PDFormFieldAdditionalActions getActions()
    {
        COSDictionary aa = dictionary.getCOSDictionary(COSName.AA);
        return aa != null ? new PDFormFieldAdditionalActions(aa) : null;
    }

    /**
     * Get the parent field to this field, or null if none exists.
     * 
     * @return The parent field.
     */
    public PDNonTerminalField getParent()
    {
        return parent;
    }

    /**
     * This will find one of the child elements. The name array are the components of the name to search down the tree
     * of names. The nameIndex is where to start in that array. This method is called recursively until it finds the end
     * point based on the name array.
     * 
     * @param name An array that picks the path to the field.
     * @param nameIndex The index into the array.
     * @return The field at the endpoint or null if none is found.
     */
    PDField findKid(String[] name, int nameIndex)
    {
        PDField retval = null;
        COSArray kids = dictionary.getCOSArray(COSName.KIDS);
        if (kids != null)
        {
            for (int i = 0; retval == null && i < kids.size(); i++)
            {
                COSDictionary kidDictionary = (COSDictionary) kids.getObject(i);
                if (name[nameIndex].equals(kidDictionary.getString(COSName.T)))
                {
                    retval = PDField.fromDictionary(acroForm, kidDictionary,
                                                    (PDNonTerminalField)this);
                    if (retval != null && name.length > nameIndex + 1)
                    {
                        retval = retval.findKid(name, nameIndex + 1);
                    }
                }
            }
        }
        return retval;
    }

    /**
     * This will get the acroform that this field is part of.
     * 
     * @return The form this field is on.
     */
    public PDAcroForm getAcroForm()
    {
        return acroForm;
    }

    /**
     * This will get the dictionary associated with this field.
     * 
     * @return the dictionary that this class wraps.
     */
    @Override
    public COSDictionary getCOSObject()
    {
        return dictionary;
    }

    /**
     * Returns the partial name of the field.
     * 
     * @return the name of the field
     */
    public String getPartialName()
    {
        return dictionary.getString(COSName.T);
    }

    /**
     * This will set the partial name of the field.
     * 
     * @param name The new name for the field.
     * @throws IllegalArgumentException If the name contains a period character.
     */
    public void setPartialName(String name)
    {
        if (name.contains("."))
        {
            throw new IllegalArgumentException(
                    "A field partial name shall not contain a period character: " + name);
        }
        dictionary.setString(COSName.T, name);
    }

    /**
     * Returns the fully qualified name of the field, which is a concatenation of the names of all the parents fields.
     * 
     * @return the name of the field
     */
    public String getFullyQualifiedName()
    {
        String finalName = getPartialName();
        String parentName = parent != null ? parent.getFullyQualifiedName() : null;
        if (parentName != null)
        {
            if (finalName != null)
            {
                finalName = parentName + "." + finalName;
            }
            else
            {
                finalName = parentName;
            }
        }
        return finalName;
    }

    /**
     * Gets the alternate name of the field ("shall be used in place of the actual field name
     * wherever the field shall be identified in the user interface (such as in error or status
     * messages referring to the field)").
     *
     * @return the alternate name of the field
     */
    public String getAlternateFieldName()
    {
        return dictionary.getString(COSName.TU);
    }

    /**
     * This will set the alternate name of the field ("shall be used in place of the actual field
     * name wherever the field shall be identified in the user interface (such as in error or status
     * messages referring to the field)"). The text appears as a tool tip in Adobe Reader. Because
     * of the usage for error or status messages, it should be different for each field.
     *
     * @param alternateFieldName the alternate name of the field.
     */
    public void setAlternateFieldName(String alternateFieldName)
    {
        dictionary.setString(COSName.TU, alternateFieldName);
    }
    
    /**
     * Gets the mapping name of the field.
     * 
     * The mapping name shall be used when exporting interactive form field
     * data from the document.
     * 
     * @return the mapping name of the field
     */
    public String getMappingName()
    {
        return dictionary.getString(COSName.TM);
    }

    /**
     * This will set the mapping name of the field.
     * 
     * @param mappingName the mapping name of the field
     */
    public void setMappingName(String mappingName)
    {
        dictionary.setString(COSName.TM, mappingName);
    }

    @Override
    public String toString()
    {
        return getFullyQualifiedName() + "{type: " + getClass().getSimpleName() + " value: " +
                getInheritableAttribute(COSName.V) + "}";
    }
}
