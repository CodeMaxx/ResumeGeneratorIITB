/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;

public class PdfGState
extends PdfDictionary {
    public static final PdfName BM_NORMAL = new PdfName("Normal");
    public static final PdfName BM_COMPATIBLE = new PdfName("Compatible");
    public static final PdfName BM_MULTIPLY = new PdfName("Multiply");
    public static final PdfName BM_SCREEN = new PdfName("Screen");
    public static final PdfName BM_OVERLAY = new PdfName("Overlay");
    public static final PdfName BM_DARKEN = new PdfName("Darken");
    public static final PdfName BM_LIGHTEN = new PdfName("Lighten");
    public static final PdfName BM_COLORDODGE = new PdfName("ColorDodge");
    public static final PdfName BM_COLORBURN = new PdfName("ColorBurn");
    public static final PdfName BM_HARDLIGHT = new PdfName("HardLight");
    public static final PdfName BM_SOFTLIGHT = new PdfName("SoftLight");
    public static final PdfName BM_DIFFERENCE = new PdfName("Difference");
    public static final PdfName BM_EXCLUSION = new PdfName("Exclusion");

    public void setOverPrintStroking(boolean bl) {
        this.put(PdfName.OP, bl ? PdfBoolean.PDFTRUE : PdfBoolean.PDFFALSE);
    }

    public void setOverPrintNonStroking(boolean bl) {
        this.put(PdfName.op, bl ? PdfBoolean.PDFTRUE : PdfBoolean.PDFFALSE);
    }

    public void setOverPrintMode(int n) {
        this.put(PdfName.OPM, new PdfNumber(n == 0 ? 0 : 1));
    }

    public void setStrokeOpacity(float f) {
        this.put(PdfName.CA, new PdfNumber(f));
    }

    public void setFillOpacity(float f) {
        this.put(PdfName.ca, new PdfNumber(f));
    }

    public void setAlphaIsShape(boolean bl) {
        this.put(PdfName.AIS, bl ? PdfBoolean.PDFTRUE : PdfBoolean.PDFFALSE);
    }

    public void setTextKnockout(boolean bl) {
        this.put(PdfName.TK, bl ? PdfBoolean.PDFTRUE : PdfBoolean.PDFFALSE);
    }

    public void setBlendMode(PdfName pdfName) {
        this.put(PdfName.BM, pdfName);
    }
}

