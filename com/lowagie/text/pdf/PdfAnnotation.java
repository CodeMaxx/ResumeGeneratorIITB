/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ByteBuffer;
import com.lowagie.text.pdf.CMYKColor;
import com.lowagie.text.pdf.ExtendedColor;
import com.lowagie.text.pdf.GrayColor;
import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfBorderArray;
import com.lowagie.text.pdf.PdfBorderDictionary;
import com.lowagie.text.pdf.PdfColor;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDestination;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfFileSpecification;
import com.lowagie.text.pdf.PdfIndirectObject;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfOCG;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfRectangle;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PdfAnnotation
extends PdfDictionary {
    public static final PdfName HIGHLIGHT_NONE = PdfName.N;
    public static final PdfName HIGHLIGHT_INVERT = PdfName.I;
    public static final PdfName HIGHLIGHT_OUTLINE = PdfName.O;
    public static final PdfName HIGHLIGHT_PUSH = PdfName.P;
    public static final PdfName HIGHLIGHT_TOGGLE = PdfName.T;
    public static final int FLAGS_INVISIBLE = 1;
    public static final int FLAGS_HIDDEN = 2;
    public static final int FLAGS_PRINT = 4;
    public static final int FLAGS_NOZOOM = 8;
    public static final int FLAGS_NOROTATE = 16;
    public static final int FLAGS_NOVIEW = 32;
    public static final int FLAGS_READONLY = 64;
    public static final int FLAGS_LOCKED = 128;
    public static final int FLAGS_TOGGLENOVIEW = 256;
    public static final PdfName APPEARANCE_NORMAL = PdfName.N;
    public static final PdfName APPEARANCE_ROLLOVER = PdfName.R;
    public static final PdfName APPEARANCE_DOWN = PdfName.D;
    public static final PdfName AA_ENTER = PdfName.E;
    public static final PdfName AA_EXIT = PdfName.X;
    public static final PdfName AA_DOWN = PdfName.D;
    public static final PdfName AA_UP = PdfName.U;
    public static final PdfName AA_FOCUS = PdfName.FO;
    public static final PdfName AA_BLUR = PdfName.BL;
    public static final PdfName AA_JS_KEY = PdfName.K;
    public static final PdfName AA_JS_FORMAT = PdfName.F;
    public static final PdfName AA_JS_CHANGE = PdfName.V;
    public static final PdfName AA_JS_OTHER_CHANGE = PdfName.C;
    public static final int MARKUP_HIGHLIGHT = 0;
    public static final int MARKUP_UNDERLINE = 1;
    public static final int MARKUP_STRIKEOUT = 2;
    public static final int MARKUP_SQUIGGLY = 3;
    protected PdfWriter writer;
    protected PdfIndirectReference reference;
    protected HashMap templates;
    protected boolean form = false;
    protected boolean annotation = true;
    protected boolean used = false;
    private int placeInPage = -1;

    public PdfAnnotation(PdfWriter pdfWriter, Rectangle rectangle) {
        this.writer = pdfWriter;
        if (rectangle != null) {
            this.put(PdfName.RECT, new PdfRectangle(rectangle));
        }
    }

    public PdfAnnotation(PdfWriter pdfWriter, float f, float f2, float f3, float f4, PdfString pdfString, PdfString pdfString2) {
        this.writer = pdfWriter;
        this.put(PdfName.SUBTYPE, PdfName.TEXT);
        this.put(PdfName.T, pdfString);
        this.put(PdfName.RECT, new PdfRectangle(f, f2, f3, f4));
        this.put(PdfName.CONTENTS, pdfString2);
    }

    public PdfAnnotation(PdfWriter pdfWriter, float f, float f2, float f3, float f4, PdfAction pdfAction) {
        this.writer = pdfWriter;
        this.put(PdfName.SUBTYPE, PdfName.LINK);
        this.put(PdfName.RECT, new PdfRectangle(f, f2, f3, f4));
        this.put(PdfName.A, pdfAction);
        this.put(PdfName.BORDER, new PdfBorderArray(0.0f, 0.0f, 0.0f));
        this.put(PdfName.C, new PdfColor(0, 0, 255));
    }

    public static PdfAnnotation createScreen(PdfWriter pdfWriter, Rectangle rectangle, String string, PdfFileSpecification pdfFileSpecification, String string2, boolean bl) throws IOException {
        PdfAnnotation pdfAnnotation = new PdfAnnotation(pdfWriter, rectangle);
        pdfAnnotation.put(PdfName.SUBTYPE, PdfName.SCREEN);
        pdfAnnotation.put(PdfName.F, new PdfNumber(4));
        pdfAnnotation.put(PdfName.TYPE, PdfName.ANNOT);
        pdfAnnotation.setPage();
        PdfIndirectReference pdfIndirectReference = pdfAnnotation.getIndirectReference();
        PdfAction pdfAction = PdfAction.rendition(string, pdfFileSpecification, string2, pdfIndirectReference);
        PdfIndirectReference pdfIndirectReference2 = pdfWriter.addToBody(pdfAction).getIndirectReference();
        if (bl) {
            PdfDictionary pdfDictionary = new PdfDictionary();
            pdfDictionary.put(new PdfName("PV"), pdfIndirectReference2);
            pdfAnnotation.put(PdfName.AA, pdfDictionary);
        }
        pdfAnnotation.put(PdfName.A, pdfIndirectReference2);
        return pdfAnnotation;
    }

    public PdfIndirectReference getIndirectReference() {
        if (this.reference == null) {
            this.reference = this.writer.getPdfIndirectReference();
        }
        return this.reference;
    }

    public static PdfAnnotation createText(PdfWriter pdfWriter, Rectangle rectangle, String string, String string2, boolean bl, String string3) {
        PdfAnnotation pdfAnnotation = new PdfAnnotation(pdfWriter, rectangle);
        pdfAnnotation.put(PdfName.SUBTYPE, PdfName.TEXT);
        if (string != null) {
            pdfAnnotation.put(PdfName.T, new PdfString(string, "UnicodeBig"));
        }
        if (string2 != null) {
            pdfAnnotation.put(PdfName.CONTENTS, new PdfString(string2, "UnicodeBig"));
        }
        if (bl) {
            pdfAnnotation.put(PdfName.OPEN, PdfBoolean.PDFTRUE);
        }
        if (string3 != null) {
            pdfAnnotation.put(PdfName.NAME, new PdfName(string3));
        }
        return pdfAnnotation;
    }

    protected static PdfAnnotation createLink(PdfWriter pdfWriter, Rectangle rectangle, PdfName pdfName) {
        PdfAnnotation pdfAnnotation = new PdfAnnotation(pdfWriter, rectangle);
        pdfAnnotation.put(PdfName.SUBTYPE, PdfName.LINK);
        if (!pdfName.equals(HIGHLIGHT_INVERT)) {
            pdfAnnotation.put(PdfName.H, pdfName);
        }
        return pdfAnnotation;
    }

    public static PdfAnnotation createLink(PdfWriter pdfWriter, Rectangle rectangle, PdfName pdfName, PdfAction pdfAction) {
        PdfAnnotation pdfAnnotation = PdfAnnotation.createLink(pdfWriter, rectangle, pdfName);
        pdfAnnotation.putEx(PdfName.A, pdfAction);
        return pdfAnnotation;
    }

    public static PdfAnnotation createLink(PdfWriter pdfWriter, Rectangle rectangle, PdfName pdfName, String string) {
        PdfAnnotation pdfAnnotation = PdfAnnotation.createLink(pdfWriter, rectangle, pdfName);
        pdfAnnotation.put(PdfName.DEST, new PdfString(string));
        return pdfAnnotation;
    }

    public static PdfAnnotation createLink(PdfWriter pdfWriter, Rectangle rectangle, PdfName pdfName, int n, PdfDestination pdfDestination) {
        PdfAnnotation pdfAnnotation = PdfAnnotation.createLink(pdfWriter, rectangle, pdfName);
        PdfIndirectReference pdfIndirectReference = pdfWriter.getPageReference(n);
        pdfDestination.addPage(pdfIndirectReference);
        pdfAnnotation.put(PdfName.DEST, pdfDestination);
        return pdfAnnotation;
    }

    public static PdfAnnotation createFreeText(PdfWriter pdfWriter, Rectangle rectangle, String string, PdfContentByte pdfContentByte) {
        PdfAnnotation pdfAnnotation = new PdfAnnotation(pdfWriter, rectangle);
        pdfAnnotation.put(PdfName.SUBTYPE, PdfName.FREETEXT);
        pdfAnnotation.put(PdfName.CONTENTS, new PdfString(string, "UnicodeBig"));
        pdfAnnotation.setDefaultAppearanceString(pdfContentByte);
        return pdfAnnotation;
    }

    public static PdfAnnotation createLine(PdfWriter pdfWriter, Rectangle rectangle, String string, float f, float f2, float f3, float f4) {
        PdfAnnotation pdfAnnotation = new PdfAnnotation(pdfWriter, rectangle);
        pdfAnnotation.put(PdfName.SUBTYPE, PdfName.LINE);
        pdfAnnotation.put(PdfName.CONTENTS, new PdfString(string, "UnicodeBig"));
        PdfArray pdfArray = new PdfArray(new PdfNumber(f));
        pdfArray.add(new PdfNumber(f2));
        pdfArray.add(new PdfNumber(f3));
        pdfArray.add(new PdfNumber(f4));
        pdfAnnotation.put(PdfName.L, pdfArray);
        return pdfAnnotation;
    }

    public static PdfAnnotation createSquareCircle(PdfWriter pdfWriter, Rectangle rectangle, String string, boolean bl) {
        PdfAnnotation pdfAnnotation = new PdfAnnotation(pdfWriter, rectangle);
        if (bl) {
            pdfAnnotation.put(PdfName.SUBTYPE, PdfName.SQUARE);
        } else {
            pdfAnnotation.put(PdfName.SUBTYPE, PdfName.CIRCLE);
        }
        pdfAnnotation.put(PdfName.CONTENTS, new PdfString(string, "UnicodeBig"));
        return pdfAnnotation;
    }

    public static PdfAnnotation createMarkup(PdfWriter pdfWriter, Rectangle rectangle, String string, int n, float[] arrf) {
        PdfAnnotation pdfAnnotation = new PdfAnnotation(pdfWriter, rectangle);
        PdfName pdfName = PdfName.HIGHLIGHT;
        switch (n) {
            case 1: {
                pdfName = PdfName.UNDERLINE;
                break;
            }
            case 2: {
                pdfName = PdfName.STRIKEOUT;
                break;
            }
            case 3: {
                pdfName = PdfName.SQUIGGLY;
            }
        }
        pdfAnnotation.put(PdfName.SUBTYPE, pdfName);
        pdfAnnotation.put(PdfName.CONTENTS, new PdfString(string, "UnicodeBig"));
        PdfArray pdfArray = new PdfArray();
        for (int i = 0; i < arrf.length; ++i) {
            pdfArray.add(new PdfNumber(arrf[i]));
        }
        pdfAnnotation.put(PdfName.QUADPOINTS, pdfArray);
        return pdfAnnotation;
    }

    public static PdfAnnotation createStamp(PdfWriter pdfWriter, Rectangle rectangle, String string, String string2) {
        PdfAnnotation pdfAnnotation = new PdfAnnotation(pdfWriter, rectangle);
        pdfAnnotation.put(PdfName.SUBTYPE, PdfName.STAMP);
        pdfAnnotation.put(PdfName.CONTENTS, new PdfString(string, "UnicodeBig"));
        pdfAnnotation.put(PdfName.NAME, new PdfName(string2));
        return pdfAnnotation;
    }

    public static PdfAnnotation createInk(PdfWriter pdfWriter, Rectangle rectangle, String string, float[][] arrf) {
        PdfAnnotation pdfAnnotation = new PdfAnnotation(pdfWriter, rectangle);
        pdfAnnotation.put(PdfName.SUBTYPE, PdfName.INK);
        pdfAnnotation.put(PdfName.CONTENTS, new PdfString(string, "UnicodeBig"));
        PdfArray pdfArray = new PdfArray();
        for (int i = 0; i < arrf.length; ++i) {
            PdfArray pdfArray2 = new PdfArray();
            float[] arrf2 = arrf[i];
            for (int j = 0; j < arrf2.length; ++j) {
                pdfArray2.add(new PdfNumber(arrf2[j]));
            }
            pdfArray.add(pdfArray2);
        }
        pdfAnnotation.put(PdfName.INKLIST, pdfArray);
        return pdfAnnotation;
    }

    public static PdfAnnotation createFileAttachment(PdfWriter pdfWriter, Rectangle rectangle, String string, byte[] arrby, String string2, String string3) throws IOException {
        return PdfAnnotation.createFileAttachment(pdfWriter, rectangle, string, PdfFileSpecification.fileEmbedded(pdfWriter, string2, string3, arrby));
    }

    public static PdfAnnotation createFileAttachment(PdfWriter pdfWriter, Rectangle rectangle, String string, PdfFileSpecification pdfFileSpecification) throws IOException {
        PdfAnnotation pdfAnnotation = new PdfAnnotation(pdfWriter, rectangle);
        pdfAnnotation.put(PdfName.SUBTYPE, PdfName.FILEATTACHMENT);
        if (string != null) {
            pdfAnnotation.put(PdfName.CONTENTS, new PdfString(string, "UnicodeBig"));
        }
        pdfAnnotation.put(PdfName.FS, pdfFileSpecification.getReference());
        return pdfAnnotation;
    }

    public static PdfAnnotation createPopup(PdfWriter pdfWriter, Rectangle rectangle, String string, boolean bl) {
        PdfAnnotation pdfAnnotation = new PdfAnnotation(pdfWriter, rectangle);
        pdfAnnotation.put(PdfName.SUBTYPE, PdfName.POPUP);
        if (string != null) {
            pdfAnnotation.put(PdfName.CONTENTS, new PdfString(string, "UnicodeBig"));
        }
        if (bl) {
            pdfAnnotation.put(PdfName.OPEN, PdfBoolean.PDFTRUE);
        }
        return pdfAnnotation;
    }

    public void setDefaultAppearanceString(PdfContentByte pdfContentByte) {
        byte[] arrby = pdfContentByte.getInternalBuffer().toByteArray();
        int n = arrby.length;
        for (int i = 0; i < n; ++i) {
            if (arrby[i] != 10) continue;
            arrby[i] = 32;
        }
        this.put(PdfName.DA, new PdfString(arrby));
    }

    public void setFlags(int n) {
        if (n == 0) {
            this.remove(PdfName.F);
        } else {
            this.put(PdfName.F, new PdfNumber(n));
        }
    }

    public void setBorder(PdfBorderArray pdfBorderArray) {
        this.put(PdfName.BORDER, pdfBorderArray);
    }

    public void setBorderStyle(PdfBorderDictionary pdfBorderDictionary) {
        this.put(PdfName.BS, pdfBorderDictionary);
    }

    public void setHighlighting(PdfName pdfName) {
        if (pdfName.equals(HIGHLIGHT_INVERT)) {
            this.remove(PdfName.H);
        } else {
            this.put(PdfName.H, pdfName);
        }
    }

    public void setAppearance(PdfName pdfName, PdfTemplate pdfTemplate) {
        PdfDictionary pdfDictionary = (PdfDictionary)this.get(PdfName.AP);
        if (pdfDictionary == null) {
            pdfDictionary = new PdfDictionary();
        }
        pdfDictionary.put(pdfName, pdfTemplate.getIndirectReference());
        this.put(PdfName.AP, pdfDictionary);
        if (!this.form) {
            return;
        }
        if (this.templates == null) {
            this.templates = new HashMap();
        }
        this.templates.put(pdfTemplate, null);
    }

    public void setAppearance(PdfName pdfName, String string, PdfTemplate pdfTemplate) {
        PdfObject pdfObject;
        PdfDictionary pdfDictionary = (PdfDictionary)this.get(PdfName.AP);
        if (pdfDictionary == null) {
            pdfDictionary = new PdfDictionary();
        }
        PdfDictionary pdfDictionary2 = (pdfObject = pdfDictionary.get(pdfName)) != null && pdfObject.isDictionary() ? (PdfDictionary)pdfObject : new PdfDictionary();
        pdfDictionary2.put(new PdfName(string), pdfTemplate.getIndirectReference());
        pdfDictionary.put(pdfName, pdfDictionary2);
        this.put(PdfName.AP, pdfDictionary);
        if (!this.form) {
            return;
        }
        if (this.templates == null) {
            this.templates = new HashMap();
        }
        this.templates.put(pdfTemplate, null);
    }

    public void setAppearanceState(String string) {
        if (string == null) {
            this.remove(PdfName.AS);
            return;
        }
        this.put(PdfName.AS, new PdfName(string));
    }

    public void setColor(Color color) {
        this.put(PdfName.C, new PdfColor(color));
    }

    public void setTitle(String string) {
        if (string == null) {
            this.remove(PdfName.T);
            return;
        }
        this.put(PdfName.T, new PdfString(string, "UnicodeBig"));
    }

    public void setPopup(PdfAnnotation pdfAnnotation) {
        this.put(PdfName.POPUP, pdfAnnotation.getIndirectReference());
        pdfAnnotation.put(PdfName.PARENT, this.getIndirectReference());
    }

    public void setAction(PdfAction pdfAction) {
        this.put(PdfName.A, pdfAction);
    }

    public void setAdditionalActions(PdfName pdfName, PdfAction pdfAction) {
        PdfObject pdfObject = this.get(PdfName.AA);
        PdfDictionary pdfDictionary = pdfObject != null && pdfObject.isDictionary() ? (PdfDictionary)pdfObject : new PdfDictionary();
        pdfDictionary.put(pdfName, pdfAction);
        this.put(PdfName.AA, pdfDictionary);
    }

    public boolean isUsed() {
        return this.used;
    }

    public void setUsed() {
        this.used = true;
    }

    public HashMap getTemplates() {
        return this.templates;
    }

    public boolean isForm() {
        return this.form;
    }

    public boolean isAnnotation() {
        return this.annotation;
    }

    public void setPage(int n) {
        this.put(PdfName.P, this.writer.getPageReference(n));
    }

    public void setPage() {
        this.put(PdfName.P, this.writer.getCurrentPage());
    }

    public int getPlaceInPage() {
        return this.placeInPage;
    }

    public void setPlaceInPage(int n) {
        this.placeInPage = n;
    }

    public void setRotate(int n) {
        this.put(PdfName.ROTATE, new PdfNumber(n));
    }

    PdfDictionary getMK() {
        PdfDictionary pdfDictionary = (PdfDictionary)this.get(PdfName.MK);
        if (pdfDictionary == null) {
            pdfDictionary = new PdfDictionary();
            this.put(PdfName.MK, pdfDictionary);
        }
        return pdfDictionary;
    }

    public void setMKRotation(int n) {
        this.getMK().put(PdfName.R, new PdfNumber(n));
    }

    public static PdfArray getMKColor(Color color) {
        PdfArray pdfArray = new PdfArray();
        int n = ExtendedColor.getType(color);
        switch (n) {
            case 1: {
                pdfArray.add(new PdfNumber(((GrayColor)color).getGray()));
                break;
            }
            case 2: {
                CMYKColor cMYKColor = (CMYKColor)color;
                pdfArray.add(new PdfNumber(cMYKColor.getCyan()));
                pdfArray.add(new PdfNumber(cMYKColor.getMagenta()));
                pdfArray.add(new PdfNumber(cMYKColor.getYellow()));
                pdfArray.add(new PdfNumber(cMYKColor.getBlack()));
                break;
            }
            case 3: 
            case 4: 
            case 5: {
                throw new RuntimeException("Separations, patterns and shadings are not allowed in MK dictionary.");
            }
            default: {
                pdfArray.add(new PdfNumber((float)color.getRed() / 255.0f));
                pdfArray.add(new PdfNumber((float)color.getGreen() / 255.0f));
                pdfArray.add(new PdfNumber((float)color.getBlue() / 255.0f));
            }
        }
        return pdfArray;
    }

    public void setMKBorderColor(Color color) {
        if (color == null) {
            this.getMK().remove(PdfName.BC);
        } else {
            this.getMK().put(PdfName.BC, PdfAnnotation.getMKColor(color));
        }
    }

    public void setMKBackgroundColor(Color color) {
        if (color == null) {
            this.getMK().remove(PdfName.BG);
        } else {
            this.getMK().put(PdfName.BG, PdfAnnotation.getMKColor(color));
        }
    }

    public void setMKNormalCaption(String string) {
        this.getMK().put(PdfName.CA, new PdfString(string, "UnicodeBig"));
    }

    public void setMKRolloverCaption(String string) {
        this.getMK().put(PdfName.RC, new PdfString(string, "UnicodeBig"));
    }

    public void setMKAlternateCaption(String string) {
        this.getMK().put(PdfName.AC, new PdfString(string, "UnicodeBig"));
    }

    public void setMKNormalIcon(PdfTemplate pdfTemplate) {
        this.getMK().put(PdfName.I, pdfTemplate.getIndirectReference());
    }

    public void setMKRolloverIcon(PdfTemplate pdfTemplate) {
        this.getMK().put(PdfName.RI, pdfTemplate.getIndirectReference());
    }

    public void setMKAlternateIcon(PdfTemplate pdfTemplate) {
        this.getMK().put(PdfName.IX, pdfTemplate.getIndirectReference());
    }

    public void setMKIconFit(PdfName pdfName, PdfName pdfName2, float f, float f2, boolean bl) {
        PdfDictionary pdfDictionary = new PdfDictionary();
        if (!pdfName.equals(PdfName.A)) {
            pdfDictionary.put(PdfName.SW, pdfName);
        }
        if (!pdfName2.equals(PdfName.P)) {
            pdfDictionary.put(PdfName.S, pdfName2);
        }
        if (f != 0.5f || f2 != 0.5f) {
            PdfArray pdfArray = new PdfArray(new PdfNumber(f));
            pdfArray.add(new PdfNumber(f2));
            pdfDictionary.put(PdfName.A, pdfArray);
        }
        if (bl) {
            pdfDictionary.put(PdfName.FB, PdfBoolean.PDFTRUE);
        }
        this.getMK().put(PdfName.IF, pdfDictionary);
    }

    public void setMKTextPosition(int n) {
        this.getMK().put(PdfName.TP, new PdfNumber(n));
    }

    public void setLayer(PdfOCG pdfOCG) {
        this.put(PdfName.OC, pdfOCG.getRef());
    }

    public void setName(String string) {
        this.put(PdfName.NM, new PdfString(string));
    }

    public static class PdfImportedLink {
        float llx;
        float lly;
        float urx;
        float ury;
        HashMap parameters = new HashMap();
        PdfArray destination = null;
        int newPage = 0;

        PdfImportedLink(PdfDictionary pdfDictionary) {
            this.parameters.putAll(pdfDictionary.hashMap);
            try {
                this.destination = (PdfArray)this.parameters.remove(PdfName.DEST);
            }
            catch (ClassCastException var2_2) {
                throw new IllegalArgumentException("You have to consolidate the named destinations of your reader.");
            }
            if (this.destination != null) {
                this.destination = new PdfArray(this.destination);
            }
            PdfArray pdfArray = (PdfArray)this.parameters.remove(PdfName.RECT);
            this.llx = pdfArray.getAsNumber(0).floatValue();
            this.lly = pdfArray.getAsNumber(1).floatValue();
            this.urx = pdfArray.getAsNumber(2).floatValue();
            this.ury = pdfArray.getAsNumber(3).floatValue();
        }

        public boolean isInternal() {
            return this.destination != null;
        }

        public int getDestinationPage() {
            if (!this.isInternal()) {
                return 0;
            }
            PdfIndirectReference pdfIndirectReference = this.destination.getAsIndirectObject(0);
            PRIndirectReference pRIndirectReference = (PRIndirectReference)pdfIndirectReference;
            PdfReader pdfReader = pRIndirectReference.getReader();
            for (int i = 1; i <= pdfReader.getNumberOfPages(); ++i) {
                PRIndirectReference pRIndirectReference2 = pdfReader.getPageOrigRef(i);
                if (pRIndirectReference2.getGeneration() != pRIndirectReference.getGeneration() || pRIndirectReference2.getNumber() != pRIndirectReference.getNumber()) continue;
                return i;
            }
            throw new IllegalArgumentException("Page not found.");
        }

        public void setDestinationPage(int n) {
            if (!this.isInternal()) {
                throw new IllegalArgumentException("Cannot change destination of external link");
            }
            this.newPage = n;
        }

        public void transformDestination(float f, float f2, float f3, float f4, float f5, float f6) {
            if (!this.isInternal()) {
                throw new IllegalArgumentException("Cannot change destination of external link");
            }
            if (this.destination.getAsName(1).equals(PdfName.XYZ)) {
                float f7 = this.destination.getAsNumber(2).floatValue();
                float f8 = this.destination.getAsNumber(3).floatValue();
                float f9 = f7 * f + f8 * f3 + f5;
                float f10 = f7 * f2 + f8 * f4 + f6;
                this.destination.getArrayList().set(2, new PdfNumber(f9));
                this.destination.getArrayList().set(3, new PdfNumber(f10));
            }
        }

        public void transformRect(float f, float f2, float f3, float f4, float f5, float f6) {
            float f7 = this.llx * f + this.lly * f3 + f5;
            float f8 = this.llx * f2 + this.lly * f4 + f6;
            this.llx = f7;
            this.lly = f8;
            f7 = this.urx * f + this.ury * f3 + f5;
            f8 = this.urx * f2 + this.ury * f4 + f6;
            this.urx = f7;
            this.ury = f8;
        }

        public PdfAnnotation createAnnotation(PdfWriter pdfWriter) {
            PdfAnnotation pdfAnnotation = new PdfAnnotation(pdfWriter, new Rectangle(this.llx, this.lly, this.urx, this.ury));
            if (this.newPage != 0) {
                PdfIndirectReference pdfIndirectReference = pdfWriter.getPageReference(this.newPage);
                this.destination.getArrayList().set(0, pdfIndirectReference);
            }
            if (this.destination != null) {
                pdfAnnotation.put(PdfName.DEST, this.destination);
            }
            pdfAnnotation.hashMap.putAll(this.parameters);
            return pdfAnnotation;
        }
    }

}

