/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfRectangle;
import com.lowagie.text.pdf.PdfSignature;
import com.lowagie.text.pdf.PdfStamperImp;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class PdfFormField
extends PdfAnnotation {
    public static final int FF_READ_ONLY = 1;
    public static final int FF_REQUIRED = 2;
    public static final int FF_NO_EXPORT = 4;
    public static final int FF_NO_TOGGLE_TO_OFF = 16384;
    public static final int FF_RADIO = 32768;
    public static final int FF_PUSHBUTTON = 65536;
    public static final int FF_MULTILINE = 4096;
    public static final int FF_PASSWORD = 8192;
    public static final int FF_COMBO = 131072;
    public static final int FF_EDIT = 262144;
    public static final int FF_FILESELECT = 1048576;
    public static final int FF_MULTISELECT = 2097152;
    public static final int FF_DONOTSPELLCHECK = 4194304;
    public static final int FF_DONOTSCROLL = 8388608;
    public static final int FF_COMB = 16777216;
    public static final int FF_RADIOSINUNISON = 33554432;
    public static final int Q_LEFT = 0;
    public static final int Q_CENTER = 1;
    public static final int Q_RIGHT = 2;
    public static final int MK_NO_ICON = 0;
    public static final int MK_NO_CAPTION = 1;
    public static final int MK_CAPTION_BELOW = 2;
    public static final int MK_CAPTION_ABOVE = 3;
    public static final int MK_CAPTION_RIGHT = 4;
    public static final int MK_CAPTION_LEFT = 5;
    public static final int MK_CAPTION_OVERLAID = 6;
    public static final PdfName IF_SCALE_ALWAYS = PdfName.A;
    public static final PdfName IF_SCALE_BIGGER = PdfName.B;
    public static final PdfName IF_SCALE_SMALLER = PdfName.S;
    public static final PdfName IF_SCALE_NEVER = PdfName.N;
    public static final PdfName IF_SCALE_ANAMORPHIC = PdfName.A;
    public static final PdfName IF_SCALE_PROPORTIONAL = PdfName.P;
    public static final boolean MULTILINE = true;
    public static final boolean SINGLELINE = false;
    public static final boolean PLAINTEXT = false;
    public static final boolean PASSWORD = true;
    static PdfName[] mergeTarget = new PdfName[]{PdfName.FONT, PdfName.XOBJECT, PdfName.COLORSPACE, PdfName.PATTERN};
    protected PdfFormField parent;
    protected ArrayList kids;

    public PdfFormField(PdfWriter pdfWriter, float f, float f2, float f3, float f4, PdfAction pdfAction) {
        super(pdfWriter, f, f2, f3, f4, pdfAction);
        this.put(PdfName.TYPE, PdfName.ANNOT);
        this.put(PdfName.SUBTYPE, PdfName.WIDGET);
        this.annotation = true;
    }

    protected PdfFormField(PdfWriter pdfWriter) {
        super(pdfWriter, null);
        this.form = true;
        this.annotation = false;
    }

    public void setWidget(Rectangle rectangle, PdfName pdfName) {
        this.put(PdfName.TYPE, PdfName.ANNOT);
        this.put(PdfName.SUBTYPE, PdfName.WIDGET);
        this.put(PdfName.RECT, new PdfRectangle(rectangle));
        this.annotation = true;
        if (pdfName != null && !pdfName.equals(HIGHLIGHT_INVERT)) {
            this.put(PdfName.H, pdfName);
        }
    }

    public static PdfFormField createEmpty(PdfWriter pdfWriter) {
        PdfFormField pdfFormField = new PdfFormField(pdfWriter);
        return pdfFormField;
    }

    public void setButton(int n) {
        this.put(PdfName.FT, PdfName.BTN);
        if (n != 0) {
            this.put(PdfName.FF, new PdfNumber(n));
        }
    }

    protected static PdfFormField createButton(PdfWriter pdfWriter, int n) {
        PdfFormField pdfFormField = new PdfFormField(pdfWriter);
        pdfFormField.setButton(n);
        return pdfFormField;
    }

    public static PdfFormField createPushButton(PdfWriter pdfWriter) {
        return PdfFormField.createButton(pdfWriter, 65536);
    }

    public static PdfFormField createCheckBox(PdfWriter pdfWriter) {
        return PdfFormField.createButton(pdfWriter, 0);
    }

    public static PdfFormField createRadioButton(PdfWriter pdfWriter, boolean bl) {
        return PdfFormField.createButton(pdfWriter, 32768 + (bl ? 16384 : 0));
    }

    public static PdfFormField createTextField(PdfWriter pdfWriter, boolean bl, boolean bl2, int n) {
        PdfFormField pdfFormField = new PdfFormField(pdfWriter);
        pdfFormField.put(PdfName.FT, PdfName.TX);
        int n2 = bl ? 4096 : 0;
        pdfFormField.put(PdfName.FF, new PdfNumber(n2 += bl2 ? 8192 : 0));
        if (n > 0) {
            pdfFormField.put(PdfName.MAXLEN, new PdfNumber(n));
        }
        return pdfFormField;
    }

    protected static PdfFormField createChoice(PdfWriter pdfWriter, int n, PdfArray pdfArray, int n2) {
        PdfFormField pdfFormField = new PdfFormField(pdfWriter);
        pdfFormField.put(PdfName.FT, PdfName.CH);
        pdfFormField.put(PdfName.FF, new PdfNumber(n));
        pdfFormField.put(PdfName.OPT, pdfArray);
        if (n2 > 0) {
            pdfFormField.put(PdfName.TI, new PdfNumber(n2));
        }
        return pdfFormField;
    }

    public static PdfFormField createList(PdfWriter pdfWriter, String[] arrstring, int n) {
        return PdfFormField.createChoice(pdfWriter, 0, PdfFormField.processOptions(arrstring), n);
    }

    public static PdfFormField createList(PdfWriter pdfWriter, String[][] arrstring, int n) {
        return PdfFormField.createChoice(pdfWriter, 0, PdfFormField.processOptions(arrstring), n);
    }

    public static PdfFormField createCombo(PdfWriter pdfWriter, boolean bl, String[] arrstring, int n) {
        return PdfFormField.createChoice(pdfWriter, 131072 + (bl ? 262144 : 0), PdfFormField.processOptions(arrstring), n);
    }

    public static PdfFormField createCombo(PdfWriter pdfWriter, boolean bl, String[][] arrstring, int n) {
        return PdfFormField.createChoice(pdfWriter, 131072 + (bl ? 262144 : 0), PdfFormField.processOptions(arrstring), n);
    }

    protected static PdfArray processOptions(String[] arrstring) {
        PdfArray pdfArray = new PdfArray();
        for (int i = 0; i < arrstring.length; ++i) {
            pdfArray.add(new PdfString(arrstring[i], "UnicodeBig"));
        }
        return pdfArray;
    }

    protected static PdfArray processOptions(String[][] arrstring) {
        PdfArray pdfArray = new PdfArray();
        for (int i = 0; i < arrstring.length; ++i) {
            String[] arrstring2 = arrstring[i];
            PdfArray pdfArray2 = new PdfArray(new PdfString(arrstring2[0], "UnicodeBig"));
            pdfArray2.add(new PdfString(arrstring2[1], "UnicodeBig"));
            pdfArray.add(pdfArray2);
        }
        return pdfArray;
    }

    public static PdfFormField createSignature(PdfWriter pdfWriter) {
        PdfFormField pdfFormField = new PdfFormField(pdfWriter);
        pdfFormField.put(PdfName.FT, PdfName.SIG);
        return pdfFormField;
    }

    public PdfFormField getParent() {
        return this.parent;
    }

    public void addKid(PdfFormField pdfFormField) {
        pdfFormField.parent = this;
        if (this.kids == null) {
            this.kids = new ArrayList();
        }
        this.kids.add(pdfFormField);
    }

    public ArrayList getKids() {
        return this.kids;
    }

    public int setFieldFlags(int n) {
        PdfNumber pdfNumber = (PdfNumber)this.get(PdfName.FF);
        int n2 = pdfNumber == null ? 0 : pdfNumber.intValue();
        int n3 = n2 | n;
        this.put(PdfName.FF, new PdfNumber(n3));
        return n2;
    }

    public void setValueAsString(String string) {
        this.put(PdfName.V, new PdfString(string, "UnicodeBig"));
    }

    public void setValueAsName(String string) {
        this.put(PdfName.V, new PdfName(string));
    }

    public void setValue(PdfSignature pdfSignature) {
        this.put(PdfName.V, pdfSignature);
    }

    public void setDefaultValueAsString(String string) {
        this.put(PdfName.DV, new PdfString(string, "UnicodeBig"));
    }

    public void setDefaultValueAsName(String string) {
        this.put(PdfName.DV, new PdfName(string));
    }

    public void setFieldName(String string) {
        if (string != null) {
            this.put(PdfName.T, new PdfString(string, "UnicodeBig"));
        }
    }

    public void setUserName(String string) {
        this.put(PdfName.TU, new PdfString(string, "UnicodeBig"));
    }

    public void setMappingName(String string) {
        this.put(PdfName.TM, new PdfString(string, "UnicodeBig"));
    }

    public void setQuadding(int n) {
        this.put(PdfName.Q, new PdfNumber(n));
    }

    static void mergeResources(PdfDictionary pdfDictionary, PdfDictionary pdfDictionary2, PdfStamperImp pdfStamperImp) {
        PdfDictionary pdfDictionary3 = null;
        PdfDictionary pdfDictionary4 = null;
        PdfName pdfName = null;
        for (int i = 0; i < mergeTarget.length; ++i) {
            pdfName = mergeTarget[i];
            PdfDictionary pdfDictionary5 = (PdfDictionary)PdfReader.getPdfObject(pdfDictionary2.get(pdfName));
            pdfDictionary3 = pdfDictionary5;
            if (pdfDictionary3 == null) continue;
            pdfDictionary4 = (PdfDictionary)PdfReader.getPdfObject(pdfDictionary.get(pdfName), pdfDictionary);
            if (pdfDictionary4 == null) {
                pdfDictionary4 = new PdfDictionary();
            }
            pdfDictionary4.mergeDifferent(pdfDictionary3);
            pdfDictionary.put(pdfName, pdfDictionary4);
            if (pdfStamperImp == null) continue;
            pdfStamperImp.markUsed(pdfDictionary4);
        }
    }

    static void mergeResources(PdfDictionary pdfDictionary, PdfDictionary pdfDictionary2) {
        PdfFormField.mergeResources(pdfDictionary, pdfDictionary2, null);
    }

    public void setUsed() {
        PdfArray pdfArray;
        this.used = true;
        if (this.parent != null) {
            this.put(PdfName.PARENT, this.parent.getIndirectReference());
        }
        if (this.kids != null) {
            pdfArray = new PdfArray();
            for (int i = 0; i < this.kids.size(); ++i) {
                pdfArray.add(((PdfFormField)this.kids.get(i)).getIndirectReference());
            }
            this.put(PdfName.KIDS, pdfArray);
        }
        if (this.templates == null) {
            return;
        }
        pdfArray = new PdfDictionary();
        Iterator iterator = this.templates.keySet().iterator();
        while (iterator.hasNext()) {
            PdfTemplate pdfTemplate = (PdfTemplate)iterator.next();
            PdfFormField.mergeResources((PdfDictionary)((Object)pdfArray), (PdfDictionary)pdfTemplate.getResources());
        }
        this.put(PdfName.DR, pdfArray);
    }

    public static PdfAnnotation shallowDuplicate(PdfAnnotation pdfAnnotation) {
        PdfDictionary pdfDictionary;
        if (pdfAnnotation.isForm()) {
            PdfAnnotation pdfAnnotation2 = pdfDictionary = new PdfFormField(pdfAnnotation.writer);
            PdfFormField pdfFormField = (PdfFormField)pdfAnnotation;
            pdfAnnotation2.parent = pdfFormField.parent;
            pdfAnnotation2.kids = pdfFormField.kids;
        } else {
            pdfDictionary = new PdfAnnotation(pdfAnnotation.writer, null);
        }
        pdfDictionary.merge(pdfAnnotation);
        pdfDictionary.form = pdfAnnotation.form;
        pdfDictionary.annotation = pdfAnnotation.annotation;
        pdfDictionary.templates = pdfAnnotation.templates;
        return pdfDictionary;
    }
}

