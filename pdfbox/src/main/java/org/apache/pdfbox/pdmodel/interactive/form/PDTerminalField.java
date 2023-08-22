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

import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.interactive.action.PDFormFieldAdditionalActions;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;

/**
 * A field in an interactive form.
 * Fields may be one of four types: button, text, choice, or signature.
 *
 * @author sug
 */
public abstract class PDTerminalField extends PDField
{
    /**
     * Constructor.
     * 
     * @param acroForm The form that this field is part of.
     */
    protected PDTerminalField(PDAcroForm acroForm)
    {
        super(acroForm);
    }

    /**
     * Constructor.
     * 
     * @param acroForm The form that this field is part of.
     * @param field the PDF object to represent as a field.
     * @param parent the parent node of the node
     */
    PDTerminalField(PDAcroForm acroForm, COSDictionary field, PDNonTerminalField parent)
    {
        super(acroForm, field, parent);
    }

    /**
     * Set the actions of the field.
     * 
     * @param actions The field actions.
     */
    public void setActions(PDFormFieldAdditionalActions actions)
    {
        getCOSObject().setItem(COSName.AA, actions);
    }

    @Override
    public String getFieldType()
    {
        String fieldType = getCOSObject().getNameAsString(COSName.FT);
        if (fieldType == null && getParent() != null)
        {
            fieldType = getParent().getFieldType();
        }
        return fieldType;
    }

    /**
     * Returns the widget annotations associated with this field.
     *
     * @return The list of widget annotations. Be aware that this list is <i>not</i> backed by the
     * actual widget collection of the field, so adding or deleting has no effect on the PDF
     * document until you call {@link #setWidgets(java.util.List) setWidgets()} with the modified
     * list.
     */
    @Override
    public List<PDAnnotationWidget> getWidgets()
    {
        List<PDAnnotationWidget> widgets = new ArrayList<>();
        COSArray kids = getCOSObject().getCOSArray(COSName.KIDS);
        if (kids == null)
        {
            // the field itself is a widget
            widgets.add(new PDAnnotationWidget(getCOSObject()));
        }
        else if (kids.size() > 0)
        {
            // there are multiple widgets
            for (int i = 0; i < kids.size(); i++)
            {
                COSBase kid = kids.getObject(i);
                if (kid instanceof COSDictionary)
                {
                    widgets.add(new PDAnnotationWidget((COSDictionary)kid));
                }
            }
        }
        return widgets;
    }

    /**
     * Sets the field's widget annotations.
     *
     * @param children The list of widget annotations.
     */
    public void setWidgets(List<PDAnnotationWidget> children)
    {
        COSArray kidsArray = new COSArray(children);
        getCOSObject().setItem(COSName.KIDS, kidsArray);
        for (PDAnnotationWidget widget : children)
        {
            widget.getCOSObject().setItem(COSName.PARENT, this);
        }
    }

}
