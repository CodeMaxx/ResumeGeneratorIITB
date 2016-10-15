/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfAppearance;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfBorderDictionary;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class PdfAcroForm
extends PdfDictionary {
    private PdfWriter writer;
    private HashMap fieldTemplates = new HashMap();
    private PdfArray documentFields = new PdfArray();
    private PdfArray calculationOrder = new PdfArray();
    private int sigFlags = 0;

    public PdfAcroForm(PdfWriter pdfWriter) {
        this.writer = pdfWriter;
    }

    public void setNeedAppearances(boolean bl) {
        this.put(PdfName.NEEDAPPEARANCES, new PdfBoolean(bl));
    }

    public void addFieldTemplates(HashMap hashMap) {
        this.fieldTemplates.putAll(hashMap);
    }

    public void addDocumentField(PdfIndirectReference pdfIndirectReference) {
        this.documentFields.add(pdfIndirectReference);
    }

    public boolean isValid() {
        if (this.documentFields.size() == 0) {
            return false;
        }
        this.put(PdfName.FIELDS, this.documentFields);
        if (this.sigFlags != 0) {
            this.put(PdfName.SIGFLAGS, new PdfNumber(this.sigFlags));
        }
        if (this.calculationOrder.size() > 0) {
            this.put(PdfName.CO, this.calculationOrder);
        }
        if (this.fieldTemplates.isEmpty()) {
            return true;
        }
        PdfDictionary pdfDictionary = new PdfDictionary();
        Object object = this.fieldTemplates.keySet().iterator();
        while (object.hasNext()) {
            PdfTemplate pdfTemplate = (PdfTemplate)object.next();
            PdfFormField.mergeResources(pdfDictionary, (PdfDictionary)pdfTemplate.getResources());
        }
        this.put(PdfName.DR, pdfDictionary);
        this.put(PdfName.DA, new PdfString("/Helv 0 Tf 0 g "));
        object = (PdfDictionary)pdfDictionary.get(PdfName.FONT);
        if (object != null) {
            this.writer.eliminateFontSubset((PdfDictionary)object);
        }
        return true;
    }

    public void addCalculationOrder(PdfFormField pdfFormField) {
        this.calculationOrder.add(pdfFormField.getIndirectReference());
    }

    public void setSigFlags(int n) {
        this.sigFlags |= n;
    }

    public void addFormField(PdfFormField pdfFormField) {
        this.writer.addAnnotation(pdfFormField);
    }

    public PdfFormField addHtmlPostButton(String string, String string2, String string3, String string4, BaseFont baseFont, float f, float f2, float f3, float f4, float f5) {
        PdfAction pdfAction = PdfAction.createSubmitForm(string4, null, 4);
        PdfFormField pdfFormField = new PdfFormField(this.writer, f2, f3, f4, f5, pdfAction);
        this.setButtonParams(pdfFormField, 65536, string, string3);
        this.drawButton(pdfFormField, string2, baseFont, f, f2, f3, f4, f5);
        this.addFormField(pdfFormField);
        return pdfFormField;
    }

    public PdfFormField addResetButton(String string, String string2, String string3, BaseFont baseFont, float f, float f2, float f3, float f4, float f5) {
        PdfAction pdfAction = PdfAction.createResetForm(null, 0);
        PdfFormField pdfFormField = new PdfFormField(this.writer, f2, f3, f4, f5, pdfAction);
        this.setButtonParams(pdfFormField, 65536, string, string3);
        this.drawButton(pdfFormField, string2, baseFont, f, f2, f3, f4, f5);
        this.addFormField(pdfFormField);
        return pdfFormField;
    }

    public PdfFormField addMap(String string, String string2, String string3, PdfContentByte pdfContentByte, float f, float f2, float f3, float f4) {
        PdfAction pdfAction = PdfAction.createSubmitForm(string3, null, 20);
        PdfFormField pdfFormField = new PdfFormField(this.writer, f, f2, f3, f4, pdfAction);
        this.setButtonParams(pdfFormField, 65536, string, null);
        PdfAppearance pdfAppearance = PdfAppearance.createAppearance(this.writer, f3 - f, f4 - f2);
        pdfAppearance.add(pdfContentByte);
        pdfFormField.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, pdfAppearance);
        this.addFormField(pdfFormField);
        return pdfFormField;
    }

    public void setButtonParams(PdfFormField pdfFormField, int n, String string, String string2) {
        pdfFormField.setButton(n);
        pdfFormField.setFlags(4);
        pdfFormField.setPage();
        pdfFormField.setFieldName(string);
        if (string2 != null) {
            pdfFormField.setValueAsString(string2);
        }
    }

    public void drawButton(PdfFormField pdfFormField, String string, BaseFont baseFont, float f, float f2, float f3, float f4, float f5) {
        PdfAppearance pdfAppearance = PdfAppearance.createAppearance(this.writer, f4 - f2, f5 - f3);
        pdfAppearance.drawButton(0.0f, 0.0f, f4 - f2, f5 - f3, string, baseFont, f);
        pdfFormField.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, pdfAppearance);
    }

    public PdfFormField addHiddenField(String string, String string2) {
        PdfFormField pdfFormField = PdfFormField.createEmpty(this.writer);
        pdfFormField.setFieldName(string);
        pdfFormField.setValueAsName(string2);
        this.addFormField(pdfFormField);
        return pdfFormField;
    }

    public PdfFormField addSingleLineTextField(String string, String string2, BaseFont baseFont, float f, float f2, float f3, float f4, float f5) {
        PdfFormField pdfFormField = PdfFormField.createTextField(this.writer, false, false, 0);
        this.setTextFieldParams(pdfFormField, string2, string, f2, f3, f4, f5);
        this.drawSingleLineOfText(pdfFormField, string2, baseFont, f, f2, f3, f4, f5);
        this.addFormField(pdfFormField);
        return pdfFormField;
    }

    public PdfFormField addMultiLineTextField(String string, String string2, BaseFont baseFont, float f, float f2, float f3, float f4, float f5) {
        PdfFormField pdfFormField = PdfFormField.createTextField(this.writer, true, false, 0);
        this.setTextFieldParams(pdfFormField, string2, string, f2, f3, f4, f5);
        this.drawMultiLineOfText(pdfFormField, string2, baseFont, f, f2, f3, f4, f5);
        this.addFormField(pdfFormField);
        return pdfFormField;
    }

    public PdfFormField addSingleLinePasswordField(String string, String string2, BaseFont baseFont, float f, float f2, float f3, float f4, float f5) {
        PdfFormField pdfFormField = PdfFormField.createTextField(this.writer, false, true, 0);
        this.setTextFieldParams(pdfFormField, string2, string, f2, f3, f4, f5);
        this.drawSingleLineOfText(pdfFormField, string2, baseFont, f, f2, f3, f4, f5);
        this.addFormField(pdfFormField);
        return pdfFormField;
    }

    public void setTextFieldParams(PdfFormField pdfFormField, String string, String string2, float f, float f2, float f3, float f4) {
        pdfFormField.setWidget(new Rectangle(f, f2, f3, f4), PdfAnnotation.HIGHLIGHT_INVERT);
        pdfFormField.setValueAsString(string);
        pdfFormField.setDefaultValueAsString(string);
        pdfFormField.setFieldName(string2);
        pdfFormField.setFlags(4);
        pdfFormField.setPage();
    }

    public void drawSingleLineOfText(PdfFormField pdfFormField, String string, BaseFont baseFont, float f, float f2, float f3, float f4, float f5) {
        PdfAppearance pdfAppearance = PdfAppearance.createAppearance(this.writer, f4 - f2, f5 - f3);
        PdfAppearance pdfAppearance2 = (PdfAppearance)pdfAppearance.getDuplicate();
        pdfAppearance2.setFontAndSize(baseFont, f);
        pdfAppearance2.resetRGBColorFill();
        pdfFormField.setDefaultAppearanceString(pdfAppearance2);
        pdfAppearance.drawTextField(0.0f, 0.0f, f4 - f2, f5 - f3);
        pdfAppearance.beginVariableText();
        pdfAppearance.saveState();
        pdfAppearance.rectangle(3.0f, 3.0f, f4 - f2 - 6.0f, f5 - f3 - 6.0f);
        pdfAppearance.clip();
        pdfAppearance.newPath();
        pdfAppearance.beginText();
        pdfAppearance.setFontAndSize(baseFont, f);
        pdfAppearance.resetRGBColorFill();
        pdfAppearance.setTextMatrix(4.0f, (f5 - f3) / 2.0f - f * 0.3f);
        pdfAppearance.showText(string);
        pdfAppearance.endText();
        pdfAppearance.restoreState();
        pdfAppearance.endVariableText();
        pdfFormField.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, pdfAppearance);
    }

    public void drawMultiLineOfText(PdfFormField pdfFormField, String string, BaseFont baseFont, float f, float f2, float f3, float f4, float f5) {
        PdfAppearance pdfAppearance = PdfAppearance.createAppearance(this.writer, f4 - f2, f5 - f3);
        PdfAppearance pdfAppearance2 = (PdfAppearance)pdfAppearance.getDuplicate();
        pdfAppearance2.setFontAndSize(baseFont, f);
        pdfAppearance2.resetRGBColorFill();
        pdfFormField.setDefaultAppearanceString(pdfAppearance2);
        pdfAppearance.drawTextField(0.0f, 0.0f, f4 - f2, f5 - f3);
        pdfAppearance.beginVariableText();
        pdfAppearance.saveState();
        pdfAppearance.rectangle(3.0f, 3.0f, f4 - f2 - 6.0f, f5 - f3 - 6.0f);
        pdfAppearance.clip();
        pdfAppearance.newPath();
        pdfAppearance.beginText();
        pdfAppearance.setFontAndSize(baseFont, f);
        pdfAppearance.resetRGBColorFill();
        pdfAppearance.setTextMatrix(4.0f, 5.0f);
        StringTokenizer stringTokenizer = new StringTokenizer(string, "\n");
        float f6 = f5 - f3;
        while (stringTokenizer.hasMoreTokens()) {
            pdfAppearance.showTextAligned(0, stringTokenizer.nextToken(), 3.0f, f6 -= f * 1.2f, 0.0f);
        }
        pdfAppearance.endText();
        pdfAppearance.restoreState();
        pdfAppearance.endVariableText();
        pdfFormField.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, pdfAppearance);
    }

    public PdfFormField addCheckBox(String string, String string2, boolean bl, float f, float f2, float f3, float f4) {
        PdfFormField pdfFormField = PdfFormField.createCheckBox(this.writer);
        this.setCheckBoxParams(pdfFormField, string, string2, bl, f, f2, f3, f4);
        this.drawCheckBoxAppearences(pdfFormField, string2, f, f2, f3, f4);
        this.addFormField(pdfFormField);
        return pdfFormField;
    }

    public void setCheckBoxParams(PdfFormField pdfFormField, String string, String string2, boolean bl, float f, float f2, float f3, float f4) {
        pdfFormField.setWidget(new Rectangle(f, f2, f3, f4), PdfAnnotation.HIGHLIGHT_TOGGLE);
        pdfFormField.setFieldName(string);
        if (bl) {
            pdfFormField.setValueAsName(string2);
            pdfFormField.setAppearanceState(string2);
        } else {
            pdfFormField.setValueAsName("Off");
            pdfFormField.setAppearanceState("Off");
        }
        pdfFormField.setFlags(4);
        pdfFormField.setPage();
        pdfFormField.setBorderStyle(new PdfBorderDictionary(1.0f, 0));
    }

    public void drawCheckBoxAppearences(PdfFormField pdfFormField, String string, float f, float f2, float f3, float f4) {
        BaseFont baseFont = null;
        try {
            baseFont = BaseFont.createFont("ZapfDingbats", "Cp1252", false);
        }
        catch (Exception var8_8) {
            throw new ExceptionConverter(var8_8);
        }
        float f5 = f4 - f2;
        PdfAppearance pdfAppearance = PdfAppearance.createAppearance(this.writer, f3 - f, f4 - f2);
        PdfAppearance pdfAppearance2 = (PdfAppearance)pdfAppearance.getDuplicate();
        pdfAppearance2.setFontAndSize(baseFont, f5);
        pdfAppearance2.resetRGBColorFill();
        pdfFormField.setDefaultAppearanceString(pdfAppearance2);
        pdfAppearance.drawTextField(0.0f, 0.0f, f3 - f, f4 - f2);
        pdfAppearance.saveState();
        pdfAppearance.resetRGBColorFill();
        pdfAppearance.beginText();
        pdfAppearance.setFontAndSize(baseFont, f5);
        pdfAppearance.showTextAligned(1, "4", (f3 - f) / 2.0f, (f4 - f2) / 2.0f - f5 * 0.3f, 0.0f);
        pdfAppearance.endText();
        pdfAppearance.restoreState();
        pdfFormField.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, string, pdfAppearance);
        PdfAppearance pdfAppearance3 = PdfAppearance.createAppearance(this.writer, f3 - f, f4 - f2);
        pdfAppearance3.drawTextField(0.0f, 0.0f, f3 - f, f4 - f2);
        pdfFormField.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, "Off", pdfAppearance3);
    }

    public PdfFormField getRadioGroup(String string, String string2, boolean bl) {
        PdfFormField pdfFormField = PdfFormField.createRadioButton(this.writer, bl);
        pdfFormField.setFieldName(string);
        pdfFormField.setValueAsName(string2);
        return pdfFormField;
    }

    public void addRadioGroup(PdfFormField pdfFormField) {
        this.addFormField(pdfFormField);
    }

    public PdfFormField addRadioButton(PdfFormField pdfFormField, String string, float f, float f2, float f3, float f4) {
        PdfFormField pdfFormField2 = PdfFormField.createEmpty(this.writer);
        pdfFormField2.setWidget(new Rectangle(f, f2, f3, f4), PdfAnnotation.HIGHLIGHT_TOGGLE);
        String string2 = ((PdfName)pdfFormField.get(PdfName.V)).toString().substring(1);
        if (string2.equals(string)) {
            pdfFormField2.setAppearanceState(string);
        } else {
            pdfFormField2.setAppearanceState("Off");
        }
        this.drawRadioAppearences(pdfFormField2, string, f, f2, f3, f4);
        pdfFormField.addKid(pdfFormField2);
        return pdfFormField2;
    }

    public void drawRadioAppearences(PdfFormField pdfFormField, String string, float f, float f2, float f3, float f4) {
        PdfAppearance pdfAppearance = PdfAppearance.createAppearance(this.writer, f3 - f, f4 - f2);
        pdfAppearance.drawRadioField(0.0f, 0.0f, f3 - f, f4 - f2, true);
        pdfFormField.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, string, pdfAppearance);
        PdfAppearance pdfAppearance2 = PdfAppearance.createAppearance(this.writer, f3 - f, f4 - f2);
        pdfAppearance2.drawRadioField(0.0f, 0.0f, f3 - f, f4 - f2, false);
        pdfFormField.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, "Off", pdfAppearance2);
    }

    public PdfFormField addSelectList(String string, String[] arrstring, String string2, BaseFont baseFont, float f, float f2, float f3, float f4, float f5) {
        PdfFormField pdfFormField = PdfFormField.createList(this.writer, arrstring, 0);
        this.setChoiceParams(pdfFormField, string, string2, f2, f3, f4, f5);
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arrstring.length; ++i) {
            stringBuffer.append(arrstring[i]).append('\n');
        }
        this.drawMultiLineOfText(pdfFormField, stringBuffer.toString(), baseFont, f, f2, f3, f4, f5);
        this.addFormField(pdfFormField);
        return pdfFormField;
    }

    public PdfFormField addSelectList(String string, String[][] arrstring, String string2, BaseFont baseFont, float f, float f2, float f3, float f4, float f5) {
        PdfFormField pdfFormField = PdfFormField.createList(this.writer, arrstring, 0);
        this.setChoiceParams(pdfFormField, string, string2, f2, f3, f4, f5);
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arrstring.length; ++i) {
            stringBuffer.append(arrstring[i][1]).append('\n');
        }
        this.drawMultiLineOfText(pdfFormField, stringBuffer.toString(), baseFont, f, f2, f3, f4, f5);
        this.addFormField(pdfFormField);
        return pdfFormField;
    }

    public PdfFormField addComboBox(String string, String[] arrstring, String string2, boolean bl, BaseFont baseFont, float f, float f2, float f3, float f4, float f5) {
        PdfFormField pdfFormField = PdfFormField.createCombo(this.writer, bl, arrstring, 0);
        this.setChoiceParams(pdfFormField, string, string2, f2, f3, f4, f5);
        if (string2 == null) {
            string2 = arrstring[0];
        }
        this.drawSingleLineOfText(pdfFormField, string2, baseFont, f, f2, f3, f4, f5);
        this.addFormField(pdfFormField);
        return pdfFormField;
    }

    public PdfFormField addComboBox(String string, String[][] arrstring, String string2, boolean bl, BaseFont baseFont, float f, float f2, float f3, float f4, float f5) {
        PdfFormField pdfFormField = PdfFormField.createCombo(this.writer, bl, arrstring, 0);
        this.setChoiceParams(pdfFormField, string, string2, f2, f3, f4, f5);
        String string3 = null;
        for (int i = 0; i < arrstring.length; ++i) {
            if (!arrstring[i][0].equals(string2)) continue;
            string3 = arrstring[i][1];
            break;
        }
        if (string3 == null) {
            string3 = arrstring[0][1];
        }
        this.drawSingleLineOfText(pdfFormField, string3, baseFont, f, f2, f3, f4, f5);
        this.addFormField(pdfFormField);
        return pdfFormField;
    }

    public void setChoiceParams(PdfFormField pdfFormField, String string, String string2, float f, float f2, float f3, float f4) {
        pdfFormField.setWidget(new Rectangle(f, f2, f3, f4), PdfAnnotation.HIGHLIGHT_INVERT);
        if (string2 != null) {
            pdfFormField.setValueAsString(string2);
            pdfFormField.setDefaultValueAsString(string2);
        }
        pdfFormField.setFieldName(string);
        pdfFormField.setFlags(4);
        pdfFormField.setPage();
        pdfFormField.setBorderStyle(new PdfBorderDictionary(2.0f, 0));
    }

    public PdfFormField addSignature(String string, float f, float f2, float f3, float f4) {
        PdfFormField pdfFormField = PdfFormField.createSignature(this.writer);
        this.setSignatureParams(pdfFormField, string, f, f2, f3, f4);
        this.drawSignatureAppearences(pdfFormField, f, f2, f3, f4);
        this.addFormField(pdfFormField);
        return pdfFormField;
    }

    public void setSignatureParams(PdfFormField pdfFormField, String string, float f, float f2, float f3, float f4) {
        pdfFormField.setWidget(new Rectangle(f, f2, f3, f4), PdfAnnotation.HIGHLIGHT_INVERT);
        pdfFormField.setFieldName(string);
        pdfFormField.setFlags(4);
        pdfFormField.setPage();
        pdfFormField.setMKBorderColor(Color.black);
        pdfFormField.setMKBackgroundColor(Color.white);
    }

    public void drawSignatureAppearences(PdfFormField pdfFormField, float f, float f2, float f3, float f4) {
        PdfAppearance pdfAppearance = PdfAppearance.createAppearance(this.writer, f3 - f, f4 - f2);
        pdfAppearance.setGrayFill(1.0f);
        pdfAppearance.rectangle(0.0f, 0.0f, f3 - f, f4 - f2);
        pdfAppearance.fill();
        pdfAppearance.setGrayStroke(0.0f);
        pdfAppearance.setLineWidth(1.0f);
        pdfAppearance.rectangle(0.5f, 0.5f, f3 - f - 0.5f, f4 - f2 - 0.5f);
        pdfAppearance.closePathStroke();
        pdfAppearance.saveState();
        pdfAppearance.rectangle(1.0f, 1.0f, f3 - f - 2.0f, f4 - f2 - 2.0f);
        pdfAppearance.clip();
        pdfAppearance.newPath();
        pdfAppearance.restoreState();
        pdfFormField.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, pdfAppearance);
    }
}

