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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.interactive.form.FieldUtils.KeyValue;

/**
 * A choice field contains several text items, one or more of which shall be selected as the field
 * value.
 * 
 * @author sug
 * @author John Hewson
 */
public abstract class PDChoice extends PDVariableText
{
    static final int FLAG_COMBO = 1 << 17;
    
    private static final int FLAG_SORT = 1 << 19;
    private static final int FLAG_MULTI_SELECT = 1 << 21;
    private static final int FLAG_DO_NOT_SPELL_CHECK = 1 << 22;
    private static final int FLAG_COMMIT_ON_SEL_CHANGE = 1 << 26;
    
    /**
     * @see PDField#PDField(PDAcroForm)
     *
     * @param acroForm The acroform.
     */
    public PDChoice(PDAcroForm acroForm)
    {
        super(acroForm);
        getCOSObject().setItem(COSName.FT, COSName.CH);
    }

    /**
     * Constructor.
     * 
     * @param acroForm The form that this field is part of.
     * @param field the PDF object to represent as a field.
     * @param parent the parent node of the node
     */
    PDChoice(PDAcroForm acroForm, COSDictionary field, PDNonTerminalField parent)
    {
        super(acroForm, field, parent);
    }
    
    /**
     * This will get the option values "Opt".
     * 
     * <p>
     * For a choice field the options array can either be an array
     * of text strings or an array of a two-element arrays.<br>
     * The method always only returns either the text strings or,
     * in case of two-element arrays, an array of the first element of 
     * the two-element arrays
     * </p>   
     * <p>
     * Use {@link #getOptionsExportValues()} and {@link #getOptionsDisplayValues()}
     * to get the entries of two-element arrays.
     * </p>
     * 
     * @return List containing the export values.
     */
    public List<String> getOptions()
    {
        COSBase values = getCOSObject().getDictionaryObject(COSName.OPT);
        return FieldUtils.getPairableItems(values, 0);
    }

    /**
     * This will set the display values - the 'Opt' key.
     * 
     * <p>
     * The Opt array specifies the list of options in the choice field either
     * as an array of text strings representing the display value 
     * or as an array of a two-element array where the
     * first element is the export value and the second the display value.
     * </p>
     * <p>
     * To set both the export and the display value use {@link #setOptions(List, List)}
     * </p> 
     *
     * @param displayValues List containing all possible options.
     */
    public void setOptions(List<String> displayValues)
    {
        if (displayValues != null && !displayValues.isEmpty())
        {
            if (isSort())
            {
                Collections.sort(displayValues);
            }
            getCOSObject().setItem(COSName.OPT,
                    COSArray.ofCOSStrings(displayValues));
        }
        else
        {
            getCOSObject().removeItem(COSName.OPT);
        }
    }

    /**
     * This will get the display values from the options.
     * 
     * <p>
     * For options with an array of text strings the display value and export value
     * are the same.<br>
     * For options with an array of two-element arrays the display value is the 
     * second entry in the two-element array.
     * </p>
     * 
     * @return List containing all the display values.
     */
    public List<String> getOptionsDisplayValues()
    {
        COSBase values = getCOSObject().getDictionaryObject(COSName.OPT);
        return FieldUtils.getPairableItems(values, 1);
    }

    /**
     * This will get the export values from the options.
     * 
     * <p>
     * For options with an array of text strings the display value and export value
     * are the same.<br>
     * For options with an array of two-element arrays the export value is the 
     * first entry in the two-element array.
     * </p>
     *
     * @return List containing all export values.
     */
    public List<String> getOptionsExportValues()
    {
        return getOptions();
    }
    
    /**
     * This will get the indices of the selected options - the 'I' key.
     * <p>
     * This is only needed if a choice field allows multiple selections and
     * two different items have the same export value or more than one values
     * is selected.
     * </p>
     * <p>The indices are zero-based</p>
     *
     * @return List containing the indices of all selected options.
     */
    public List<Integer> getSelectedOptionsIndex()
    {
        COSArray value = getCOSObject().getCOSArray(COSName.I);
        return value != null ? value.toCOSNumberIntegerList() : Collections.emptyList();
    }

    /**
     * Determines if Sort is set.
     * 
     * <p>
     * If set, the field’s option items shall be sorted alphabetically.
     * The sorting has to be done when writing the PDF. PDF Readers are supposed to
     * display the options in the order in which they occur in the Opt array. 
     * </p>
     * 
     * @return true if the options are sorted.
     */
    public boolean isSort()
    {
        return getCOSObject().getFlag(COSName.FF, FLAG_SORT);
    }

    /**
     * Set the Sort bit.
     * 
     * @see #isSort()
     * @param sort The value for Sort.
     */
    public void setSort(boolean sort)
    {
        getCOSObject().setFlag(COSName.FF, FLAG_SORT, sort);
    }

    /**
     * Determines if MultiSelect is set.
     * 
     * @return true if multi select is allowed.
     */
    public boolean isMultiSelect()
    {
        return getCOSObject().getFlag(COSName.FF, FLAG_MULTI_SELECT);
    }

    /**
     * Set the MultiSelect bit.
     *
     * @param multiSelect The value for MultiSelect.
     */
    public void setMultiSelect(boolean multiSelect)
    {
        getCOSObject().setFlag(COSName.FF, FLAG_MULTI_SELECT, multiSelect);
    }

    /**
     * Determines if DoNotSpellCheck is set.
     * 
     * @return true if spell checker is disabled.
     */
    public boolean isDoNotSpellCheck()
    {
        return getCOSObject().getFlag(COSName.FF, FLAG_DO_NOT_SPELL_CHECK);
    }

    /**
     * Set the DoNotSpellCheck bit.
     *
     * @param doNotSpellCheck The value for DoNotSpellCheck.
     */
    public void setDoNotSpellCheck(boolean doNotSpellCheck)
    {
        getCOSObject().setFlag(COSName.FF, FLAG_DO_NOT_SPELL_CHECK, doNotSpellCheck);
    }

    /**
     * Determines if CommitOnSelChange is set.
     * 
     * @return true if value shall be committed as soon as a selection is made.
     */
    public boolean isCommitOnSelChange()
    {
        return getCOSObject().getFlag(COSName.FF, FLAG_COMMIT_ON_SEL_CHANGE);
    }

    /**
     * Set the CommitOnSelChange bit.
     *
     * @param commitOnSelChange The value for CommitOnSelChange.
     */
    public void setCommitOnSelChange(boolean commitOnSelChange)
    {
        getCOSObject().setFlag(COSName.FF, FLAG_COMMIT_ON_SEL_CHANGE, commitOnSelChange);
    }

    /**
     * Determines if Combo is set.
     * 
     * @return true if value the choice is a combo box..
     */
    public boolean isCombo()
    {
        return getCOSObject().getFlag(COSName.FF, FLAG_COMBO);
    }

    /**
     * Set the Combo bit.
     *
     * @param combo The value for Combo.
     */
    public void setCombo(boolean combo)
    {
        getCOSObject().setFlag(COSName.FF, FLAG_COMBO, combo);
    }

    /**
     * Returns the selected values, or an empty List. This list always contains a single item
     * unless {@link #isMultiSelect()} is true.
     *
     * @return A non-null string.
     */
    public List<String> getValue()
    {
        return getValueFor(COSName.V);
    }

    /**
     * Returns the selected values, or an empty List, for the given key.
     */
    private List<String> getValueFor(COSName name)
    {
        COSBase value = getCOSObject().getDictionaryObject(name);
        if (value instanceof COSString)
        {
            return Collections.singletonList(((COSString) value).getString());
        }
        else if (value instanceof COSArray)
        {
            return ((COSArray) value).toCOSStringStringList();
        }
        return Collections.emptyList();
    }

    @Override
    public String getValueAsString()
    {
        return Arrays.toString(getValue().toArray());
    }

}
